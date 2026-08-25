package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.UdhariModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreUdhariDAO implements UdhariDAO {
    private static final String COLLECTION_NAME = "udhari";

    public FirestoreUdhariDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveUdhari(UdhariModel udhari) {
        if (udhari == null || udhari.getUdhariId() == null || udhari.getUdhariId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = udhari.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(udhari.getUdhariId().trim()).set(map).get();
            System.out.println("[FirestoreUdhariDAO] Saved udhari '" + udhari.getUdhariId() + "' to collection '" + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error saving udhari: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateUdhari(UdhariModel udhari) {
        if (udhari == null || udhari.getUdhariId() == null || udhari.getUdhariId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = udhari.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(udhari.getUdhariId().trim()).set(map, SetOptions.merge()).get();
            System.out.println("[FirestoreUdhariDAO] Updated udhari '" + udhari.getUdhariId() + "' in collection '" + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error updating udhari: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public UdhariModel getUdhariById(String udhariId) {
        if (udhariId == null || udhariId.trim().isEmpty()) return null;
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(udhariId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return UdhariModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error fetching udhari by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UdhariModel getUdhariByBillingId(String billingId) {
        if (billingId == null || billingId.trim().isEmpty()) return null;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("billingId", billingId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty() && docs.get(0).getData() != null) {
                return UdhariModel.fromDocumentMap(docs.get(0).getId(), docs.get(0).getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error fetching udhari by billingId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<UdhariModel> getUdhariByCustomer(String customerId) {
        List<UdhariModel> list = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(UdhariModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error fetching udhari for customer: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<UdhariModel> getUdhariByShop(String shopId) {
        List<UdhariModel> list = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(UdhariModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error fetching udhari for shop: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<UdhariModel> getUdhariByCustomerAndShop(String customerId, String shopId) {
        List<UdhariModel> list = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()) return list;
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("customerId", customerId.trim())
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(UdhariModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error fetching udhari for customer+shop: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getUdhariCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreUdhariDAO] Error getting count: " + e.getMessage());
        }
        return 0;
    }
}
