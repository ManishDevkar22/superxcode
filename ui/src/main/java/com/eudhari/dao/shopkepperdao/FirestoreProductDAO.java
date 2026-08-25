package com.eudhari.dao.shopkepperdao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.shopkeppermodel.ProductModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firestore DAO implementation for Products.
 * Interacts with the "products" collection in Firestore.
 */
public class FirestoreProductDAO implements ProductDAO {
    private static final String COLLECTION_NAME = "products";

    public FirestoreProductDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public List<ProductModel> getAllProducts() {
        List<ProductModel> productList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                productList.add(documentToProduct(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error fetching all products: " + e.getMessage());
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public ProductModel getProductById(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(id.trim()).get().get();
            if (doc.exists()) {
                return documentToProduct(doc);
            }
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error fetching product by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveProduct(ProductModel product) {
        if (product == null || product.getId() == null)
            return;
        try {
            Map<String, Object> docData = toDocumentMap(product);
            getDb().collection(COLLECTION_NAME).document(product.getId()).set(docData).get();
            System.out
                    .println("[FirestoreDAO] Saved product to Firestore '" + COLLECTION_NAME + "': " + product.getId());
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error saving product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateProduct(ProductModel product) {
        if (product == null || product.getId() == null)
            return;
        try {
            Map<String, Object> docData = toDocumentMap(product);
            getDb().collection(COLLECTION_NAME).document(product.getId()).set(docData, SetOptions.merge()).get();
            System.out.println(
                    "[FirestoreDAO] Updated product in Firestore '" + COLLECTION_NAME + "': " + product.getId());
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(String id) {
        if (id == null || id.trim().isEmpty())
            return;
        try {
            getDb().collection(COLLECTION_NAME).document(id.trim()).delete().get();
            System.out.println("[FirestoreDAO] Permanently deleted product ID '" + id + "' from collection '"
                    + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error deleting product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void restoreProduct(String id) {
        if (id == null || id.trim().isEmpty())
            return;
        try {
            getDb().collection(COLLECTION_NAME).document(id.trim()).update("deleted", false).get();
            System.out
                    .println("[FirestoreDAO] Restored product ID '" + id + "' in collection '" + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error restoring product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ProductModel> getProductsByShopId(String shopId) {
        List<ProductModel> productList = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return productList;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                productList.add(documentToProduct(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreProductDAO] Error fetching products for shop " + shopId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return productList;
    }

    private ProductModel documentToProduct(DocumentSnapshot doc) {
        String id = doc.getString("productId");
        if (id == null || id.isEmpty()) {
            id = doc.getString("id");
        }
        if (id == null || id.isEmpty()) {
            id = doc.getId();
        }
        String shopId = doc.getString("shopId");
        String name = doc.getString("name");
        String category = doc.getString("category");

        Double priceVal = doc.getDouble("price");
        double price = priceVal != null ? priceVal : 0.0;

        String unit = doc.getString("unit");

        Double stockVal = doc.getDouble("stockQuantity");
        if (stockVal == null) {
            stockVal = doc.getDouble("stock");
        }
        double stock = stockVal != null ? stockVal : 0.0;

        String image = doc.getString("image");
        String imageUrl = doc.getString("imageUrl");

        Boolean deletedVal = doc.getBoolean("deleted");
        boolean deleted = deletedVal != null ? deletedVal : false;

        String createdAt = doc.getString("createdAt");
        String updatedAt = doc.getString("updatedAt");

        Long salesCountVal = doc.getLong("salesCount");
        int salesCount = salesCountVal != null ? salesCountVal.intValue() : 0;

        ProductModel p = new ProductModel(id, name != null ? name : "", category != null ? category : "", price,
                unit != null ? unit : "", stock, image != null ? image : "", imageUrl != null ? imageUrl : "",
                "Shopkeeper", "Available", deleted, createdAt != null ? createdAt : "", updatedAt != null ? updatedAt : "", salesCount);
        if (shopId != null) p.setShopId(shopId);
        return p;
    }

    private Map<String, Object> toDocumentMap(ProductModel p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("productId", p.getId());
        map.put("shopId", p.getShopId());
        map.put("name", p.getName());
        map.put("category", p.getCategory());
        map.put("price", p.getPrice());
        map.put("unit", p.getUnit());
        map.put("stock", p.getStock());
        map.put("stockQuantity", p.getStock());
        map.put("salesCount", p.getSalesCount());
        map.put("image", p.getImage());
        map.put("imageUrl", p.getImageUrl());
        map.put("deleted", p.isDeleted());

        String now = java.time.Instant.now().toString();
        map.put("createdAt", p.getCreatedAt() != null && !p.getCreatedAt().isEmpty() ? p.getCreatedAt() : now);
        map.put("updatedAt", now);
        return map;
    }
}
