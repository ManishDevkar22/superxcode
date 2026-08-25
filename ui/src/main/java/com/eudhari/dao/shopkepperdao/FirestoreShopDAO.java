package com.eudhari.dao.shopkepperdao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.ShopModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreShopDAO implements ShopDAO {
    private static final String COLLECTION_NAME = "shops";

    public FirestoreShopDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveShop(ShopModel shop) {
        if (shop == null || shop.getShopId() == null || shop.getShopId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = shop.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(shop.getShopId().trim()).set(map).get();
            System.out.println("[FirestoreShopDAO] Saved shop to Firestore '" + COLLECTION_NAME + "': " + shop.getShopId());
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error saving shop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ShopModel getShopById(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) {
            return null;
        }
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(shopId.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return ShopModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error fetching shop by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ShopModel getShopByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            return null;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("ownerId", ownerId.trim())
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty()) {
                DocumentSnapshot doc = docs.get(0);
                return ShopModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error fetching shop by owner ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ShopModel> getAllShops() {
        List<ShopModel> shops = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc.exists() && doc.getData() != null) {
                    shops.add(ShopModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error fetching all shops: " + e.getMessage());
            e.printStackTrace();
        }
        return shops;
    }

    @Override
    public void updateShop(ShopModel shop) {
        if (shop == null || shop.getShopId() == null || shop.getShopId().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = shop.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(shop.getShopId().trim()).set(map, SetOptions.merge()).get();
            System.out.println("[FirestoreShopDAO] Updated shop in Firestore '" + COLLECTION_NAME + "': " + shop.getShopId());
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error updating shop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getShopCount() {
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreShopDAO] Error fetching shop count: " + e.getMessage());
        }
        return 0;
    }
}
