package com.eudhari.dao;

import com.eudhari.model.ConnectionRequestModel;
import java.util.List;

public interface ConnectionRequestDAO {
    void saveRequest(ConnectionRequestModel request);
    void updateRequestStatus(String requestId, String status);
    ConnectionRequestModel getRequestById(String requestId);
    List<ConnectionRequestModel> getRequestsByCustomer(String customerId);
    List<ConnectionRequestModel> getRequestsByShopkeeper(String shopkeeperId);
    List<ConnectionRequestModel> getApprovedRequestsForCustomer(String customerId);
    List<ConnectionRequestModel> getApprovedRequestsForShop(String shopId);
    List<ConnectionRequestModel> getApprovedRequestsForShopkeeper(String shopkeeperId);
    boolean hasExistingRequest(String customerId, String shopId);
    ConnectionRequestModel getRequestByCustomerAndShop(String customerId, String shopId);
    int getRequestCount();
}
