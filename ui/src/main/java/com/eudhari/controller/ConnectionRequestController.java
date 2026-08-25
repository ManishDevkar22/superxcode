package com.eudhari.controller;

import com.eudhari.dao.ConnectionRequestDAO;
import com.eudhari.dao.FirestoreConnectionRequestDAO;
import com.eudhari.model.ConnectionRequestModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ConnectionRequestController {
    private static ConnectionRequestController instance;
    private final ConnectionRequestDAO requestDAO;

    private ConnectionRequestController() {
        this.requestDAO = new FirestoreConnectionRequestDAO();
    }

    public static synchronized ConnectionRequestController getInstance() {
        if (instance == null) {
            instance = new ConnectionRequestController();
        }
        return instance;
    }

    public ConnectionRequestModel sendConnectionRequest(String customerId, String customerName, String shopId, String shopName, String shopkeeperId) {
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()) {
            return null;
        }

        ConnectionRequestModel existing = requestDAO.getRequestByCustomerAndShop(customerId, shopId);
        if (existing != null) {
            System.out.println("[ConnectionRequestController] Request already exists: " + existing.getRequestId() + " (" + existing.getStatus() + ")");
            return existing;
        }

        int count = requestDAO.getRequestCount();
        String requestId = String.format("REQ%02d", count + 1);
        String timestamp = Instant.now().toString();

        ConnectionRequestModel request = new ConnectionRequestModel(
                requestId,
                customerId.trim(),
                customerName != null ? customerName.trim() : "",
                shopId.trim(),
                shopName != null ? shopName.trim() : "",
                shopkeeperId != null ? shopkeeperId.trim() : "",
                "PENDING",
                timestamp,
                timestamp
        );

        requestDAO.saveRequest(request);

        // Send notification to shopkeeper
        NotificationController.getInstance().sendNotification(
                shopkeeperId,
                "shopkeeper",
                customerId,
                "customer",
                "CONNECTION",
                "New Connection Request",
                customerName + " requested to connect with your shop '" + shopName + "'.",
                requestId
        );

        return request;
    }

    public void acceptRequest(String requestId) {
        if (requestId != null && !requestId.trim().isEmpty()) {
            requestDAO.updateRequestStatus(requestId, "APPROVED");
            ConnectionRequestModel req = requestDAO.getRequestById(requestId);
            if (req != null) {
                NotificationController.getInstance().sendNotification(
                        req.getCustomerId(),
                        "customer",
                        req.getShopkeeperId(),
                        "shopkeeper",
                        "CONNECTION",
                        "Connection Request Approved",
                        "Shopkeeper for '" + req.getShopName() + "' accepted your connection request!",
                        requestId
                );
            }
        }
    }

    public void rejectRequest(String requestId) {
        if (requestId != null && !requestId.trim().isEmpty()) {
            requestDAO.updateRequestStatus(requestId, "REJECTED");
            ConnectionRequestModel req = requestDAO.getRequestById(requestId);
            if (req != null) {
                NotificationController.getInstance().sendNotification(
                        req.getCustomerId(),
                        "customer",
                        req.getShopkeeperId(),
                        "shopkeeper",
                        "CONNECTION",
                        "Connection Request Declined",
                        "Shopkeeper for '" + req.getShopName() + "' declined your connection request.",
                        requestId
                );
            }
        }
    }

    public List<ConnectionRequestModel> getPendingRequestsForShopkeeper(String shopkeeperId) {
        List<ConnectionRequestModel> pending = new ArrayList<>();
        if (shopkeeperId == null || shopkeeperId.trim().isEmpty()) {
            return pending;
        }
        for (ConnectionRequestModel req : requestDAO.getRequestsByShopkeeper(shopkeeperId)) {
            if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                pending.add(req);
            }
        }
        return pending;
    }

    public List<ConnectionRequestModel> getApprovedConnectedShopsForCustomer(String customerId) {
        return requestDAO.getApprovedRequestsForCustomer(customerId);
    }

    public List<ConnectionRequestModel> getApprovedCustomersForShopkeeper(String shopkeeperId) {
        return requestDAO.getApprovedRequestsForShopkeeper(shopkeeperId);
    }

    public List<ConnectionRequestModel> getApprovedCustomersForShop(String shopId) {
        return requestDAO.getApprovedRequestsForShop(shopId);
    }

    public List<ConnectionRequestModel> getRequestsByCustomer(String customerId) {
        return requestDAO.getRequestsByCustomer(customerId);
    }

    public String getRequestStatus(String customerId, String shopId) {
        ConnectionRequestModel req = requestDAO.getRequestByCustomerAndShop(customerId, shopId);
        if (req != null) {
            return req.getStatus() != null ? req.getStatus().toUpperCase() : "NONE";
        }
        return "NONE";
    }
}
