package com.eudhari.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String uid;
    private String userCode;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role; // customer / shopkeeper / admin
    private String createdAt;
    private String status = "ACTIVE"; // ACTIVE / INACTIVE

    // Shopkeeper specific optional fields
    private String ownerName;
    private String shopName;
    private String shopAddress;
    private String gpayId;
    private String businessCategory;
    private String storeImagePath;

    public UserModel() {}

    public UserModel(String uid, String name, String email, String phone, String role, String createdAt) {
        this(uid, null, name, email, phone, role, createdAt);
    }

    public UserModel(String uid, String userCode, String name, String email, String phone, String role, String createdAt) {
        this.uid = uid;
        this.userCode = userCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        if (address != null && !address.trim().isEmpty()) {
            return address;
        }
        return shopAddress != null ? shopAddress : "";
    }

    public void setAddress(String address) {
        this.address = address;
        this.shopAddress = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return getAddress();
    }

    public void setShopAddress(String shopAddress) {
        setAddress(shopAddress);
    }

    public String getGpayId() {
        return gpayId;
    }

    public void setGpayId(String gpayId) {
        this.gpayId = gpayId;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getStoreImagePath() {
        return storeImagePath;
    }

    public void setStoreImagePath(String storeImagePath) {
        this.storeImagePath = storeImagePath;
    }

    public String getStatus() {
        return status != null ? status : "ACTIVE";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        if (userCode != null) map.put("userCode", userCode);
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        if (getAddress() != null) map.put("address", getAddress());
        map.put("role", role);
        map.put("createdAt", createdAt);
        map.put("status", getStatus());

        if (ownerName != null) map.put("ownerName", ownerName);
        if (shopName != null) map.put("shopName", shopName);
        if (shopAddress != null) map.put("shopAddress", shopAddress);
        if (gpayId != null) map.put("gpayId", gpayId);
        if (businessCategory != null) map.put("businessCategory", businessCategory);
        if (storeImagePath != null) map.put("storeImagePath", storeImagePath);

        return map;
    }

    public static UserModel fromDocumentMap(String documentId, Map<String, Object> map) {
        UserModel user = new UserModel();
        user.setUid(map.containsKey("uid") && map.get("uid") != null ? map.get("uid").toString() : documentId);
        if (map.containsKey("userCode") && map.get("userCode") != null) user.setUserCode(map.get("userCode").toString());
        user.setName(map.containsKey("name") && map.get("name") != null ? map.get("name").toString() : "");
        user.setEmail(map.containsKey("email") && map.get("email") != null ? map.get("email").toString() : "");
        user.setPhone(map.containsKey("phone") && map.get("phone") != null ? map.get("phone").toString() : "");
        if (map.containsKey("address") && map.get("address") != null) {
            user.setAddress(map.get("address").toString());
        } else if (map.containsKey("shopAddress") && map.get("shopAddress") != null) {
            user.setAddress(map.get("shopAddress").toString());
        }
        user.setRole(map.containsKey("role") && map.get("role") != null ? map.get("role").toString() : "");
        user.setCreatedAt(map.containsKey("createdAt") && map.get("createdAt") != null ? map.get("createdAt").toString() : "");
        if (map.containsKey("status") && map.get("status") != null) user.setStatus(map.get("status").toString());

        if (map.containsKey("ownerName") && map.get("ownerName") != null) user.setOwnerName(map.get("ownerName").toString());
        if (map.containsKey("shopName") && map.get("shopName") != null) user.setShopName(map.get("shopName").toString());
        if (map.containsKey("gpayId") && map.get("gpayId") != null) user.setGpayId(map.get("gpayId").toString());
        if (map.containsKey("businessCategory") && map.get("businessCategory") != null) user.setBusinessCategory(map.get("businessCategory").toString());
        if (map.containsKey("storeImagePath") && map.get("storeImagePath") != null) user.setStoreImagePath(map.get("storeImagePath").toString());

        return user;
    }
}
