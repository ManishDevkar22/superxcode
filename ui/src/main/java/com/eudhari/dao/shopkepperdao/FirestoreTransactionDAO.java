package com.eudhari.dao.shopkepperdao;

import com.eudhari.config.Firebaseinitialization;
import com.eudhari.model.shopkeppermodel.*;
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
 * Firestore DAO implementation for Transactions / Sales.
 * Interacts with the "transactions" collection in Firestore.
 */
public class FirestoreTransactionDAO implements TransactionDAO {
    private static final String COLLECTION_NAME = "transactions";

    public FirestoreTransactionDAO() {
        Firebaseinitialization.getFirebaseConfig();
    }

    private Firestore getDb() {
        return Firebaseinitialization.getFireStore();
    }

    @Override
    public List<TransactionModel> getAllTransactions() {
        List<TransactionModel> txList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                txList.add(documentToTransaction(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreTransactionDAO] Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return txList;
    }

    @Override
    public List<TransactionModel> getTransactionsByShopId(String shopId) {
        List<TransactionModel> txList = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return txList;
        }
        try {
            ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME)
                    .whereEqualTo("shopId", shopId.trim())
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                txList.add(documentToTransaction(doc));
            }
        } catch (Exception e) {
            System.err.println("[FirestoreTransactionDAO] Error fetching transactions for shop " + shopId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return txList;
    }

    @Override
    public void saveTransaction(TransactionModel transaction) {
        if (transaction == null || transaction.getBillId() == null)
            return;
        try {
            Map<String, Object> docMap = toDocumentMap(transaction);
            getDb().collection(COLLECTION_NAME).document(transaction.getBillId()).set(docMap).get();
            System.out.println("[FirestoreDAO] Saved transaction to Firestore '" + COLLECTION_NAME + "': "
                    + transaction.getBillId());
        } catch (Exception e) {
            System.err.println("[FirestoreTransactionDAO] Error saving transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateTransaction(TransactionModel transaction) {
        if (transaction == null || transaction.getBillId() == null)
            return;
        try {
            Map<String, Object> docMap = toDocumentMap(transaction);
            getDb().collection(COLLECTION_NAME).document(transaction.getBillId()).set(docMap, SetOptions.merge()).get();
            System.out.println("[FirestoreDAO] Updated transaction in Firestore '" + COLLECTION_NAME + "': "
                    + transaction.getBillId());
        } catch (Exception e) {
            System.err.println("[FirestoreTransactionDAO] Error updating transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTransaction(String billId) {
        if (billId == null || billId.trim().isEmpty())
            return;
        try {
            getDb().collection(COLLECTION_NAME).document(billId.trim()).delete().get();
            System.out.println("[FirestoreDAO] Deleted transaction ID '" + billId + "' from Firestore collection '"
                    + COLLECTION_NAME + "'");
        } catch (Exception e) {
            System.err.println("[FirestoreTransactionDAO] Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private TransactionModel documentToTransaction(DocumentSnapshot doc) {
        String billId = doc.getString("billId");
        if (billId == null || billId.isEmpty())
            billId = doc.getId();
        String shopId = doc.getString("shopId");
        String orderId = doc.getString("orderId");
        String customerId = doc.getString("customerId");
        String customerName = doc.getString("customerName");
        String itemsSummary = doc.getString("itemsSummary");
        Long totalQuantityVal = doc.getLong("totalQuantity");
        int totalQuantity = totalQuantityVal != null ? totalQuantityVal.intValue() : 0;
        Double totalAmountVal = doc.getDouble("totalAmount");
        double totalAmount = totalAmountVal != null ? totalAmountVal : 0.0;
        String paymentMethod = doc.getString("paymentMethod");
        String dateTime = doc.getString("dateTime");
        String status = doc.getString("status");

        TransactionModel tx = new TransactionModel(billId, customerId != null ? customerId : "",
                customerName != null ? customerName : "", itemsSummary != null ? itemsSummary : "", totalQuantity,
                totalAmount, paymentMethod != null ? paymentMethod : "", dateTime != null ? dateTime : "",
                status != null ? status : "");
        if (shopId != null) tx.setShopId(shopId);
        if (orderId != null) tx.setOrderId(orderId);
        return tx;
    }

    private Map<String, Object> toDocumentMap(TransactionModel t) {
        Map<String, Object> map = new HashMap<>();
        map.put("billId", t.getBillId());
        map.put("billingId", t.getBillId());
        map.put("shopId", t.getShopId());
        map.put("orderId", t.getOrderId());
        map.put("customerId", t.getCustomerId());
        map.put("customerName", t.getCustomerName());
        map.put("itemsSummary", t.getItemsSummary());
        map.put("totalQuantity", t.getTotalQuantity());
        map.put("totalAmount", t.getTotalAmount());
        map.put("paymentMethod", t.getPaymentMethod());
        map.put("dateTime", t.getDateTime());
        map.put("status", t.getStatus());
        return map;
    }
}
