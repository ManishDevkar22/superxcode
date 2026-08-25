package com.eudhari.model.shopkeppermodel;

import com.eudhari.config.UserSession;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.dao.shopkepperdao.*;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionStore {
    private static TransactionStore instance;

    private final TransactionDAO transactionDAO;
    private final ObservableList<TransactionModel> transactions = FXCollections.observableArrayList();
    private final FilteredList<TransactionModel> udhariTransactions;
    private int billCounter = 1005;

    private TransactionStore() {
        this.transactionDAO = DAOFactory.getTransactionDAO();
        udhariTransactions = new FilteredList<>(transactions, tx -> "Udhari".equalsIgnoreCase(tx.getPaymentMethod())
                || "Udhari Pending".equalsIgnoreCase(tx.getStatus()));
        loadFromDAO();
    }

    public static synchronized TransactionStore getInstance() {
        if (instance == null) {
            instance = new TransactionStore();
        }
        return instance;
    }

    public void loadFromDAO() {
        new Thread(() -> {
            UserModel user = UserSession.getInstance().getCurrentUser();
            java.util.List<TransactionModel> fetched = new java.util.ArrayList<>();
            if (user != null && "shopkeeper".equalsIgnoreCase(user.getRole())) {
                ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
                if (shop != null && shop.getShopId() != null && !shop.getShopId().isEmpty()) {
                    fetched.addAll(transactionDAO.getTransactionsByShopId(shop.getShopId()));
                }
            } else if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                fetched.addAll(transactionDAO.getAllTransactions());
            }
            final java.util.List<TransactionModel> resultList = fetched;
            javafx.application.Platform.runLater(() -> {
                transactions.clear();
                transactions.addAll(resultList);
                billCounter = 1000 + transactions.size();
            });
        }).start();
    }

    public ObservableList<TransactionModel> getAllTransactions() {
        return transactions;
    }

    public FilteredList<TransactionModel> getUdhariTransactions() {
        return udhariTransactions;
    }

    public String generateBillId() {
        billCounter++;
        return "BILL-" + billCounter;
    }

    public TransactionModel addTransaction(CustomerModel customer, String itemsSummary, int totalQty,
            double totalAmount, String paymentMethod) {
        String billId = generateBillId();
        String cId = customer != null ? customer.getId() : "CUST-GUEST";
        String cName = customer != null ? customer.getName() : "Walk-in Customer";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String status = "Udhari".equalsIgnoreCase(paymentMethod) ? "Udhari Pending" : "Paid";

        TransactionModel tx = new TransactionModel(billId, cId, cName, itemsSummary, totalQty, totalAmount,
                paymentMethod, dateTime, status);
        UserModel user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
            if (shop != null && shop.getShopId() != null) {
                tx.setShopId(shop.getShopId());
            }
        }
        transactions.add(0, tx);

        // Update customer total purchases and pending balance if applicable
        if (customer != null) {
            customer.setTotalPurchases(customer.getTotalPurchases() + totalAmount);
            if ("Udhari".equalsIgnoreCase(paymentMethod)) {
                customer.setPendingUdhari(customer.getPendingUdhari() + totalAmount);
            }
        }

        transactionDAO.saveTransaction(tx);
        return tx;
    }

    public void markUdhariPaid(TransactionModel tx) {
        if (tx != null && "Udhari Pending".equalsIgnoreCase(tx.getStatus())) {
            tx.setStatus("Paid");
            CustomerModel customer = CustomerStore.getInstance().getCustomerById(tx.getCustomerId());
            if (customer != null) {
                double remaining = Math.max(0, customer.getPendingUdhari() - tx.getTotalAmount());
                customer.setPendingUdhari(remaining);
                DAOFactory.getCustomerDAO().updateCustomer(customer);
            }
            transactionDAO.updateTransaction(tx);
        }
    }

    public void deleteTransaction(TransactionModel tx) {
        if (tx != null) {
            transactions.remove(tx);
            transactionDAO.deleteTransaction(tx.getBillId());
        }
    }
}
