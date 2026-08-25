package com.eudhari.controller;

import com.eudhari.dao.FirestoreNotificationDAO;
import com.eudhari.dao.NotificationDAO;
import com.eudhari.model.NotificationModel;

import java.time.Instant;
import java.util.List;

public class NotificationController {
    private static NotificationController instance;
    private final NotificationDAO notificationDAO;

    private NotificationController() {
        this.notificationDAO = new FirestoreNotificationDAO();
    }

    public static synchronized NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }

    public NotificationModel sendNotification(String receiverId, String receiverRole, String senderId, String senderRole,
                                             String type, String title, String message, String relatedId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            return null;
        }

        int count = notificationDAO.getNotificationCount();
        String notificationId = String.format("NTF%04d", count + 1);
        String createdAt = Instant.now().toString();

        NotificationModel notification = new NotificationModel(
                notificationId,
                receiverId.trim(),
                receiverRole != null ? receiverRole.trim() : "",
                senderId != null ? senderId.trim() : "",
                senderRole != null ? senderRole.trim() : "",
                type != null ? type.trim() : "SYSTEM",
                title != null ? title.trim() : "",
                message != null ? message.trim() : "",
                relatedId != null ? relatedId.trim() : "",
                false,
                createdAt
        );

        notificationDAO.saveNotification(notification);
        return notification;
    }

    public List<NotificationModel> getNotificationsForUser(String receiverId) {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return notificationDAO.getNotificationsByReceiver(receiverId);
    }

    public void markAsRead(String notificationId) {
        if (notificationId != null && !notificationId.trim().isEmpty()) {
            notificationDAO.markAsRead(notificationId);
        }
    }

    public void markAllAsRead(String receiverId) {
        if (receiverId != null && !receiverId.trim().isEmpty()) {
            notificationDAO.markAllAsRead(receiverId);
        }
    }
}
