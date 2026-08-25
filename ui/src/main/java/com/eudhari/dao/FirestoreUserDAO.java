package com.eudhari.dao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.UserModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;

import java.util.Map;

public class FirestoreUserDAO implements UserDAO {
    private static final String COLLECTION_NAME = "users";

    public FirestoreUserDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public void saveUser(UserModel user) {
        if (user == null || user.getUid() == null || user.getUid().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = user.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(user.getUid().trim()).set(map).get();
            System.out.println("[FirestoreUserDAO] Saved user to Firestore '" + COLLECTION_NAME + "': " + user.getUid());
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public UserModel getUserById(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            return null;
        }
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(uid.trim()).get().get();
            if (doc.exists() && doc.getData() != null) {
                return UserModel.fromDocumentMap(doc.getId(), doc.getData());
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error fetching user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateUser(UserModel user) {
        if (user == null || user.getUid() == null || user.getUid().trim().isEmpty()) {
            return;
        }
        try {
            Map<String, Object> map = user.toDocumentMap();
            getDb().collection(COLLECTION_NAME).document(user.getUid().trim()).set(map, SetOptions.merge()).get();
            System.out.println("[FirestoreUserDAO] Updated user in Firestore '" + COLLECTION_NAME + "': " + user.getUid());
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getUserCountByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return 0;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("role", role.trim().toLowerCase())
                    .get();
            return future.get().getDocuments().size();
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error fetching user count by role: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public java.util.List<UserModel> getUsersByRole(String role) {
        java.util.List<UserModel> list = new java.util.ArrayList<>();
        if (role == null || role.trim().isEmpty()) {
            return list;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("role", role.trim().toLowerCase())
                    .get();
            for (DocumentSnapshot doc : future.get().getDocuments()) {
                if (doc.exists() && doc.getData() != null) {
                    list.add(UserModel.fromDocumentMap(doc.getId(), doc.getData()));
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error fetching users by role: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void updateUserStatus(String uid, String status) {
        if (uid == null || uid.trim().isEmpty()) return;
        try {
            getDb().collection(COLLECTION_NAME).document(uid.trim()).update("status", status).get();
            System.out.println("[FirestoreUserDAO] Updated user status '" + uid + "' -> " + status);
        } catch (Exception e) {
            System.err.println("[FirestoreUserDAO] Error updating user status: " + e.getMessage());
        }
    }
}
