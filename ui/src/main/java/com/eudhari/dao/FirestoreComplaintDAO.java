package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.ComplaintModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreComplaintDAO implements ComplaintDAO {
    private static final String COLLECTION_NAME = "complaints";

    public FirestoreComplaintDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveComplaint(ComplaintModel complaint) {
        if (complaint == null || complaint.getComplaintId() == null || complaint.getComplaintId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = complaint.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(complaint.getComplaintId().trim()).set(map).get();
            System.out.println("[FirestoreComplaintDAO] Saved complaint to Firestore '" + COLLECTION_NAME + "': " + complaint.getComplaintId());
        } catch (Exception e) {
            System.err.println("[FirestoreComplaintDAO] Error saving complaint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ComplaintModel> getComplaintsByUserId(String userId) {
        List<ComplaintModel> list = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId.trim())
                    .get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(ComplaintModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreComplaintDAO] Error fetching complaints for user: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ComplaintModel> getAllComplaints() {
        List<ComplaintModel> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(ComplaintModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreComplaintDAO] Error fetching all complaints: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ComplaintModel getComplaintById(String complaintId) {
        if (complaintId == null || complaintId.trim().isEmpty()) return null;
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(complaintId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return ComplaintModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreComplaintDAO] Error fetching complaint by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateComplaintStatusAndResponse(String complaintId, String status, String adminResponse) {
        if (complaintId == null || complaintId.trim().isEmpty()) return;
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status);
            updates.put("adminResponse", adminResponse);
            updates.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            getDb().collection(COLLECTION_NAME).document(complaintId.trim()).update(updates).get();
            System.out.println("[FirestoreComplaintDAO] Updated complaint '" + complaintId + "' status=" + status);
        } catch (Exception e) {
            System.err.println("[FirestoreComplaintDAO] Error updating complaint status: " + e.getMessage());
        }
    }
}
