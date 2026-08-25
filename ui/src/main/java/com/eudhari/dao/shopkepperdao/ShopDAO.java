package com.eudhari.dao.shopkepperdao;

import com.eudhari.model.ShopModel;
import java.util.List;

public interface ShopDAO {
    void saveShop(ShopModel shop);
    ShopModel getShopById(String shopId);
    ShopModel getShopByOwnerId(String ownerId);
    List<ShopModel> getAllShops();
    void updateShop(ShopModel shop);
    int getShopCount();
}
