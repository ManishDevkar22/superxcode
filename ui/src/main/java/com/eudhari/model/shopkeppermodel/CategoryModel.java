package com.eudhari.model.shopkeppermodel;

public class CategoryModel {
    private String categoryId;
    private String categoryName;
    private String shopId;

    public CategoryModel() {
    }

    public CategoryModel(String categoryId, String categoryName, String shopId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.shopId = shopId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
