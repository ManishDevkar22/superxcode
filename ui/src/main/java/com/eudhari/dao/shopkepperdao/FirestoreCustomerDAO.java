package com.eudhari.dao.shopkepperdao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.shopkeppermodel.CustomerModel;
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

/**
 * Firestore DAO implementation for Customers.
 * Interacts with the "customers" collection in Firestore.
 */
public class FirestoreCustomerDAO implements CustomerDAO {
    private static final String COLLECTION_NAME = "customers";

    public FirestoreCustomerDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        List<CustomerModel> customerList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                customerList.add(documentToCustomer(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error fetching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customerList;
    }

    @Override
    public List<CustomerModel> getCustomersByShopId(String shopId) {
        List<CustomerModel> customerList = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return customerList;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                customerList.add(documentToCustomer(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error fetching customers for shop " + shopId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return customerList;
    }

    @Override
    public CustomerModel getCustomerById(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        try {
            DocumentSnapshot doc = getDb().collection(COLLECTION_NAME).document(id.trim()).get().get();
            if (doc.exists()) {
                return documentToCustomer(doc);
            }
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error fetching customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveCustomer(CustomerModel customer) {
        if (customer == null || customer.getId() == null)
            return;
        try {
            Map<String, Object> map = toDocumentMap(customer);
            getDb().collection(COLLECTION_NAME).document(customer.getId()).set(map).get();
            System.out.println(
                    "[FirestoreDAO] Saved customer to Firestore '" + COLLECTION_NAME + "': " + customer.getId());
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error saving customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateCustomer(CustomerModel customer) {
        if (customer == null || customer.getId() == null)
            return;
        try {
            Map<String, Object> map = toDocumentMap(customer);
            getDb().collection(COLLECTION_NAME).document(customer.getId()).set(map, SetOptions.merge()).get();
            System.out.println(
                    "[FirestoreDAO] Updated customer in Firestore '" + COLLECTION_NAME + "': " + customer.getId());
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCustomer(String id) {
        if (id == null || id.trim().isEmpty())
            return;
        try {
            getDb().collection(COLLECTION_NAME).document(id.trim()).delete().get();
            System.out.println("[FirestoreDAO] Permanently deleted customer ID '" + id + "' from Firestore collection '"
                    + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreCustomerDAO] Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private CustomerModel documentToCustomer(DocumentSnapshot doc) {
        String id = doc.getString("id");
        if (id == null || id.isEmpty())
            id = doc.getId();
        String uid = doc.getString("uid");
        String shopId = doc.getString("shopId");
        String name = doc.getString("name");
        String phone = doc.getString("phone");
        String joinedDate = doc.getString("joinedDate");
        String status = doc.getString("status");
        Double totalPurchasesVal = doc.getDouble("totalPurchases");
        double totalPurchases = totalPurchasesVal != null ? totalPurchasesVal : 0.0;
        Double pendingUdhariVal = doc.getDouble("pendingUdhari");
        double pendingUdhari = pendingUdhariVal != null ? pendingUdhariVal : 0.0;
        Boolean deletedVal = doc.getBoolean("deleted");
        boolean deleted = deletedVal != null ? deletedVal : false;

        CustomerModel customer = new CustomerModel(id, name != null ? name : "", phone != null ? phone : "",
                joinedDate != null ? joinedDate : "", status != null ? status : "", totalPurchases, pendingUdhari);
        if (uid != null) customer.setUid(uid);
        if (shopId != null) customer.setShopId(shopId);
        customer.setDeleted(deleted);
        return customer;
    }

    private Map<String, Object> toDocumentMap(CustomerModel c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("uid", c.getUid());
        map.put("shopId", c.getShopId());
        map.put("name", c.getName());
        map.put("phone", c.getPhone());
        map.put("joinedDate", c.getJoinedDate());
        map.put("status", c.getStatus());
        map.put("totalPurchases", c.getTotalPurchases());
        map.put("pendingUdhari", c.getPendingUdhari());
        map.put("deleted", c.isDeleted());
        return map;
    }
}
