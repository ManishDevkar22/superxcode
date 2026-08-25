package com.eudhari.controller.shopkeppercontroller;

import com.eudhari.dao.shopkepperdao.*;
// import com.eudhari.dao.ProductDAO;
import com.eudhari.model.shopkeppermodel.*;
// import com.eudhari.model.ProductStore;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ProductController {
    private static ProductController instance;
    private final ProductDAO productDAO;
    private final ProductStore productStore;

    private ProductController() {
        this.productDAO = DAOFactory.getProductDAO();
        this.productStore = ProductStore.getInstance();
    }

    public static synchronized ProductController getInstance() {
        if (instance == null) {
            instance = new ProductController();
        }
        return instance;
    }

    public ObservableList<ProductModel> getAllProducts() {
        return productStore.getAllProducts();
    }

    public FilteredList<ProductModel> getActiveProducts() {
        return productStore.getActiveProducts();
    }

    public FilteredList<ProductModel> getDeletedProducts() {
        return productStore.getDeletedProducts();
    }

    public ObservableList<String> getAvailableCategories() {
        return productStore.getAvailableCategories();
    }

    public void addCategory(String category) {
        productStore.addCategory(category);
    }

    public void removeCategory(String category) {
        productStore.removeCategory(category);
    }

    public ObservableList<String> getCustomCategories() {
        return productStore.getCustomCategories();
    }

    public void addCustomCategory(String category) {
        productStore.addCustomCategory(category);
    }

    public void removeCustomCategory(String category) {
        productStore.removeCustomCategory(category);
    }

    public java.util.List<ProductModel> getProductsByShopId(String shopId) {
        return productDAO.getProductsByShopId(shopId);
    }

    /**
     * View -> Controller -> Model -> DAO -> Firestore flow for adding product.
     */
    public void addProduct(String name, String category, double price, String unit, double stock, String image,
            String imageUrl) {
        String id = "P-" + (100 + productStore.getAllProducts().size() + 1);
        ProductModel p = new ProductModel(id, name, category, price, unit, stock, image, imageUrl, false);

        // Resolve current shopkeeper's shopId automatically
        try {
            com.eudhari.model.UserModel currentUser = com.eudhari.config.UserSession.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getUid() != null) {
                com.eudhari.model.ShopModel shop = ShopController.getInstance().getShopByOwnerId(currentUser.getUid());
                if (shop != null && shop.getShopId() != null) {
                    p.setShopId(shop.getShopId());
                }
            }
        } catch (Exception ignored) {}

        // 1. Update in-memory store (Model)
        productStore.getAllProducts().add(p);

        // 2. Persist in Firestore via DAO
        productDAO.saveProduct(p);
    }

    public void addProduct(String name, String category, double price, String unit, double stock, String image) {
        addProduct(name, category, price, unit, stock, image, "");
    }

    /**
     * View -> Controller -> Model -> DAO -> Firestore flow for updating product.
     */
    public void updateProduct(ProductModel product, String name, String category, double price, String unit,
            double stock, String image, String imageUrl) {
        if (product != null) {
            product.setName(name);
            product.setCategory(category);
            product.setPrice(price);
            product.setUnit(unit);
            product.setStock(stock);
            product.setImage(image);
            product.setImageUrl(imageUrl);

            // Persist update in Firestore via DAO
            productDAO.updateProduct(product);
        }
    }

    public void updateProduct(ProductModel product, String name, String category, double price, String unit,
            double stock, String image) {
        updateProduct(product, name, category, price, unit, stock, image, product != null ? product.getImageUrl() : "");
    }

    /**
     * View -> Controller -> Model -> DAO -> Firestore flow for stock adjustment.
     */
    public void addStock(ProductModel product, double additionalQty) {
        if (product != null && additionalQty > 0) {
            product.setStock(product.getStock() + additionalQty);

            // Persist update in Firestore via DAO
            productDAO.updateProduct(product);
        }
    }

    /**
     * View -> Controller -> Model -> DAO -> Firestore flow for deleting product.
     */
    public void deleteProduct(ProductModel product) {
        if (product != null) {
            productStore.getAllProducts().remove(product);
            productDAO.deleteProduct(product.getId());
        }
    }

    /**
     * View -> Controller -> Model -> DAO -> Firestore flow for restoring product.
     */
    public void restoreProduct(ProductModel product) {
        if (product != null) {
            product.setDeleted(false);

            // Restore in Firestore via DAO
            productDAO.restoreProduct(product.getId());
        }
    }

    public double getTotalStockKg() {
        return productStore.getTotalStockKg();
    }

    public double getStorageUsagePercentage() {
        return productStore.getStorageUsagePercentage();
    }
}
