package com.eudhari.model.shopkeppermodel;

import com.eudhari.config.UserSession;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.dao.shopkepperdao.CategoryDAO;
import com.eudhari.dao.shopkepperdao.DAOFactory;
import com.eudhari.dao.shopkepperdao.ProductDAO;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.ArrayList;
import java.util.List;

public class ProductStore {
    public static final double MAX_STORAGE_CAPACITY_KG = 500.0;
    private static ProductStore instance;

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final ObservableList<ProductModel> products = FXCollections.observableArrayList();
    private final ObservableList<String> availableCategories = FXCollections.observableArrayList("Grocery", "Dairy", "Bakery");
    private final FilteredList<ProductModel> activeProducts;
    private final FilteredList<ProductModel> deletedProducts;

    private ProductStore() {
        this.productDAO = DAOFactory.getProductDAO();
        this.categoryDAO = DAOFactory.getCategoryDAO();
        activeProducts = new FilteredList<>(products, p -> !p.isDeleted());
        deletedProducts = new FilteredList<>(products, ProductModel::isDeleted);

        // Load existing products and custom categories from Firestore DAO
        loadFromDAO();
    }

    public static synchronized ProductStore getInstance() {
        if (instance == null) {
            instance = new ProductStore();
        }
        return instance;
    }

    public void loadFromDAO() {
        new Thread(() -> {
            UserModel user = UserSession.getInstance().getCurrentUser();
            List<ProductModel> fetchedProducts = new ArrayList<>();
            List<String> fetchedCategories = new ArrayList<>();

            if (user != null && "shopkeeper".equalsIgnoreCase(user.getRole())) {
                ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
                if (shop != null && shop.getShopId() != null && !shop.getShopId().isEmpty()) {
                    String shopId = shop.getShopId();
                    fetchedProducts.addAll(productDAO.getProductsByShopId(shopId));
                    List<CategoryModel> customCats = categoryDAO.getCategoriesByShopId(shopId);
                    for (CategoryModel cm : customCats) {
                        if (cm != null && cm.getCategoryName() != null && !cm.getCategoryName().isBlank()) {
                            fetchedCategories.add(cm.getCategoryName().trim());
                        }
                    }
                }
            } else if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                fetchedProducts.addAll(productDAO.getAllProducts());
            }

            final List<ProductModel> productResultList = fetchedProducts;
            final List<String> categoryResultList = fetchedCategories;

            javafx.application.Platform.runLater(() -> {
                products.clear();
                products.addAll(productResultList);

                availableCategories.clear();
                availableCategories.addAll("Grocery", "Dairy", "Bakery");
                for (String cat : categoryResultList) {
                    if (availableCategories.stream().noneMatch(c -> c.equalsIgnoreCase(cat))) {
                        availableCategories.add(cat);
                    }
                }
                for (ProductModel p : productResultList) {
                    if (p.getCategory() != null && !p.getCategory().isBlank()) {
                        String catName = p.getCategory().trim();
                        if (availableCategories.stream().noneMatch(c -> c.equalsIgnoreCase(catName))) {
                            availableCategories.add(catName);
                        }
                    }
                }
            });
        }).start();
    }

    public ObservableList<ProductModel> getAllProducts() {
        return products;
    }

    public FilteredList<ProductModel> getActiveProducts() {
        return activeProducts;
    }

    public FilteredList<ProductModel> getDeletedProducts() {
        return deletedProducts;
    }

    public ObservableList<String> getAvailableCategories() {
        return availableCategories;
    }

    public void addCategory(String category) {
        if (category != null && !category.isBlank()) {
            String trimmed = category.trim();
            if (!trimmed.equalsIgnoreCase("Other") && !trimmed.equalsIgnoreCase("All")) {
                boolean exists = availableCategories.stream().anyMatch(c -> c.equalsIgnoreCase(trimmed));
                if (!exists) {
                    availableCategories.add(trimmed);
                }
                new Thread(() -> {
                    UserModel user = UserSession.getInstance().getCurrentUser();
                    if (user != null && "shopkeeper".equalsIgnoreCase(user.getRole())) {
                        ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
                        if (shop != null && shop.getShopId() != null && !shop.getShopId().isEmpty()) {
                            String shopId = shop.getShopId();
                            String catId = "CAT_" + System.currentTimeMillis();
                            categoryDAO.saveCategory(new CategoryModel(catId, trimmed, shopId));
                        }
                    }
                }).start();
            }
        }
    }

    public void removeCategory(String category) {
        if (category != null) {
            String trimmed = category.trim();
            availableCategories.removeIf(c -> c.equalsIgnoreCase(trimmed));
            new Thread(() -> {
                UserModel user = UserSession.getInstance().getCurrentUser();
                if (user != null && "shopkeeper".equalsIgnoreCase(user.getRole())) {
                    ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
                    if (shop != null && shop.getShopId() != null && !shop.getShopId().isEmpty()) {
                        categoryDAO.deleteCategory(shop.getShopId(), trimmed);
                    }
                }
            }).start();
        }
    }

    // Compatibility methods
    public ObservableList<String> getCustomCategories() {
        return availableCategories;
    }

    public void addCustomCategory(String category) {
        addCategory(category);
    }

    public void removeCustomCategory(String category) {
        removeCategory(category);
    }

    public void addProduct(String name, String category, double price, String unit, double stock, String image,
            String imageUrl) {
        String id = "P-" + (100 + products.size() + 1);
        ProductModel p = new ProductModel(id, name, category, price, unit, stock, image, imageUrl, false);
        UserModel user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
            if (shop != null && shop.getShopId() != null) {
                p.setShopId(shop.getShopId());
            }
        }
        products.add(p);
        productDAO.saveProduct(p);
    }

    public void addProduct(String name, String category, double price, String unit, double stock, String image) {
        addProduct(name, category, price, unit, stock, image, "");
    }

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
            productDAO.updateProduct(product);
        }
    }

    public void updateProduct(ProductModel product, String name, String category, double price, String unit,
            double stock, String image) {
        updateProduct(product, name, category, price, unit, stock, image, product != null ? product.getImageUrl() : "");
    }

    public void deleteProduct(ProductModel product) {
        if (product != null) {
            products.remove(product);
            productDAO.deleteProduct(product.getId());
        }
    }

    public void restoreProduct(ProductModel product) {
        if (product != null) {
            product.setDeleted(false);
            productDAO.restoreProduct(product.getId());
        }
    }

    public double getTotalStockKg() {
        double totalKg = 0;
        for (ProductModel p : activeProducts) {
            totalKg += p.getWeightInKg();
        }
        return totalKg;
    }

    public double getStorageUsagePercentage() {
        return Math.min(100.0, (getTotalStockKg() / MAX_STORAGE_CAPACITY_KG) * 100.0);
    }
}
