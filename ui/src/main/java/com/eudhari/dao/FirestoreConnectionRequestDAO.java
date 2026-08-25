package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.ConnectionRequestModel;
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

public class FirestoreConnectionRequestDAO implements ConnectionRequestDAO {
    private static final String COLLECTION_NAME = "connectionRequests";

    public FirestoreConnectionRequestDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveRequest(ConnectionRequestModel request) {
        if (request == null || request.getRequestId() == null || request.getRequestId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = request.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(request.getRequestId().trim()).set(map).get();
            System.out.println("[FirestoreConnectionRequestDAO] Saved connection request: " + request.getRequestId());
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error saving connection request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateRequestStatus(String requestId, String status) {
        if (requestId == null || requestId.trim().isEmpty() || status == null) {
            return;
        }
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status.trim().toUpperCase());
            updates.put("updatedAt", Instant.now().toString());
            getDb().collection(COLLECTION_NAME).document(requestId.trim()).set(updates, SetOptions.merge()).get();
            System.out.println("[FirestoreConnectionRequestDAO] Updated request " + requestId + " to status: " + status);
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error updating request status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ConnectionRequestModel getRequestById(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return null;
        }
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(requestId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return ConnectionRequestModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error fetching request by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ConnectionRequestModel> getRequestsByCustomer(String customerId) {
        List<ConnectionRequestModel> list = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) {
            return list;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(ConnectionRequestModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error fetching requests by customer: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ConnectionRequestModel> getRequestsByShopkeeper(String shopkeeperId) {
        List<ConnectionRequestModel> list = new ArrayList<>();
        if (shopkeeperId == null || shopkeeperId.trim().isEmpty()) {
            return list;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopkeeperId", shopkeeperId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(ConnectionRequestModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error fetching requests by shopkeeper: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ConnectionRequestModel> getApprovedRequestsForCustomer(String customerId) {
        List<ConnectionRequestModel> approved = new ArrayList<>();
        for (ConnectionRequestModel req : getRequestsByCustomer(customerId)) {
            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                approved.add(req);
            }
        }
        return approved;
    }

    @Override
    public List<ConnectionRequestModel> getApprovedRequestsForShop(String shopId) {
        List<ConnectionRequestModel> list = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return list;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    ConnectionRequestModel req = ConnectionRequestModel.fromDocumentMap(doc.getId(), doc.getData());
                    if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                        list.add(req);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error fetching approved requests for shop: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ConnectionRequestModel> getApprovedRequestsForShopkeeper(String shopkeeperId) {
        List<ConnectionRequestModel> approved = new ArrayList<>();
        for (ConnectionRequestModel req : getRequestsByShopkeeper(shopkeeperId)) {
            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                approved.add(req);
            }
        }
        return approved;
    }

    @Override
    public boolean hasExistingRequest(String customerId, String shopId) {
        return getRequestByCustomerAndShop(customerId, shopId) != null;
    }

    @Override
    public ConnectionRequestModel getRequestByCustomerAndShop(String customerId, String shopId) {
        if (customerId == null || shopId == null || customerId.trim().isEmpty() || shopId.trim().isEmpty()) {
            return null;
        }
        for (ConnectionRequestModel req : getRequestsByCustomer(customerId)) {
            if (shopId.trim().equalsIgnoreCase(req.getShopId())) {
                return req;
            }
        }
        return null;
    }

    @Override
    public int getRequestCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreConnectionRequestDAO] Error fetching request count: " + e.getMessage());
        }
        return 0;
    }
}
