package com.eudhari.controller.shopkeppercontroller;

import com.eudhari.dao.shopkepperdao.*;
// import com.eudhari.dao.DAOFactory;
import com.eudhari.model.shopkeppermodel.*;
// import com.eudhari.model.CustomerStore;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerController {
    private static CustomerController instance;
    private final CustomerDAO customerDAO;
    private final CustomerStore customerStore;

    private CustomerController() {
        this.customerDAO = DAOFactory.getCustomerDAO();
        this.customerStore = CustomerStore.getInstance();
    }

    public static synchronized CustomerController getInstance() {
        if (instance == null) {
            instance = new CustomerController();
        }
        return instance;
    }

    public ObservableList<CustomerModel> getAllCustomers() {
        return customerStore.getAllCustomers();
    }

    public FilteredList<CustomerModel> getActiveCustomers() {
        return customerStore.getActiveCustomers();
    }

    public String generateUniqueCustomerId() {
        return customerStore.generateUniqueCustomerId();
    }

    public CustomerModel addCustomer(String name, String phone, String status) {
        String id = generateUniqueCustomerId();
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        CustomerModel c = new CustomerModel(id, name, phone, todayStr, status != null ? status : "New Customer", 0.0,
                0.0);

        // 1. Model update
        customerStore.getAllCustomers().add(c);

        // 2. DAO -> Firestore
        customerDAO.saveCustomer(c);
        return c;
    }

    public void updateCustomer(CustomerModel c, String name, String phone, String status) {
        if (c != null) {
            c.setName(name);
            c.setPhone(phone);
            if (status != null && !status.isBlank()) {
                c.setStatus(status);
            }

            // DAO -> Firestore
            customerDAO.updateCustomer(c);
        }
    }

    public void deleteCustomer(CustomerModel c) {
        if (c != null) {
            customerStore.getAllCustomers().remove(c);
            customerDAO.deleteCustomer(c.getId());
        }
    }

    public CustomerModel getCustomerById(String id) {
        return customerStore.getCustomerById(id);
    }
}
