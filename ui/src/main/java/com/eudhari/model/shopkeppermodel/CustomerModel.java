package com.eudhari.model.shopkeppermodel;

import javafx.beans.property.*;

public class CustomerModel {
    private final StringProperty id;
    private final StringProperty uid;
    private final StringProperty shopId;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty joinedDate;
    private final StringProperty connectedShop;
    private final StringProperty status;
    private final DoubleProperty totalPurchases;
    private final DoubleProperty pendingUdhari;
    private final DoubleProperty udhariLimit;
    private final BooleanProperty deleted;

    public CustomerModel(String id, String name, String phone, String joinedDate, String status, double totalPurchases,
            double pendingUdhari) {
        this(id, name, name.toLowerCase().replace(" ", "") + "@gmail.com", phone, "", joinedDate, status,
                totalPurchases, pendingUdhari, 5000.0);
    }

    public CustomerModel(String id, String name, String email, String phone, String connectedShop, String balStr,
            String date, String status) {
        this(id, name, email, phone, connectedShop, date, status, 0.0, parseDouble(balStr), 5000.0);
    }

    public CustomerModel(String id, String name, String email, String phone, String connectedShop, String joinedDate,
            String status, double totalPurchases, double pendingUdhari) {
        this(id, name, email, phone, connectedShop, joinedDate, status, totalPurchases, pendingUdhari, 5000.0);
    }

    public CustomerModel(String id, String name, String email, String phone, String connectedShop, String joinedDate,
            String status, double totalPurchases, double pendingUdhari, double udhariLimit) {
        this.id = new SimpleStringProperty(id);
        this.uid = new SimpleStringProperty("");
        this.shopId = new SimpleStringProperty("");
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email != null ? email : "");
        this.phone = new SimpleStringProperty(phone);
        this.connectedShop = new SimpleStringProperty(connectedShop != null ? connectedShop : "");
        this.joinedDate = new SimpleStringProperty(joinedDate);
        this.status = new SimpleStringProperty(status);
        this.totalPurchases = new SimpleDoubleProperty(totalPurchases);
        this.pendingUdhari = new SimpleDoubleProperty(pendingUdhari);
        this.udhariLimit = new SimpleDoubleProperty(udhariLimit > 0 ? udhariLimit : 5000.0);
        this.deleted = new SimpleBooleanProperty(false);
    }

    public String getUid() { return uid.get(); }
    public void setUid(String v) { this.uid.set(v != null ? v : ""); }
    public StringProperty uidProperty() { return uid; }

    public String getShopId() { return shopId.get(); }
    public void setShopId(String v) { this.shopId.set(v != null ? v : ""); }
    public StringProperty shopIdProperty() { return shopId; }

    private static double parseDouble(String str) {
        if (str == null)
            return 0.0;
        try {
            return Double.parseDouble(str.replace("Rs", "").replace("₹", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public String getId() {
        return id.get();
    }

    public String getCustomerId() {
        return getId();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public String getCustomerName() {
        return getName();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setCustomerName(String name) {
        setName(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getConnectedShop() {
        return connectedShop.get();
    }

    public void setConnectedShop(String shop) {
        this.connectedShop.set(shop);
    }

    public StringProperty connectedShopProperty() {
        return connectedShop;
    }

    public String getJoinedDate() {
        return joinedDate.get();
    }

    public String getRegistrationDate() {
        return getJoinedDate();
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate.set(joinedDate);
    }

    public void setRegistrationDate(String regDate) {
        setJoinedDate(regDate);
    }

    public StringProperty joinedDateProperty() {
        return joinedDate;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public double getTotalPurchases() {
        return totalPurchases.get();
    }

    public void setTotalPurchases(double totalPurchases) {
        this.totalPurchases.set(totalPurchases);
    }

    public DoubleProperty totalPurchasesProperty() {
        return totalPurchases;
    }

    public double getPendingUdhari() {
        return pendingUdhari.get();
    }

    public String getUdhariBalance() {
        return String.format("Rs %.2f", getPendingUdhari());
    }

    public void setPendingUdhari(double pendingUdhari) {
        this.pendingUdhari.set(pendingUdhari);
    }

    public DoubleProperty pendingUdhariProperty() {
        return pendingUdhari;
    }

    public double getUdhariLimit() {
        return udhariLimit.get();
    }

    public void setUdhariLimit(double limit) {
        this.udhariLimit.set(limit);
    }

    public DoubleProperty udhariLimitProperty() {
        return udhariLimit;
    }

    public boolean isUdhariLimitReached() {
        return getPendingUdhari() >= getUdhariLimit();
    }

    public boolean isDeleted() {
        return deleted.get();
    }

    public void setDeleted(boolean deleted) {
        this.deleted.set(deleted);
    }

    public BooleanProperty deletedProperty() {
        return deleted;
    }

    public String getDisplayName() {
        return name.get() + " (" + id.get() + ")";
    }
}
