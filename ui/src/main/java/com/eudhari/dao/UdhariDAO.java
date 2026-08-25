package com.eudhari.dao;

import com.eudhari.model.UdhariModel;
import java.util.List;

public interface UdhariDAO {
    void saveUdhari(UdhariModel udhari);

    void updateUdhari(UdhariModel udhari);

    UdhariModel getUdhariById(String udhariId);

    UdhariModel getUdhariByBillingId(String billingId);

    List<UdhariModel> getUdhariByCustomer(String customerId);

    List<UdhariModel> getUdhariByShop(String shopId);

    List<UdhariModel> getUdhariByCustomerAndShop(String customerId, String shopId);

    int getUdhariCount();
}
