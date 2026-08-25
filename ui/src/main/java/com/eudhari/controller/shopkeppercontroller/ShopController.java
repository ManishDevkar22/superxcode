package com.eudhari.controller.shopkeppercontroller;

import com.eudhari.dao.shopkepperdao.DAOFactory;
import com.eudhari.dao.shopkepperdao.ShopDAO;
import com.eudhari.model.ShopModel;

import java.time.Instant;
import java.util.List;

public class ShopController {
    private static ShopController instance;
    private final ShopDAO shopDAO;

    private final java.util.Map<String, ShopModel> ownerShopCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, ShopModel> idShopCache = new java.util.concurrent.ConcurrentHashMap<>();

    private ShopController() {
        this.shopDAO = DAOFactory.getShopDAO();
    }

    public static synchronized ShopController getInstance() {
        if (instance == null) {
            instance = new ShopController();
        }
        return instance;
    }

    public void clearCache() {
        ownerShopCache.clear();
        idShopCache.clear();
    }

    public ShopModel createShop(String shopName, String ownerId, String ownerName, String address, String businessCategory, String gpayId) {
        int count = shopDAO.getShopCount();
        String shopId = String.format("SH%02d", count + 1);
        String createdAt = Instant.now().toString();
        String status = "ACTIVE";

        ShopModel shop = new ShopModel(
                shopId,
                shopName != null ? shopName.trim() : "",
                ownerId != null ? ownerId.trim() : "",
                ownerName != null ? ownerName.trim() : "",
                address != null ? address.trim() : "",
                businessCategory != null ? businessCategory.trim() : "",
                gpayId != null ? gpayId.trim() : "",
                status,
                createdAt
        );

        shopDAO.saveShop(shop);
        if (shop.getOwnerId() != null && !shop.getOwnerId().isBlank()) {
            ownerShopCache.put(shop.getOwnerId().trim(), shop);
        }
        if (shop.getShopId() != null && !shop.getShopId().isBlank()) {
            idShopCache.put(shop.getShopId().trim(), shop);
        }
        return shop;
    }

    public ShopModel getShopByOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            return null;
        }
        String key = ownerId.trim();
        if (ownerShopCache.containsKey(key)) {
            return ownerShopCache.get(key);
        }
        ShopModel shop = shopDAO.getShopByOwnerId(key);
        if (shop != null) {
            ownerShopCache.put(key, shop);
            if (shop.getShopId() != null && !shop.getShopId().isBlank()) {
                idShopCache.put(shop.getShopId().trim(), shop);
            }
        }
        return shop;
    }

    public ShopModel getShopById(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) {
            return null;
        }
        String key = shopId.trim();
        if (idShopCache.containsKey(key)) {
            return idShopCache.get(key);
        }
        ShopModel shop = shopDAO.getShopById(key);
        if (shop != null) {
            idShopCache.put(key, shop);
            if (shop.getOwnerId() != null && !shop.getOwnerId().isBlank()) {
                ownerShopCache.put(shop.getOwnerId().trim(), shop);
            }
        }
        return shop;
    }

    public List<ShopModel> getAllShops() {
        return shopDAO.getAllShops();
    }

    public void updateShop(ShopModel shop) {
        if (shop != null) {
            shopDAO.updateShop(shop);
            if (shop.getOwnerId() != null && !shop.getOwnerId().isBlank()) {
                ownerShopCache.put(shop.getOwnerId().trim(), shop);
            }
            if (shop.getShopId() != null && !shop.getShopId().isBlank()) {
                idShopCache.put(shop.getShopId().trim(), shop);
            }
        }
    }
}
