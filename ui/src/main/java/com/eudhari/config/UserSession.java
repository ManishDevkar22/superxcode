package com.eudhari.config;

import com.eudhari.model.UserModel;

/**
 * Singleton class to hold the active user session.
 */
public class UserSession {
    private static UserSession instance;
    private UserModel currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserModel currentUser) {
        UserModel oldUser = this.currentUser;
        this.currentUser = currentUser;
        if (oldUser == null || currentUser == null || oldUser.getUid() == null || !oldUser.getUid().equals(currentUser.getUid())) {
            reloadStores();
        }
    }

    public void clear() {
        this.currentUser = null;
        reloadStores();
    }

    public void reloadStores() {
        try {
            com.eudhari.model.shopkeppermodel.ProductStore.getInstance().loadFromDAO();
            com.eudhari.model.shopkeppermodel.CustomerStore.getInstance().loadFromDAO();
            com.eudhari.model.shopkeppermodel.TransactionStore.getInstance().loadFromDAO();
            com.eudhari.controller.shopkeppercontroller.BillingController.getInstance().clearBasket();
        } catch (Exception e) {
            System.err.println("[UserSession] Error reloading stores: " + e.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null && currentUser.getUid() != null && !currentUser.getUid().trim().isEmpty();
    }
}
