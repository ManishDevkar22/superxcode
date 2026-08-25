package com.eudhari.controller;

import com.eudhari.dao.BillingDAO;
import com.eudhari.dao.FirestoreBillingDAO;
import com.eudhari.dao.FirestoreUdhariDAO;
import com.eudhari.dao.UdhariDAO;
import com.eudhari.model.BillingModel;
import com.eudhari.model.UdhariModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UdhariController {
    private static UdhariController instance;
    private final UdhariDAO udhariDAO;
    private final BillingDAO billingDAO;

    private UdhariController() {
        this.udhariDAO = new FirestoreUdhariDAO();
        this.billingDAO = new FirestoreBillingDAO();
    }

    public static synchronized UdhariController getInstance() {
        if (instance == null) {
            instance = new UdhariController();
        }
        return instance;
    }

    public UdhariModel createUdhariRecord(BillingModel billing) {
        if (billing == null || billing.getBillingId() == null) return null;

        int count = udhariDAO.getUdhariCount();
        String udhariId = String.format("UDH%03d", count + 1);
        String timestamp = Instant.now().toString();

        UdhariModel udhari = new UdhariModel(
                udhariId,
                billing.getBillingId(),
                billing.getOrderId(),
                billing.getCustomerId(),
                billing.getCustomerName(),
                billing.getShopId(),
                billing.getShopName(),
                billing.getTotalAmount(),
                0.0,
                billing.getTotalAmount(),
                "PENDING",
                timestamp,
                timestamp
        );

        udhariDAO.saveUdhari(udhari);

        // Trigger notification to Customer
        NotificationController.getInstance().sendNotification(
                billing.getCustomerId(),
                "customer",
                billing.getShopkeeperId(),
                "shopkeeper",
                "UDHARI",
                "New Udhari Credit Record Added",
                billing.getShopName() + " created an Udhari record of ₹" + String.format("%.2f", billing.getTotalAmount()) + " for Bill #" + billing.getBillingId(),
                udhariId
        );

        return udhari;
    }

    public boolean payUdhari(String udhariId, double amountPaid) {
        if (udhariId == null || udhariId.trim().isEmpty() || amountPaid <= 0) return false;

        UdhariModel udhari = udhariDAO.getUdhariById(udhariId);
        if (udhari == null) {
            System.err.println("[UdhariController] Udhari record not found: " + udhariId);
            return false;
        }

        double newPaid = udhari.getPaidAmount() + amountPaid;
        double newRemaining = Math.max(0.0, udhari.getTotalAmount() - newPaid);
        udhari.setPaidAmount(newPaid);
        udhari.setRemainingAmount(newRemaining);

        String newStatus = newRemaining <= 0 ? "PAID" : "PARTIALLY_PAID";
        udhari.setStatus(newStatus);
        udhari.setUpdatedAt(Instant.now().toString());

        udhariDAO.updateUdhari(udhari);

        // If fully paid, update related billing payment status to PAID
        if ("PAID".equalsIgnoreCase(newStatus) && udhari.getBillingId() != null && !udhari.getBillingId().isEmpty()) {
            billingDAO.updateBillingStatus(udhari.getBillingId(), "PAID");
        }

        // Notify both Customer and Shopkeeper
        NotificationController.getInstance().sendNotification(
                udhari.getCustomerId(),
                "customer",
                udhari.getShopId(),
                "shopkeeper",
                "UDHARI",
                "Udhari Payment Received",
                "Payment of ₹" + String.format("%.2f", amountPaid) + " recorded for " + udhari.getShopName() + ". Remaining: ₹" + String.format("%.2f", newRemaining),
                udhariId
        );

        return true;
    }

    public List<UdhariModel> getUdhariForCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) return new ArrayList<>();
        return udhariDAO.getUdhariByCustomer(customerId);
    }

    public List<UdhariModel> getUdhariForShop(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) return new ArrayList<>();
        return udhariDAO.getUdhariByShop(shopId);
    }

    public List<UdhariModel> getUdhariForCustomerAndShop(String customerId, String shopId) {
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return udhariDAO.getUdhariByCustomerAndShop(customerId, shopId);
    }

    public double getTotalRemainingUdhariForCustomer(String customerId, String shopId) {
        List<UdhariModel> records = getUdhariForCustomerAndShop(customerId, shopId);
        double sum = 0.0;
        for (UdhariModel u : records) {
            sum += u.getRemainingAmount();
        }
        return sum;
    }

    public double getTotalRemainingUdhariForCustomer(String customerId) {
        List<UdhariModel> records = getUdhariForCustomer(customerId);
        double sum = 0.0;
        for (UdhariModel u : records) {
            sum += u.getRemainingAmount();
        }
        return sum;
    }

    public UdhariModel getUdhariById(String udhariId) {
        if (udhariId == null || udhariId.trim().isEmpty()) return null;
        return udhariDAO.getUdhariById(udhariId);
    }
}
