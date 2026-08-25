package com.eudhari.model.shopkeppermodel;

import javafx.beans.property.*;

public class TransactionModel {
    private final StringProperty billId;
    private final StringProperty shopId;
    private final StringProperty orderId;
    private final StringProperty customerId;
    private final StringProperty customerName;
    private final StringProperty itemsSummary;
    private final IntegerProperty totalQuantity;
    private final DoubleProperty totalAmount;
    private final StringProperty paymentMethod;
    private final StringProperty dateTime;
    private final StringProperty status;

    public TransactionModel(String billId, String customerId, String customerName, String itemsSummary,
            int totalQuantity, double totalAmount, String paymentMethod, String dateTime, String status) {
        this.billId = new SimpleStringProperty(billId);
        this.shopId = new SimpleStringProperty("");
        this.orderId = new SimpleStringProperty("");
        this.customerId = new SimpleStringProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.itemsSummary = new SimpleStringProperty(itemsSummary);
        this.totalQuantity = new SimpleIntegerProperty(totalQuantity);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.status = new SimpleStringProperty(status);
    }

    public String getBillId() {
        return billId.get();
    }

    public String getBillingId() {
        return getBillId();
    }

    public StringProperty billIdProperty() {
        return billId;
    }

    public String getShopId() { return shopId.get(); }
    public void setShopId(String v) { this.shopId.set(v != null ? v : ""); }
    public StringProperty shopIdProperty() { return shopId; }

    public String getOrderId() { return orderId.get(); }
    public void setOrderId(String v) { this.orderId.set(v != null ? v : ""); }
    public StringProperty orderIdProperty() { return orderId; }

    public String getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(String customerId) {
        this.customerId.set(customerId);
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public String getItemsSummary() {
        return itemsSummary.get();
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary.set(itemsSummary);
    }

    public StringProperty itemsSummaryProperty() {
        return itemsSummary;
    }

    public int getTotalQuantity() {
        return totalQuantity.get();
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity.set(totalQuantity);
    }

    public IntegerProperty totalQuantityProperty() {
        return totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount.set(totalAmount);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
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

    public String getFormattedDisplay() {
        return getBillId() + " — " + getItemsSummary() + " (" + getCustomerName() + " [" + getCustomerId() + "] - ₹"
                + (int) getTotalAmount() + " - " + getPaymentMethod() + ")";
    }
}
