package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.BillingModel;
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

public class FirestoreBillingDAO implements BillingDAO {
    private static final String COLLECTION_NAME = "billing";

    public FirestoreBillingDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveBilling(BillingModel billing) {
        if (billing == null || billing.getBillingId() == null || billing.getBillingId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = billing.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(billing.getBillingId().trim()).set(map).get();
            System.out.println("[FirestoreBillingDAO] Saved billing '" + billing.getBillingId() + "' to collection '" + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error saving billing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateBillingStatus(String billingId, String paymentStatus) {
        if (billingId == null || billingId.trim().isEmpty() || paymentStatus == null) return;
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("paymentStatus", paymentStatus.trim().toUpperCase());
            getDb().collection(COLLECTION_NAME).document(billingId.trim()).set(updates, SetOptions.merge()).get();
            System.out.println("[FirestoreBillingDAO] Updated billing status for '" + billingId + "' -> " + paymentStatus);
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error updating billing status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public BillingModel getBillingById(String billingId) {
        if (billingId == null || billingId.trim().isEmpty()) return null;
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(billingId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return BillingModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error fetching billing by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BillingModel> getBillingByCustomer(String customerId) {
        List<BillingModel> list = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            String queryId = customerId.trim().toLowerCase();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    BillingModel b = BillingModel.fromDocumentMap(doc.getId(), doc.getData());
                    String cId = b.getCustomerId() != null ? b.getCustomerId().trim().toLowerCase() : "";
                    String cName = b.getCustomerName() != null ? b.getCustomerName().trim().toLowerCase() : "";
                    if (cId.equals(queryId) || cId.contains(queryId) || queryId.contains(cId) || (!cName.isEmpty() && queryId.contains(cName))) {
                        list.add(b);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error fetching billing for customer: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BillingModel> getBillingByShop(String shopId) {
        List<BillingModel> list = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(BillingModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error fetching billing for shop: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BillingModel> getBillingByCustomerAndShop(String customerId, String shopId) {
        List<BillingModel> list = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(BillingModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error fetching billing for customer+shop: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<BillingModel> getAllBillings() {
        List<BillingModel> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(BillingModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error fetching all billings: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getBillingCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreBillingDAO] Error getting count: " + e.getMessage());
        }
        return 0;
    }
}
