package com.eudhari.dao;

import com.eudhari.model.OrderModel;
import java.util.List;

public interface OrderDAO {
    void saveOrder(OrderModel order);

    void updateOrderStatus(String orderId, String status);

    OrderModel getOrderById(String orderId);

    List<OrderModel> getOrdersByCustomer(String customerId);

    List<OrderModel> getOrdersByShopkeeper(String shopkeeperId);

    List<OrderModel> getOrdersByShop(String shopId);

    int getOrderCount();
}
