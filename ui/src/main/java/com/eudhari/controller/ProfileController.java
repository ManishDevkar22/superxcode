package com.eudhari.controller;

import com.eudhari.config.UserSession;
import com.eudhari.dao.FirestoreUserDAO;
import com.eudhari.dao.UserDAO;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;

public class ProfileController {
    private static ProfileController instance;
    private final UserDAO userDAO;
    private final ShopController shopController;

    private ProfileController() {
        this.userDAO = new FirestoreUserDAO();
        this.shopController = ShopController.getInstance();
    }

    public static synchronized ProfileController getInstance() {
        if (instance == null) {
            instance = new ProfileController();
        }
        return instance;
    }

    public UserModel getCurrentUserProfile() {
        return UserSession.getInstance().getCurrentUser();
    }

    public UserModel refreshCurrentUserProfile() {
        UserModel sessionUser = UserSession.getInstance().getCurrentUser();
        if (sessionUser == null || sessionUser.getUid() == null) {
            return null;
        }
        UserModel latestUser = userDAO.getUserById(sessionUser.getUid());
        if (latestUser != null) {
            UserSession.getInstance().setCurrentUser(latestUser);
            return latestUser;
        }
        return sessionUser;
    }

    public UserModel updateCustomerProfile(String uid, String name, String phone, String address) {
        if (uid == null || uid.trim().isEmpty()) {
            return null;
        }
        UserModel user = userDAO.getUserById(uid);
        if (user == null) {
            user = UserSession.getInstance().getCurrentUser();
        }
        if (user == null) {
            return null;
        }

        if (name != null) user.setName(name.trim());
        if (phone != null) user.setPhone(phone.trim());
        if (address != null) user.setAddress(address.trim());

        userDAO.updateUser(user);
        UserSession.getInstance().setCurrentUser(user);
        return user;
    }

    public UserModel updateShopkeeperProfile(String uid, String name, String phone, String address, String shopName, String businessCategory, String gpayId) {
        if (uid == null || uid.trim().isEmpty()) {
            return null;
        }
        UserModel user = userDAO.getUserById(uid);
        if (user == null) {
            user = UserSession.getInstance().getCurrentUser();
        }
        if (user == null) {
            return null;
        }

        if (name != null) {
            user.setName(name.trim());
            user.setOwnerName(name.trim());
        }
        if (phone != null) user.setPhone(phone.trim());
        if (address != null) user.setAddress(address.trim());
        if (shopName != null) user.setShopName(shopName.trim());
        if (businessCategory != null) user.setBusinessCategory(businessCategory.trim());
        if (gpayId != null) user.setGpayId(gpayId.trim());

        userDAO.updateUser(user);
        UserSession.getInstance().setCurrentUser(user);

        // Also update linked shop document in shops collection
        ShopModel shop = shopController.getShopByOwnerId(uid);
        if (shop != null) {
            if (shopName != null) shop.setShopName(shopName.trim());
            if (name != null) shop.setOwnerName(name.trim());
            if (address != null) shop.setAddress(address.trim());
            if (businessCategory != null) shop.setBusinessCategory(businessCategory.trim());
            if (gpayId != null) shop.setGpayId(gpayId.trim());
            shopController.updateShop(shop);
        }

        return user;
    }
}
