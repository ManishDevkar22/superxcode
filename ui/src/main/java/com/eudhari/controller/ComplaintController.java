package com.eudhari.controller;

import com.eudhari.dao.ComplaintDAO;
import com.eudhari.dao.FirestoreComplaintDAO;
import com.eudhari.model.ComplaintModel;

import java.util.List;

public class ComplaintController {
    private static ComplaintController instance;
    private final ComplaintDAO complaintDAO;

    private ComplaintController() {
        this.complaintDAO = new FirestoreComplaintDAO();
    }

    public static synchronized ComplaintController getInstance() {
        if (instance == null) {
            instance = new ComplaintController();
        }
        return instance;
    }

    public ComplaintModel createComplaint(String userId, String userRole, String name, String subject, String description) {
        String complaintId = "CMP" + String.format("%03d", (int) (Math.random() * 900) + 100);
        ComplaintModel complaint = new ComplaintModel(complaintId, userId, userRole, name, subject, description);
        complaintDAO.saveComplaint(complaint);
        return complaint;
    }

    public List<ComplaintModel> getComplaintsForUser(String userId) {
        return complaintDAO.getComplaintsByUserId(userId);
    }

    public List<ComplaintModel> getAllComplaints() {
        return complaintDAO.getAllComplaints();
    }

    public void updateComplaintByAdmin(String complaintId, String status, String adminResponse) {
        ComplaintModel c = complaintDAO.getComplaintById(complaintId);
        complaintDAO.updateComplaintStatusAndResponse(complaintId, status, adminResponse);

        if (c != null && c.getUserId() != null && !c.getUserId().isBlank()) {
            NotificationController.getInstance().sendNotification(
                    c.getUserId(),
                    c.getUserRole() != null ? c.getUserRole() : "USER",
                    "ADMIN",
                    "ADMIN",
                    "COMPLAINT",
                    "Complaint Update: " + (c.getSubject() != null ? c.getSubject() : "Support Ticket"),
                    "Status: " + status + ". Admin Response: " + adminResponse,
                    complaintId
            );
        }
    }
}
