package com.eudhari.model.shopkeppermodel;

import javafx.beans.property.*;

public class ProductModel {
    private final StringProperty id;
    private final StringProperty shopId;
    private final StringProperty name;
    private final StringProperty category;
    private final DoubleProperty price;
    private final StringProperty unit;
    private final DoubleProperty stock;
    private final StringProperty image;
    private final StringProperty imageUrl;
    private final StringProperty shopkeeper;
    private final StringProperty status;
    private final BooleanProperty deleted;
    private final StringProperty createdAt;
    private final StringProperty updatedAt;
    private final IntegerProperty salesCount;

    public ProductModel(String id, String name, String category, double price, String unit, double stock,
            String image) {
        this(id, name, category, price, unit, stock, image, "", "", "Available", false);
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            String imageUrl) {
        this(id, name, category, price, unit, stock, image, imageUrl, "", "Available", false);
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            boolean deleted) {
        this(id, name, category, price, unit, stock, image, "", "", "Available", deleted);
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            String imageUrl, boolean deleted) {
        this(id, name, category, price, unit, stock, image, imageUrl, "", "Available", deleted);
    }

    public ProductModel(String id, String name, String category, String shopkeeper, String priceStr, double stock,
            String status) {
        this(id, name, category, parseDouble(priceStr), "kg", stock, "", "", shopkeeper, status, false);
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            String imageUrl, String shopkeeper, String status, boolean deleted) {
        this(id, name, category, price, unit, stock, image, imageUrl, shopkeeper, status, deleted, "", "");
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            String imageUrl, String shopkeeper, String status, boolean deleted, String createdAt, String updatedAt) {
        this(id, name, category, price, unit, stock, image, imageUrl, shopkeeper, status, deleted, createdAt, updatedAt, 0);
    }

    public ProductModel(String id, String name, String category, double price, String unit, double stock, String image,
            String imageUrl, String shopkeeper, String status, boolean deleted, String createdAt, String updatedAt, int salesCount) {
        this.id = new SimpleStringProperty(id);
        this.shopId = new SimpleStringProperty("");
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
        this.unit = new SimpleStringProperty(unit != null ? unit : "kg");
        this.stock = new SimpleDoubleProperty(stock);
        this.image = new SimpleStringProperty(image != null ? image : "");
        this.imageUrl = new SimpleStringProperty(imageUrl != null ? imageUrl : "");
        this.shopkeeper = new SimpleStringProperty(shopkeeper != null ? shopkeeper : "");
        this.status = new SimpleStringProperty(status != null ? status : "Available");
        this.deleted = new SimpleBooleanProperty(deleted);
        this.createdAt = new SimpleStringProperty(createdAt != null ? createdAt : "");
        this.updatedAt = new SimpleStringProperty(updatedAt != null ? updatedAt : "");
        this.salesCount = new SimpleIntegerProperty(salesCount);
    }

    public String getShopId() { return shopId.get(); }
    public void setShopId(String v) { this.shopId.set(v != null ? v : ""); }
    public StringProperty shopIdProperty() { return shopId; }

    public double getStockQuantity() { return getStock(); }
    public void setStockQuantity(double qty) { setStock(qty); }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String v) { this.createdAt.set(v != null ? v : ""); }
    public StringProperty createdAtProperty() { return createdAt; }

    public String getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(String v) { this.updatedAt.set(v != null ? v : ""); }
    public StringProperty updatedAtProperty() { return updatedAt; }

    private static double parseDouble(String str) {
        if (str == null)
            return 0.0;
        try {
            return Double.parseDouble(str.replace("Rs", "").replace("₹", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getId() {
        return id.get();
    }

    public String getProductId() {
        return getId();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public String getProductName() {
        return getName();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setProductName(String name) {
        setName(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public double getPrice() {
        return price.get();
    }

    public String getPriceAsString() {
        return String.format("Rs %.2f", price.get());
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setPrice(String priceStr) {
        this.price.set(parseDouble(priceStr));
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public double getStock() {
        return stock.get();
    }

    public void setStock(double stock) {
        this.stock.set(stock);
    }

    public DoubleProperty stockProperty() {
        return stock;
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public StringProperty imageProperty() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl != null ? imageUrl : "");
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    public String getShopkeeper() {
        return shopkeeper.get();
    }

    public void setShopkeeper(String shopkeeper) {
        this.shopkeeper.set(shopkeeper);
    }

    public StringProperty shopkeeperProperty() {
        return shopkeeper;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public boolean isDeleted() {
        return deleted.get();
    }

    public void setDeleted(boolean deleted) {
        this.deleted.set(deleted);
    }

    public BooleanProperty deletedProperty() {
        return deleted;
    }

    public String getFormattedPrice() {
        return String.format("Rs %.2f / %s", getPrice(), getUnit());
    }

    public String getFormattedStock() {
        if (getStock() == (long) getStock()) {
            return String.format("%d %s", (long) getStock(), getUnit());
        }
        return String.format("%.1f %s", getStock(), getUnit());
    }

    public double getWeightInKg() {
        double s = getStock();
        String u = getUnit().toLowerCase();
        if (u.contains("kg"))
            return s;
        if (u.contains("g") && !u.contains("kg"))
            return (s * 0.25);
        if (u.contains("l") || u.contains("litre") || u.contains("liter"))
            return s;
        return s * 0.5;
    }

    public int getSalesCount() {
        return salesCount != null ? salesCount.get() : 0;
    }

    public void setSalesCount(int count) {
        if (salesCount != null) {
            salesCount.set(Math.max(0, count));
        }
    }

    public IntegerProperty salesCountProperty() {
        return salesCount;
    }
}
