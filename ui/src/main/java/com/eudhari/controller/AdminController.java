package com.eudhari.controller;

import com.eudhari.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import com.eudhari.model.shopkeppermodel.*;

public class AdminController {
    private final ObservableList<ShopkeeperModel> shopkeepers = FXCollections.observableArrayList();
    private final ObservableList<CustomerModel> customers = FXCollections.observableArrayList();
    private final ObservableList<ProductModel> products = FXCollections.observableArrayList();
    private final ObservableList<ActivityModel> activities = FXCollections.observableArrayList();
    private final ObservableList<ReportModel> reports = FXCollections.observableArrayList();
    private AdminModel adminProfile;

    private final com.eudhari.dao.FirestoreUserDAO userDAO = new com.eudhari.dao.FirestoreUserDAO();
    private final com.eudhari.dao.shopkepperdao.FirestoreShopDAO shopDAO = new com.eudhari.dao.shopkepperdao.FirestoreShopDAO();

    public AdminController() {
        initSampleData();
        loadShopkeepersFromFirestore();
    }

    public void loadShopkeepersFromFirestore() {
        try {
            shopkeepers.clear();
            java.util.List<UserModel> userList = userDAO.getUsersByRole("shopkeeper");
            java.util.List<ShopModel> shopList = shopDAO.getAllShops();

            java.util.Map<String, ShopModel> shopByOwnerMap = new java.util.HashMap<>();
            if (shopList != null) {
                for (ShopModel s : shopList) {
                    if (s.getOwnerId() != null) {
                        shopByOwnerMap.put(s.getOwnerId(), s);
                    }
                    if (s.getShopId() != null) {
                        shopByOwnerMap.put(s.getShopId(), s);
                    }
                }
            }

            if (userList != null) {
                for (UserModel u : userList) {
                    ShopModel matchingShop = shopByOwnerMap.get(u.getUid());
                    String shopName = matchingShop != null && matchingShop.getShopName() != null ? matchingShop.getShopName() : (u.getShopName() != null && !u.getShopName().isBlank() ? u.getShopName() : u.getName() + " Store");
                    String address = matchingShop != null && matchingShop.getAddress() != null ? matchingShop.getAddress() : (u.getAddress() != null ? u.getAddress() : "Pune");
                    String regDate = u.getCreatedAt() != null && !u.getCreatedAt().isBlank() ? u.getCreatedAt() : "18 Aug 2026";
                    String status = u.getStatus() != null && !u.getStatus().isBlank() ? u.getStatus() : "Active";

                    ShopkeeperModel sk = new ShopkeeperModel(
                            u.getUid(),
                            shopName,
                            u.getName() != null ? u.getName() : "Owner",
                            u.getEmail() != null ? u.getEmail() : "",
                            u.getPhone() != null ? u.getPhone() : "",
                            address,
                            regDate,
                            status,
                            0,
                            "Rs 0"
                    );
                    shopkeepers.add(sk);
                }
            }
        } catch (Exception e) {
            System.err.println("[AdminController] Error loading shopkeepers from Firestore: " + e.getMessage());
        }
    }

    private void initSampleData() {
        // Customers
        customers.addAll(
                new CustomerModel("CU001", "Omkar Sonawane", "omkar@gmail.com", "+91 98765 11111", "Goroba Kirana",
                        "10 Jan 2025", "Active", 15000, 0),
                new CustomerModel("CU002", "Manish Patil", "manish@gmail.com", "+91 98765 22222", "Sai Kirana",
                        "15 Feb 2025", "Active", 8500, 1200),
                new CustomerModel("CU003", "Rahul Verma", "rahul@gmail.com", "+91 98765 33333", "Saishradha Kirana",
                        "01 Mar 2025", "Overdue", 12000, 3500));

        // Products
        products.addAll(
                new ProductModel("PR001", "Basmati Rice", "Grocery", "Goroba Kirana", "Rs 60.00", 250, "Available"),
                new ProductModel("PR002", "Sunflower Oil 1L", "Grocery", "Sai Kirana", "Rs 135.00", 80, "Available"),
                new ProductModel("PR003", "Whole Wheat Flour", "Grocery", "Saishradha Kirana", "Rs 45.00", 15,
                        "Low Stock"));

        // Activities
        activities.addAll(
                new ActivityModel("payment", "Manish Patil repaid Rs 1,200 Udhari at Sai Kirana", "5 mins ago"),
                new ActivityModel("udhari", "Rahul Verma created Udhari of Rs 850 at Saishradha Kirana", "25 mins ago"),
                new ActivityModel("user", "New customer registration: Priya Sharma", "1 hour ago"));

        // Reports
        reports.addAll(
                new ReportModel("REP-2026-08-01", "Monthly Financial Summary", "18 Aug 2026", "System Admin",
                        "Completed", "2.4 MB"),
                new ReportModel("REP-2026-08-02", "Shopkeeper Settlement Audit", "15 Aug 2026", "System Admin",
                        "Completed", "1.8 MB"),
                new ReportModel("REP-2026-08-03", "Platform Risk & Credit Analysis", "10 Aug 2026", "System Admin",
                        "Completed", "3.1 MB"));

        // Admin Profile
        adminProfile = new AdminModel("System Admin", "admin@eudhari.com", "+91 98000 00000", "Super Administrator");
    }

    // Shopkeeper operations
    public ObservableList<ShopkeeperModel> getAllShopkeepers() {
        return shopkeepers;
    }

    public FilteredList<ShopkeeperModel> filterShopkeepers(String query, String status) {
        FilteredList<ShopkeeperModel> filtered = new FilteredList<>(shopkeepers);
        filtered.setPredicate(s -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    s.getShopName().toLowerCase().contains(query.toLowerCase()) ||
                    s.getOwnerName().toLowerCase().contains(query.toLowerCase()) ||
                    s.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    s.getPhone().contains(query);
            boolean matchesStatus = status == null || status.equals("All Statuses")
                    || s.getStatus().equalsIgnoreCase(status);
            return matchesQuery && matchesStatus;
        });
        return filtered;
    }

    public void saveOrUpdateShopkeeper(ShopkeeperModel model, boolean isNew) {
        if (isNew) {
            shopkeepers.add(model);
        }
        // Sync to Firestore
        try {
            UserModel user = new UserModel(
                    model.getShopkeeperId(),
                    model.getOwnerName(),
                    model.getEmail(),
                    model.getPhone(),
                    "shopkeeper",
                    model.getRegistrationDate()
            );
            user.setStatus(model.getStatus());
            user.setAddress(model.getAddress());
            user.setShopName(model.getShopName());
            userDAO.saveUser(user);

            ShopModel shop = new ShopModel(
                    model.getShopkeeperId(),
                    model.getShopName(),
                    model.getShopkeeperId(),
                    model.getOwnerName(),
                    model.getAddress(),
                    model.getPhone(),
                    "General Store",
                    model.getStatus(),
                    model.getRegistrationDate()
            );
            shopDAO.saveShop(shop);
        } catch (Exception e) {
            System.err.println("[AdminController] Error saving shopkeeper to Firestore: " + e.getMessage());
        }
    }

    public void deleteShopkeeper(ShopkeeperModel model) {
        shopkeepers.remove(model);
        try {
            userDAO.updateUserStatus(model.getShopkeeperId(), "Suspended");
        } catch (Exception e) {
            System.err.println("[AdminController] Error deleting/suspending shopkeeper in Firestore: " + e.getMessage());
        }
    }

    // Customer operations
    public ObservableList<CustomerModel> getAllCustomers() {
        return customers;
    }

    public FilteredList<CustomerModel> filterCustomers(String query, String status) {
        FilteredList<CustomerModel> filtered = new FilteredList<>(customers);
        filtered.setPredicate(c -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    c.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                    c.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    c.getPhone().contains(query);
            boolean matchesStatus = status == null || status.equals("All Statuses")
                    || c.getStatus().equalsIgnoreCase(status);
            return matchesQuery && matchesStatus;
        });
        return filtered;
    }

    public void saveOrUpdateCustomer(CustomerModel model, boolean isNew) {
        if (isNew) {
            customers.add(model);
        }
    }

    public void deleteCustomer(CustomerModel model) {
        customers.remove(model);
    }

    // Product operations
    public ObservableList<ProductModel> getAllProducts() {
        return products;
    }

    public FilteredList<ProductModel> filterProducts(String query, String category) {
        FilteredList<ProductModel> filtered = new FilteredList<>(products);
        filtered.setPredicate(p -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    p.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                    p.getShopkeeper().toLowerCase().contains(query.toLowerCase()) ||
                    p.getProductId().toLowerCase().contains(query.toLowerCase());
            boolean matchesCat = category == null || category.equals("All Categories")
                    || p.getCategory().equalsIgnoreCase(category);
            return matchesQuery && matchesCat;
        });
        return filtered;
    }

    public void saveOrUpdateProduct(ProductModel model, boolean isNew) {
        if (isNew) {
            products.add(model);
        }
    }

    public void deleteProduct(ProductModel model) {
        products.remove(model);
    }

    // Activity & Report operations
    public ObservableList<ActivityModel> getAllActivities() {
        return activities;
    }

    public ObservableList<ReportModel> getAllReports() {
        return reports;
    }

    // Profile operations
    public AdminModel getAdminProfile() {
        UserModel user = com.eudhari.config.UserSession.getInstance().getCurrentUser();
        if (user != null && user.getUid() != null && !user.getUid().isBlank()) {
            try {
                UserModel latestUser = userDAO.getUserById(user.getUid());
                if (latestUser != null) {
                    user = latestUser;
                    com.eudhari.config.UserSession.getInstance().setCurrentUser(latestUser);
                }
            } catch (Exception e) {
                System.err.println("[AdminController] Error fetching admin profile from Firestore: " + e.getMessage());
            }
            String name = user.getName() != null && !user.getName().isBlank() ? user.getName() : "System Admin";
            String email = user.getEmail() != null && !user.getEmail().isBlank() ? user.getEmail() : "admin@eudhari.com";
            String phone = user.getPhone() != null && !user.getPhone().isBlank() ? user.getPhone() : "";
            String role = user.getRole() != null && !user.getRole().isBlank() ? user.getRole() : "admin";
            adminProfile = new AdminModel(name, email, phone, role);
            return adminProfile;
        }
        return adminProfile;
    }

    public void updateAdminProfile(AdminModel model) {
        this.adminProfile = model;
        UserModel user = com.eudhari.config.UserSession.getInstance().getCurrentUser();
        if (user != null && user.getUid() != null && !user.getUid().isBlank()) {
            user.setName(model.getName());
            user.setEmail(model.getEmail());
            user.setPhone(model.getPhone());
            if (model.getRole() != null && !model.getRole().isBlank()) {
                user.setRole(model.getRole());
            }
            try {
                userDAO.updateUser(user);
                com.eudhari.config.UserSession.getInstance().setCurrentUser(user);
            } catch (Exception e) {
                System.err.println("[AdminController] Error updating admin profile in Firestore: " + e.getMessage());
            }
        }
    }
}
