package com.eudhari.model;

import javafx.beans.property.*;
import java.util.HashMap;
import java.util.Map;

public class ShopModel {
    private final StringProperty shopId;
    private final StringProperty shopName;
    private final StringProperty ownerId;
    private final StringProperty ownerName;
    private final StringProperty address;
    private final StringProperty businessCategory;
    private final StringProperty gpayId;
    private final StringProperty status;
    private final StringProperty createdAt;

    public ShopModel(String shopId, String shopName, String ownerId, String ownerName, String address, String businessCategory, String gpayId, String status, String createdAt) {
        this.shopId = new SimpleStringProperty(shopId);
        this.shopName = new SimpleStringProperty(shopName);
        this.ownerId = new SimpleStringProperty(ownerId);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.address = new SimpleStringProperty(address);
        this.businessCategory = new SimpleStringProperty(businessCategory);
        this.gpayId = new SimpleStringProperty(gpayId);
        this.status = new SimpleStringProperty(status);
        this.createdAt = new SimpleStringProperty(createdAt);
    }

    public ShopModel(String shopId, String shopName, String address, String ownerName, String gpayId, String businessCategory, String status) {
        this(shopId, shopName, "", ownerName, address, businessCategory, gpayId, status, "");
    }

    public String getShopId() { return shopId.get(); }
    public void setShopId(String value) { this.shopId.set(value); }
    public StringProperty shopIdProperty() { return shopId; }

    public String getShopName() { return shopName.get(); }
    public void setShopName(String value) { this.shopName.set(value); }
    public StringProperty shopNameProperty() { return shopName; }

    public String getOwnerId() { return ownerId.get(); }
    public void setOwnerId(String value) { this.ownerId.set(value); }
    public StringProperty ownerIdProperty() { return ownerId; }

    public String getOwnerName() { return ownerName.get(); }
    public void setOwnerName(String value) { this.ownerName.set(value); }
    public StringProperty ownerNameProperty() { return ownerName; }

    public String getAddress() { return address.get(); }
    public void setAddress(String value) { this.address.set(value); }
    public StringProperty addressProperty() { return address; }

    public String getBusinessCategory() { return businessCategory.get(); }
    public void setBusinessCategory(String value) { this.businessCategory.set(value); }
    public StringProperty businessCategoryProperty() { return businessCategory; }

    public String getGpayId() { return gpayId.get(); }
    public void setGpayId(String value) { this.gpayId.set(value); }
    public StringProperty gpayIdProperty() { return gpayId; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { this.status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String value) { this.createdAt.set(value); }
    public StringProperty createdAtProperty() { return createdAt; }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", getShopId());
        map.put("shopName", getShopName());
        map.put("ownerId", getOwnerId());
        map.put("ownerName", getOwnerName());
        map.put("address", getAddress());
        map.put("businessCategory", getBusinessCategory());
        map.put("gpayId", getGpayId());
        map.put("status", getStatus());
        map.put("createdAt", getCreatedAt());
        return map;
    }

    public static ShopModel fromDocumentMap(String documentId, Map<String, Object> map) {
        String sId = map.containsKey("shopId") && map.get("shopId") != null ? map.get("shopId").toString() : documentId;
        String sName = map.containsKey("shopName") && map.get("shopName") != null ? map.get("shopName").toString() : "";
        String oId = map.containsKey("ownerId") && map.get("ownerId") != null ? map.get("ownerId").toString() : "";
        String oName = map.containsKey("ownerName") && map.get("ownerName") != null ? map.get("ownerName").toString() : "";
        String addr = map.containsKey("address") && map.get("address") != null ? map.get("address").toString() : "";
        String bCat = map.containsKey("businessCategory") && map.get("businessCategory") != null ? map.get("businessCategory").toString() : "";
        String gpay = map.containsKey("gpayId") && map.get("gpayId") != null ? map.get("gpayId").toString() : "";
        String st = map.containsKey("status") && map.get("status") != null ? map.get("status").toString() : "ACTIVE";
        String cAt = map.containsKey("createdAt") && map.get("createdAt") != null ? map.get("createdAt").toString() : "";

        return new ShopModel(sId, sName, oId, oName, addr, bCat, gpay, st, cAt);
    }
}
