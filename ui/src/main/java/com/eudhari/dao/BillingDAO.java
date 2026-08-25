package com.eudhari.dao;

import com.eudhari.model.BillingModel;
import java.util.List;

public interface BillingDAO {
    void saveBilling(BillingModel billing);

    void updateBillingStatus(String billingId, String paymentStatus);

    BillingModel getBillingById(String billingId);

    List<BillingModel> getBillingByCustomer(String customerId);

    List<BillingModel> getBillingByShop(String shopId);

    List<BillingModel> getBillingByCustomerAndShop(String customerId, String shopId);

    List<BillingModel> getAllBillings();

    int getBillingCount();
}
