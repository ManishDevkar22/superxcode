package com.eudhari.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ComplaintModel {
    private String complaintId;
    private String userId;
    private String userRole;
    private String name;
    private String subject;
    private String description;
    private String status; // OPEN, IN_PROGRESS, RESOLVED
    private String adminResponse;
    private String createdAt;
    private String updatedAt;

    public ComplaintModel() {}

    public ComplaintModel(String complaintId, String userId, String userRole, String name, String subject, String description) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.userRole = userRole;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.status = "OPEN";
        this.adminResponse = "";
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status != null ? status : "OPEN";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminResponse() {
        return adminResponse != null ? adminResponse : "";
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> toDocumentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("complaintId", complaintId);
        map.put("userId", userId);
        map.put("userRole", userRole);
        map.put("name", name);
        map.put("subject", subject);
        map.put("description", description);
        map.put("status", getStatus());
        map.put("adminResponse", getAdminResponse());
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    public static ComplaintModel fromDocumentMap(String docId, Map<String, Object> map) {
        ComplaintModel c = new ComplaintModel();
        c.setComplaintId(map.containsKey("complaintId") && map.get("complaintId") != null ? map.get("complaintId").toString() : docId);
        c.setUserId(map.containsKey("userId") && map.get("userId") != null ? map.get("userId").toString() : "");
        c.setUserRole(map.containsKey("userRole") && map.get("userRole") != null ? map.get("userRole").toString() : "");
        c.setName(map.containsKey("name") && map.get("name") != null ? map.get("name").toString() : "");
        c.setSubject(map.containsKey("subject") && map.get("subject") != null ? map.get("subject").toString() : "");
        c.setDescription(map.containsKey("description") && map.get("description") != null ? map.get("description").toString() : "");
        c.setStatus(map.containsKey("status") && map.get("status") != null ? map.get("status").toString() : "OPEN");
        c.setAdminResponse(map.containsKey("adminResponse") && map.get("adminResponse") != null ? map.get("adminResponse").toString() : "");
        c.setCreatedAt(map.containsKey("createdAt") && map.get("createdAt") != null ? map.get("createdAt").toString() : "");
        c.setUpdatedAt(map.containsKey("updatedAt") && map.get("updatedAt") != null ? map.get("updatedAt").toString() : "");
        return c;
    }
}
