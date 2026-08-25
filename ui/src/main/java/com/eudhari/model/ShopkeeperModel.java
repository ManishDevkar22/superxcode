package com.eudhari.model;

import javafx.beans.property.*;

public class ShopkeeperModel {
    private final StringProperty shopkeeperId;
    private final StringProperty shopName;
    private final StringProperty ownerName;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty address;
    private final StringProperty registrationDate;
    private final StringProperty status;
    private final IntegerProperty transactionsCount;
    private final StringProperty volumeAmount;

    public ShopkeeperModel(String shopkeeperId, String shopName, String ownerName, String email, String phone, String address, String registrationDate, String status) {
        this(shopkeeperId, shopName, ownerName, email, phone, address, registrationDate, status, 0, "Rs 0");
    }

    public ShopkeeperModel(String shopkeeperId, String shopName, String ownerName, String email, String phone, String address, String registrationDate, String status, int transactionsCount, String volumeAmount) {
        this.shopkeeperId = new SimpleStringProperty(shopkeeperId);
        this.shopName = new SimpleStringProperty(shopName);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.registrationDate = new SimpleStringProperty(registrationDate);
        this.status = new SimpleStringProperty(status);
        this.transactionsCount = new SimpleIntegerProperty(transactionsCount);
        this.volumeAmount = new SimpleStringProperty(volumeAmount);
    }

    public String getShopkeeperId() { return shopkeeperId.get(); }
    public void setShopkeeperId(String v) { this.shopkeeperId.set(v); }
    public StringProperty shopkeeperIdProperty() { return shopkeeperId; }

    public String getShopName() { return shopName.get(); }
    public void setShopName(String v) { this.shopName.set(v); }
    public StringProperty shopNameProperty() { return shopName; }

    public String getOwnerName() { return ownerName.get(); }
    public void setOwnerName(String v) { this.ownerName.set(v); }
    public StringProperty ownerNameProperty() { return ownerName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { this.email.set(v); }
    public StringProperty emailProperty() { return email; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String v) { this.phone.set(v); }
    public StringProperty phoneProperty() { return phone; }

    public String getAddress() { return address.get(); }
    public void setAddress(String v) { this.address.set(v); }
    public StringProperty addressProperty() { return address; }

    public String getRegistrationDate() { return registrationDate.get(); }
    public void setRegistrationDate(String v) { this.registrationDate.set(v); }
    public StringProperty registrationDateProperty() { return registrationDate; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }

    public int getTransactionsCount() { return transactionsCount.get(); }
    public void setTransactionsCount(int v) { this.transactionsCount.set(v); }
    public IntegerProperty transactionsCountProperty() { return transactionsCount; }

    public String getVolumeAmount() { return volumeAmount.get(); }
    public void setVolumeAmount(String v) { this.volumeAmount.set(v); }
    public StringProperty volumeAmountProperty() { return volumeAmount; }
}
