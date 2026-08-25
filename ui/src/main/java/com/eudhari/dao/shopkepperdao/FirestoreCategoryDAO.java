package com.eudhari.dao.shopkepperdao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.shopkeppermodel.CategoryModel;
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
 * Firestore DAO implementation for Categories.
 * Interacts with the "categories" collection in Firestore.
 */
public class FirestoreCategoryDAO implements CategoryDAO {
    private static final String COLLECTION_NAME = "categories";

    public FirestoreCategoryDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public List<CategoryModel> getCategoriesByShopId(String shopId) {
        List<CategoryModel> list = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return list;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                String catId = doc.getString("categoryId");
                if (catId == null || catId.isEmpty()) {
                    catId = doc.getId();
                }
                String catName = doc.getString("categoryName");
                String sId = doc.getString("shopId");
                if (catName != null && !catName.isBlank()) {
                    list.add(new CategoryModel(catId, catName.trim(), sId != null ? sId : shopId));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreCategoryDAO] Error fetching categories for shop " + shopId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void saveCategory(CategoryModel category) {
        if (category == null || category.getCategoryName() == null || category.getCategoryName().isBlank()
                || category.getShopId() == null || category.getShopId().isBlank()) {
            return;
        }
        try {
            String docId = category.getCategoryId();
            if (docId == null || docId.isBlank()) {
                docId = "CAT_" + System.currentTimeMillis();
                category.setCategoryId(docId);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("categoryId", docId);
            map.put("categoryName", category.getCategoryName().trim());
            map.put("shopId", category.getShopId().trim());
            map.put("createdAt", java.time.Instant.now().toString());

            getDb().collection(COLLECTION_NAME).document(docId).set(map, SetOptions.merge()).get();
            System.out.println("[FirestoreCategoryDAO] Saved category '" + category.getCategoryName() + "' for shop '" + category.getShopId() + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreCategoryDAO] Error saving category: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCategory(String shopId, String categoryName) {
        if (shopId == null || shopId.isBlank() || categoryName == null || categoryName.isBlank()) {
            return;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                String name = doc.getString("categoryName");
                if (name != null && name.trim().equalsIgnoreCase(categoryName.trim())) {
                    getDb().collection(COLLECTION_NAME).document(doc.getId()).delete().get();
                    System.out.println("[FirestoreCategoryDAO] Deleted category document '" + doc.getId() + "' (" + categoryName + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreCategoryDAO] Error deleting category: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
