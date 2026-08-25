package com.eudhari.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRequestModel {
    private final StringProperty requestId;
    private final StringProperty customerId;
    private final StringProperty customerName;
    private final StringProperty shopId;
    private final StringProperty shopName;
    private final StringProperty shopkeeperId;
    private final StringProperty status;
    private final StringProperty requestedAt;
    private final StringProperty updatedAt;

    public ConnectionRequestModel(String requestId, String customerId, String customerName, String shopId, String shopName, String shopkeeperId, String status, String requestedAt, String updatedAt) {
        this.requestId = new SimpleStringProperty(requestId);
        this.customerId = new SimpleStringProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.shopId = new SimpleStringProperty(shopId);
        this.shopName = new SimpleStringProperty(shopName);
        this.shopkeeperId = new SimpleStringProperty(shopkeeperId);
        this.status = new SimpleStringProperty(status);
        this.requestedAt = new SimpleStringProperty(requestedAt);
        this.updatedAt = new SimpleStringProperty(updatedAt);
    }

    public String getRequestId() { return requestId.get(); }
    public void setRequestId(String v) { this.requestId.set(v); }
    public StringProperty requestIdProperty() { return requestId; }

    public String getCustomerId() { return customerId.get(); }
    public void setCustomerId(String v) { this.customerId.set(v); }
    public StringProperty customerIdProperty() { return customerId; }

    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String v) { this.customerName.set(v); }
    public StringProperty customerNameProperty() { return customerName; }

    public String getShopId() { return shopId.get(); }
    public void setShopId(String v) { this.shopId.set(v); }
    public StringProperty shopIdProperty() { return shopId; }

    public String getShopName() { return shopName.get(); }
    public void setShopName(String v) { this.shopName.set(v); }
    public StringProperty shopNameProperty() { return shopName; }

    public String getShopkeeperId() { return shopkeeperId.get(); }
    public void setShopkeeperId(String v) { this.shopkeeperId.set(v); }
    public StringProperty shopkeeperIdProperty() { return shopkeeperId; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }

    public String getRequestedAt() { return requestedAt.get(); }
    public void setRequestedAt(String v) { this.requestedAt.set(v); }
    public StringProperty requestedAtProperty() { return requestedAt; }

    public String getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(String v) { this.updatedAt.set(v); }
    public StringProperty updatedAtProperty() { return updatedAt; }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", getRequestId());
        map.put("customerId", getCustomerId());
        map.put("customerName", getCustomerName());
        map.put("shopId", getShopId());
        map.put("shopName", getShopName());
        map.put("shopkeeperId", getShopkeeperId());
        map.put("status", getStatus());
        map.put("requestedAt", getRequestedAt());
        map.put("updatedAt", getUpdatedAt());
        return map;
    }

    public static ConnectionRequestModel fromDocumentMap(String documentId, Map<String, Object> map) {
        String reqId = map.containsKey("requestId") && map.get("requestId") != null ? map.get("requestId").toString() : documentId;
        String cId = map.containsKey("customerId") && map.get("customerId") != null ? map.get("customerId").toString() : "";
        String cName = map.containsKey("customerName") && map.get("customerName") != null ? map.get("customerName").toString() : "";
        String sId = map.containsKey("shopId") && map.get("shopId") != null ? map.get("shopId").toString() : "";
        String sName = map.containsKey("shopName") && map.get("shopName") != null ? map.get("shopName").toString() : "";
        String skId = map.containsKey("shopkeeperId") && map.get("shopkeeperId") != null ? map.get("shopkeeperId").toString() : "";
        String st = map.containsKey("status") && map.get("status") != null ? map.get("status").toString() : "PENDING";
        String rAt = map.containsKey("requestedAt") && map.get("requestedAt") != null ? map.get("requestedAt").toString() : "";
        String uAt = map.containsKey("updatedAt") && map.get("updatedAt") != null ? map.get("updatedAt").toString() : "";

        return new ConnectionRequestModel(reqId, cId, cName, sId, sName, skId, st, rAt, uAt);
    }
}
