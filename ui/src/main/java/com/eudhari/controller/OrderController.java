package com.eudhari.controller;

import com.eudhari.dao.FirestoreOrderDAO;
import com.eudhari.dao.OrderDAO;
import com.eudhari.model.OrderItemModel;
import com.eudhari.model.OrderModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private static OrderController instance;
    private final OrderDAO orderDAO;

    private OrderController() {
        this.orderDAO = new FirestoreOrderDAO();
    }

    public static synchronized OrderController getInstance() {
        if (instance == null) {
            instance = new OrderController();
        }
        return instance;
    }

    public OrderModel createOrder(String customerId, String customerName, String shopId, String shopName,
                                 String shopkeeperId, List<OrderItemModel> items, double totalAmount) {
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()
                || items == null || items.isEmpty()) {
            System.err.println("[OrderController] Cannot create order: missing customerId, shopId, or empty items.");
            return null;
        }

        int count = orderDAO.getOrderCount();
        String orderId = String.format("ORD%03d", count + 1);
        String timestamp = Instant.now().toString();

        OrderModel order = new OrderModel(
                orderId,
                customerId.trim(),
                customerName != null ? customerName.trim() : "",
                shopId.trim(),
                shopName != null ? shopName.trim() : "",
                shopkeeperId != null ? shopkeeperId.trim() : "",
                items,
                totalAmount,
                "PENDING",
                timestamp,
                timestamp
        );

        orderDAO.saveOrder(order);

        // Send notification to shopkeeper
        NotificationController.getInstance().sendNotification(
                shopkeeperId,
                "shopkeeper",
                customerId,
                "customer",
                "ORDER",
                "New Order Request",
                customerName + " sent an order request #" + orderId + " totaling ₹" + String.format("%.2f", totalAmount),
                orderId
        );

        return order;
    }

    public boolean approveOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        orderDAO.updateOrderStatus(orderId, "APPROVED");
        OrderModel order = orderDAO.getOrderById(orderId);
        if (order != null) {
            NotificationController.getInstance().sendNotification(
                    order.getCustomerId(),
                    "customer",
                    order.getShopkeeperId(),
                    "shopkeeper",
                    "ORDER",
                    "Order Approved",
                    "Your order #" + orderId + " was approved by " + order.getShopName() + "!",
                    orderId
            );
        }
        return true;
    }

    public boolean acceptOrder(String orderId) {
        return approveOrder(orderId);
    }

    public boolean rejectOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        orderDAO.updateOrderStatus(orderId, "REJECTED");
        OrderModel order = orderDAO.getOrderById(orderId);
        if (order != null) {
            NotificationController.getInstance().sendNotification(
                    order.getCustomerId(),
                    "customer",
                    order.getShopkeeperId(),
                    "shopkeeper",
                    "ORDER",
                    "Order Rejected",
                    "Your order #" + orderId + " was rejected by " + order.getShopName() + ".",
                    orderId
            );
        }
        return true;
    }

    public boolean cancelOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        OrderModel existing = orderDAO.getOrderById(orderId);
        if (existing != null && "PENDING".equalsIgnoreCase(existing.getStatus())) {
            orderDAO.updateOrderStatus(orderId, "CANCELLED");
            return true;
        }
        System.err.println("[OrderController] Cannot cancel order " + orderId + ": status is not PENDING.");
        return false;
    }

    public List<OrderModel> getOrdersForCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return orderDAO.getOrdersByCustomer(customerId);
    }

    public List<OrderModel> getOrdersForShopkeeper(String shopkeeperId) {
        if (shopkeeperId == null || shopkeeperId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return orderDAO.getOrdersByShopkeeper(shopkeeperId);
    }

    public List<OrderModel> getOrdersForShop(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return orderDAO.getOrdersByShop(shopId);
    }

    public OrderModel getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return null;
        }
        return orderDAO.getOrderById(orderId);
    }
}
