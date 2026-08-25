package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.NotificationModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreNotificationDAO implements NotificationDAO {
    private static final String COLLECTION_NAME = "notifications";

    public FirestoreNotificationDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveNotification(NotificationModel notification) {
        if (notification == null || notification.getNotificationId() == null || notification.getNotificationId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = notification.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(notification.getNotificationId().trim()).set(map).get();
            System.out.println("[FirestoreNotificationDAO] Saved notification '" + notification.getNotificationId() + "' to receiver: " + notification.getReceiverId());
        } catch (Exception e) {
            System.err.println("[FirestoreNotificationDAO] Error saving notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void markAsRead(String notificationId) {
        if (notificationId == null || notificationId.trim().isEmpty()) return;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("isRead", true);
            getDb().collection(COLLECTION_NAME).document(notificationId.trim()).set(map, SetOptions.merge()).get();
        } catch (Exception e) {
            System.err.println("[FirestoreNotificationDAO] Error marking notification read: " + e.getMessage());
        }
    }

    @Override
    public void markAllAsRead(String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) return;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("receiverId", receiverId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("isRead", true);
                    getDb().collection(COLLECTION_NAME).document(doc.getId()).set(map, SetOptions.merge());
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreNotificationDAO] Error marking all notifications read: " + e.getMessage());
        }
    }

    @Override
    public List<NotificationModel> getNotificationsByReceiver(String receiverId) {
        List<NotificationModel> list = new ArrayList<>();
        if (receiverId == null || receiverId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("receiverId", receiverId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(NotificationModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreNotificationDAO] Error fetching notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getNotificationCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreNotificationDAO] Error getting count: " + e.getMessage());
        }
        return 0;
    }
}
