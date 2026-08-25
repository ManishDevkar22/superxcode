package com.eudhari.model;

import java.util.HashMap;
import java.util.Map;

public class OrderItemModel {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    public OrderItemModel() {}

    public OrderItemModel(String productId, String productName, int quantity, double price, double subtotal) {
        this.productId = productId != null ? productId : "";
        this.productName = productName != null ? productName : "";
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal > 0 ? subtotal : (price * quantity);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId != null ? productId : "";
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName != null ? productName : "";
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("productName", productName);
        map.put("quantity", quantity);
        map.put("price", price);
        map.put("subtotal", subtotal);
        return map;
    }

    public static OrderItemModel fromDocumentMap(Map<String, Object> map) {
        if (map == null) return new OrderItemModel();
        String productId = map.get("productId") != null ? map.get("productId").toString() : "";
        String productName = map.get("productName") != null ? map.get("productName").toString() : "";
        int quantity = 0;
        if (map.get("quantity") != null) {
            try {
                quantity = Integer.parseInt(map.get("quantity").toString());
            } catch (Exception e) {
                quantity = ((Number) map.get("quantity")).intValue();
            }
        }
        double price = 0.0;
        if (map.get("price") != null) {
            price = Double.parseDouble(map.get("price").toString());
        }
        double subtotal = 0.0;
        if (map.get("subtotal") != null) {
            subtotal = Double.parseDouble(map.get("subtotal").toString());
        } else {
            subtotal = price * quantity;
        }
        return new OrderItemModel(productId, productName, quantity, price, subtotal);
    }
}
