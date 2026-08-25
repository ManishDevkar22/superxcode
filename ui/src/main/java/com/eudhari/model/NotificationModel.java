package com.eudhari.model;

import java.util.HashMap;
import java.util.Map;

public class NotificationModel {
    private String notificationId;
    private String receiverId;
    private String receiverRole;
    private String senderId;
    private String senderRole;
    private String type; // CONNECTION, ORDER, BILLING, UDHARI, SYSTEM
    private String title;
    private String message;
    private String relatedId;
    private boolean isRead;
    private String createdAt;

    public NotificationModel() {}

    public NotificationModel(String notificationId, String receiverId, String receiverRole, String senderId,
                             String senderRole, String type, String title, String message, String relatedId,
                             boolean isRead, String createdAt) {
        this.notificationId = notificationId != null ? notificationId : "";
        this.receiverId = receiverId != null ? receiverId : "";
        this.receiverRole = receiverRole != null ? receiverRole : "";
        this.senderId = senderId != null ? senderId : "";
        this.senderRole = senderRole != null ? senderRole : "";
        this.type = type != null ? type : "SYSTEM";
        this.title = title != null ? title : "";
        this.message = message != null ? message : "";
        this.relatedId = relatedId != null ? relatedId : "";
        this.isRead = isRead;
        this.createdAt = createdAt != null ? createdAt : "";
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId != null ? notificationId : "";
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId != null ? receiverId : "";
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole != null ? receiverRole : "";
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId != null ? senderId : "";
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole != null ? senderRole : "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type : "SYSTEM";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId != null ? relatedId : "";
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt != null ? createdAt : "";
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("notificationId", notificationId);
        map.put("receiverId", receiverId);
        map.put("receiverRole", receiverRole);
        map.put("senderId", senderId);
        map.put("senderRole", senderRole);
        map.put("type", type);
        map.put("title", title);
        map.put("message", message);
        map.put("relatedId", relatedId);
        map.put("isRead", isRead);
        map.put("createdAt", createdAt);
        return map;
    }

    public static NotificationModel fromDocumentMap(String docId, Map<String, Object> map) {
        if (map == null) return new NotificationModel();
        String notificationId = map.get("notificationId") != null ? map.get("notificationId").toString() : docId;
        String receiverId = map.get("receiverId") != null ? map.get("receiverId").toString() : "";
        String receiverRole = map.get("receiverRole") != null ? map.get("receiverRole").toString() : "";
        String senderId = map.get("senderId") != null ? map.get("senderId").toString() : "";
        String senderRole = map.get("senderRole") != null ? map.get("senderRole").toString() : "";
        String type = map.get("type") != null ? map.get("type").toString() : "SYSTEM";
        String title = map.get("title") != null ? map.get("title").toString() : "";
        String message = map.get("message") != null ? map.get("message").toString() : "";
        String relatedId = map.get("relatedId") != null ? map.get("relatedId").toString() : "";

        boolean isRead = false;
        if (map.get("isRead") != null) {
            try {
                isRead = Boolean.parseBoolean(map.get("isRead").toString());
            } catch (Exception ignored) {}
        }

        String createdAt = map.get("createdAt") != null ? map.get("createdAt").toString() : "";

        return new NotificationModel(notificationId, receiverId, receiverRole, senderId, senderRole, type, title, message, relatedId, isRead, createdAt);
    }
}
