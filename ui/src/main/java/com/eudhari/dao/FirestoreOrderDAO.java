package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.OrderModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreOrderDAO implements OrderDAO {
    private static final String COLLECTION_NAME = "orders";

    public FirestoreOrderDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveOrder(OrderModel order) {
        if (order == null || order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> docMap = order.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(order.getOrderId().trim()).set(docMap).get();
            System.out.println("[FirestoreOrderDAO] Saved order to Firestore '" + COLLECTION_NAME + "': " + order.getOrderId());
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error saving order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        if (orderId == null || orderId.trim().isEmpty() || status == null) {
            return;
        }
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status.trim().toUpperCase());
            updates.put("updatedAt", Instant.now().toString());
            getDb().collection(COLLECTION_NAME).document(orderId.trim()).set(updates, SetOptions.merge()).get();
            System.out.println("[FirestoreOrderDAO] Updated order status in Firestore '" + orderId + "' -> " + status);
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error updating order status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public OrderModel getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return null;
        }
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(orderId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return OrderModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error fetching order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderModel> getOrdersByCustomer(String customerId) {
        List<OrderModel> orders = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) {
            return orders;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    orders.add(OrderModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error fetching orders by customer ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<OrderModel> getOrdersByShopkeeper(String shopkeeperId) {
        List<OrderModel> orders = new ArrayList<>();
        if (shopkeeperId == null || shopkeeperId.trim().isEmpty()) {
            return orders;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopkeeperId", shopkeeperId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    orders.add(OrderModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error fetching orders by shopkeeper ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<OrderModel> getOrdersByShop(String shopId) {
        List<OrderModel> orders = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return orders;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    orders.add(OrderModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error fetching orders by shop ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public int getOrderCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreOrderDAO] Error fetching order count: " + e.getMessage());
        }
        return 0;
    }
}
