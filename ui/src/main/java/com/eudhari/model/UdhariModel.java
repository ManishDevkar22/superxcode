package com.eudhari.model;

import java.util.HashMap;
import java.util.Map;

public class UdhariModel {
    private String udhariId;
    private String billingId;
    private String orderId;
    private String customerId;
    private String customerName;
    private String shopId;
    private String shopName;
    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;
    private String status; // PENDING, PARTIALLY_PAID, PAID
    private String createdAt;
    private String updatedAt;

    public UdhariModel() {}

    public UdhariModel(String udhariId, String billingId, String orderId, String customerId, String customerName,
                       String shopId, String shopName, double totalAmount, double paidAmount, double remainingAmount,
                       String status, String createdAt, String updatedAt) {
        this.udhariId = udhariId != null ? udhariId : "";
        this.billingId = billingId != null ? billingId : "";
        this.orderId = orderId != null ? orderId : "";
        this.customerId = customerId != null ? customerId : "";
        this.customerName = customerName != null ? customerName : "";
        this.shopId = shopId != null ? shopId : "";
        this.shopName = shopName != null ? shopName : "";
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.status = status != null ? status : "PENDING";
        this.createdAt = createdAt != null ? createdAt : "";
        this.updatedAt = updatedAt != null ? updatedAt : "";
    }

    public String getUdhariId() {
        return udhariId;
    }

    public void setUdhariId(String udhariId) {
        this.udhariId = udhariId != null ? udhariId : "";
    }

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId != null ? billingId : "";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId != null ? orderId : "";
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId != null ? customerId : "";
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName != null ? customerName : "";
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId != null ? shopId : "";
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName != null ? shopName : "";
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "PENDING";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt != null ? createdAt : "";
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : "";
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("udhariId", udhariId);
        map.put("billingId", billingId);
        map.put("orderId", orderId);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("shopId", shopId);
        map.put("shopName", shopName);
        map.put("totalAmount", totalAmount);
        map.put("paidAmount", paidAmount);
        map.put("remainingAmount", remainingAmount);
        map.put("status", status);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    public static UdhariModel fromDocumentMap(String docId, Map<String, Object> map) {
        if (map == null) return new UdhariModel();

        String udhariId = map.get("udhariId") != null ? map.get("udhariId").toString() : docId;
        String billingId = map.get("billingId") != null ? map.get("billingId").toString() : "";
        String orderId = map.get("orderId") != null ? map.get("orderId").toString() : "";
        String customerId = map.get("customerId") != null ? map.get("customerId").toString() : "";
        String customerName = map.get("customerName") != null ? map.get("customerName").toString() : "";
        String shopId = map.get("shopId") != null ? map.get("shopId").toString() : "";
        String shopName = map.get("shopName") != null ? map.get("shopName").toString() : "";

        double totalAmount = 0.0;
        if (map.get("totalAmount") != null) {
            try { totalAmount = Double.parseDouble(map.get("totalAmount").toString()); } catch (Exception ignored) {}
        }
        double paidAmount = 0.0;
        if (map.get("paidAmount") != null) {
            try { paidAmount = Double.parseDouble(map.get("paidAmount").toString()); } catch (Exception ignored) {}
        }
        double remainingAmount = totalAmount - paidAmount;
        if (map.get("remainingAmount") != null) {
            try { remainingAmount = Double.parseDouble(map.get("remainingAmount").toString()); } catch (Exception ignored) {}
        }

        String status = map.get("status") != null ? map.get("status").toString() : "PENDING";
        String createdAt = map.get("createdAt") != null ? map.get("createdAt").toString() : "";
        String updatedAt = map.get("updatedAt") != null ? map.get("updatedAt").toString() : "";

        return new UdhariModel(udhariId, billingId, orderId, customerId, customerName, shopId, shopName, totalAmount, paidAmount, remainingAmount, status, createdAt, updatedAt);
    }
}
