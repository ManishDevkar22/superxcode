package com.eudhari.model.shopkeppermodel;

import com.eudhari.config.UserSession;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.dao.shopkepperdao.*;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerStore {
    private static CustomerStore instance;

    private final CustomerDAO customerDAO;
    private final ObservableList<CustomerModel> customers = FXCollections.observableArrayList();
    private final FilteredList<CustomerModel> activeCustomers;
    private int customerCounter = 1007;

    private CustomerStore() {
        this.customerDAO = DAOFactory.getCustomerDAO();
        activeCustomers = new FilteredList<>(customers, c -> !c.isDeleted());
        loadFromDAO();
    }

    public static synchronized CustomerStore getInstance() {
        if (instance == null) {
            instance = new CustomerStore();
        }
        return instance;
    }

    public void loadFromDAO() {
        new Thread(() -> {
            UserModel user = UserSession.getInstance().getCurrentUser();
            java.util.List<CustomerModel> fetched = new java.util.ArrayList<>();
            if (user != null && "shopkeeper".equalsIgnoreCase(user.getRole())) {
                ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
                if (shop != null && shop.getShopId() != null && !shop.getShopId().isEmpty()) {
                    fetched.addAll(customerDAO.getCustomersByShopId(shop.getShopId()));
                }
            } else if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
                fetched.addAll(customerDAO.getAllCustomers());
            }
            final java.util.List<CustomerModel> resultList = fetched;
            javafx.application.Platform.runLater(() -> {
                customers.clear();
                customers.addAll(resultList);
                customerCounter = 1000 + customers.size();
            });
        }).start();
    }

    public ObservableList<CustomerModel> getAllCustomers() {
        return customers;
    }

    public FilteredList<CustomerModel> getActiveCustomers() {
        return activeCustomers;
    }

    public String generateUniqueCustomerId() {
        customerCounter++;
        return "CUST-" + customerCounter;
    }

    public CustomerModel addCustomer(String name, String phone, String status) {
        String id = generateUniqueCustomerId();
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        CustomerModel c = new CustomerModel(id, name, phone, todayStr, status != null ? status : "New Customer", 0.0,
                0.0);
        UserModel user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            ShopModel shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
            if (shop != null && shop.getShopId() != null) {
                c.setShopId(shop.getShopId());
                c.setUid(user.getUid());
                c.setConnectedShop(shop.getShopName());
            }
        }
        customers.add(c);
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
            customerDAO.updateCustomer(c);
        }
    }

    public void deleteCustomer(CustomerModel c) {
        if (c != null) {
            customers.remove(c);
            customerDAO.deleteCustomer(c.getId());
        }
    }

    public CustomerModel getCustomerById(String id) {
        if (id == null)
            return null;
        for (CustomerModel c : customers) {
            if (c.getId().equalsIgnoreCase(id.trim())) {
                return c;
            }
        }
        return null;
    }
}
