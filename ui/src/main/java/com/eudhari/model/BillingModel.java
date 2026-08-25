package com.eudhari.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingModel {
    private String billingId;
    private String orderId;
    private String customerId;
    private String customerName;
    private String shopId;
    private String shopName;
    private String shopkeeperId;
    private List<OrderItemModel> items = new ArrayList<>();
    private double totalAmount;
    private String paymentMethod; // CASH, ONLINE, UDHARI
    private String paymentStatus; // PAID, PENDING
    private String createdAt;

    public BillingModel() {}

    public BillingModel(String billingId, String orderId, String customerId, String customerName, String shopId,
                        String shopName, String shopkeeperId, List<OrderItemModel> items, double totalAmount,
                        String paymentMethod, String paymentStatus, String createdAt) {
        this.billingId = billingId != null ? billingId : "";
        this.orderId = orderId != null ? orderId : "";
        this.customerId = customerId != null ? customerId : "";
        this.customerName = customerName != null ? customerName : "";
        this.shopId = shopId != null ? shopId : "";
        this.shopName = shopName != null ? shopName : "";
        this.shopkeeperId = shopkeeperId != null ? shopkeeperId : "";
        if (items != null) {
            this.items = items;
        }
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "CASH";
        this.paymentStatus = paymentStatus != null ? paymentStatus : "PAID";
        this.createdAt = createdAt != null ? createdAt : "";
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

    public String getShopkeeperId() {
        return shopkeeperId;
    }

    public void setShopkeeperId(String shopkeeperId) {
        this.shopkeeperId = shopkeeperId != null ? shopkeeperId : "";
    }

    public List<OrderItemModel> getItems() {
        return items;
    }

    public void setItems(List<OrderItemModel> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod != null ? paymentMethod : "CASH";
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus != null ? paymentStatus : "PAID";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt != null ? createdAt : "";
    }

    public String getItemsSummary() {
        if (items == null || items.isEmpty()) return "No items";
        StringBuilder sb = new StringBuilder();
        for (OrderItemModel item : items) {
            sb.append(item.getProductName()).append(" x").append(item.getQuantity()).append(", ");
        }
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : "No items";
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("billingId", billingId);
        map.put("orderId", orderId);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("shopId", shopId);
        map.put("shopName", shopName);
        map.put("shopkeeperId", shopkeeperId);

        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (items != null) {
            for (OrderItemModel item : items) {
                itemsList.add(item.toDocumentMap());
            }
        }
        map.put("items", itemsList);
        map.put("totalAmount", totalAmount);
        map.put("paymentMethod", paymentMethod);
        map.put("paymentStatus", paymentStatus);
        map.put("createdAt", createdAt);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static BillingModel fromDocumentMap(String docId, Map<String, Object> map) {
        if (map == null) return new BillingModel();

        String billingId = map.get("billingId") != null ? map.get("billingId").toString() : docId;
        String orderId = map.get("orderId") != null ? map.get("orderId").toString() : "";
        String customerId = map.get("customerId") != null ? map.get("customerId").toString() : "";
        String customerName = map.get("customerName") != null ? map.get("customerName").toString() : "";
        String shopId = map.get("shopId") != null ? map.get("shopId").toString() : "";
        String shopName = map.get("shopName") != null ? map.get("shopName").toString() : "";
        String shopkeeperId = map.get("shopkeeperId") != null ? map.get("shopkeeperId").toString() : "";

        List<OrderItemModel> itemList = new ArrayList<>();
        if (map.get("items") instanceof List) {
            List<?> rawList = (List<?>) map.get("items");
            for (Object obj : rawList) {
                if (obj instanceof Map) {
                    itemList.add(OrderItemModel.fromDocumentMap((Map<String, Object>) obj));
                }
            }
        }

        double totalAmount = 0.0;
        if (map.get("totalAmount") != null) {
            try {
                totalAmount = Double.parseDouble(map.get("totalAmount").toString());
            } catch (Exception ignored) {}
        }

        String paymentMethod = map.get("paymentMethod") != null ? map.get("paymentMethod").toString() : "CASH";
        String paymentStatus = map.get("paymentStatus") != null ? map.get("paymentStatus").toString() : "PAID";
        String createdAt = map.get("createdAt") != null ? map.get("createdAt").toString() : "";

        return new BillingModel(billingId, orderId, customerId, customerName, shopId, shopName, shopkeeperId, itemList, totalAmount, paymentMethod, paymentStatus, createdAt);
    }
}
