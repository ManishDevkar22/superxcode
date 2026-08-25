package com.eudhari.dao.shopkepperdao;

import com.eudhari.model.shopkeppermodel.CustomerModel;
import java.util.List;

public interface CustomerDAO {
    List<CustomerModel> getAllCustomers();

    List<CustomerModel> getCustomersByShopId(String shopId);

    CustomerModel getCustomerById(String id);

    void saveCustomer(CustomerModel customer);

    void updateCustomer(CustomerModel customer);

    void deleteCustomer(String id);
}
