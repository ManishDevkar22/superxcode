package com.eudhari.controller.shopkeppercontroller;

import com.eudhari.controller.NotificationController;
import com.eudhari.controller.OrderController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.UdhariController;
import com.eudhari.dao.BillingDAO;
import com.eudhari.dao.FirestoreBillingDAO;
import com.eudhari.dao.shopkepperdao.*;
import com.eudhari.model.BillingModel;
import com.eudhari.model.OrderItemModel;
import com.eudhari.model.OrderModel;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import com.eudhari.model.shopkeppermodel.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BillingController {
    private static BillingController instance;

    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;
    private final TransactionDAO transactionDAO;
    private final BillingDAO billingDAO;

    private final ProductStore productStore;
    private final TransactionStore transactionStore;

    private final Map<String, Integer> basket = new LinkedHashMap<>();

    private BillingController() {
        this.productDAO = DAOFactory.getProductDAO();
        this.customerDAO = DAOFactory.getCustomerDAO();
        this.transactionDAO = DAOFactory.getTransactionDAO();
        this.billingDAO = new FirestoreBillingDAO();
        this.productStore = ProductStore.getInstance();
        this.transactionStore = TransactionStore.getInstance();
    }

    public static synchronized BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController();
        }
        return instance;
    }

    public Map<String, Integer> getBasket() {
        return new LinkedHashMap<>(basket);
    }

    public void addToBasket(String productName) {
        if (productName != null && !productName.isBlank()) {
            basket.merge(productName, 1, Integer::sum);
        }
    }

    public void changeQuantity(String productName, int delta) {
        if (productName == null)
            return;
        int current = basket.getOrDefault(productName, 0) + delta;
        if (current <= 0) {
            basket.remove(productName);
        } else {
            basket.put(productName, current);
        }
    }

    public void clearBasket() {
        basket.clear();
    }

    public String getBasketSummary() {
        if (basket.isEmpty())
            return "No products selected";
        return basket.entrySet().stream()
                .map(e -> e.getKey() + " x" + e.getValue())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    /**
     * Process billing for an ACCEPTED order:
     * 1. Creates BillingModel in "billing" collection.
     * 2. Sets paymentStatus = "PAID" for CASH/ONLINE, or "PENDING" for UDHARI.
     * 3. Sets Order status = "COMPLETED".
     * 4. If UDHARI, creates Udhari document in "udhari" collection.
     * 5. Sends Notification to customer.
     */
    public BillingModel processOrderBilling(String orderId, String paymentMethod) {
        if (orderId == null || orderId.trim().isEmpty()) return null;

        OrderModel order = OrderController.getInstance().getOrderById(orderId);
        if (order == null) {
            System.err.println("[BillingController] Cannot process billing: Order not found -> " + orderId);
            return null;
        }

        String method = paymentMethod != null ? paymentMethod.trim().toUpperCase() : "CASH";
        String pStatus = "UDHARI".equals(method) ? "PENDING" : "PAID";

        int count = billingDAO.getBillingCount();
        String billingId = String.format("BIL%03d", count + 1);
        String createdAt = Instant.now().toString();

        BillingModel billing = new BillingModel(
                billingId,
                order.getOrderId(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getShopId(),
                order.getShopName(),
                order.getShopkeeperId(),
                order.getItems(),
                order.getTotalAmount(),
                method,
                pStatus,
                createdAt
        );

        // Update product sales count & stock for order items
        if (order.getItems() != null) {
            for (com.eudhari.model.OrderItemModel item : order.getItems()) {
                String pName = item.getProductName();
                int qty = item.getQuantity();
                for (ProductModel pm : productStore.getAllProducts()) {
                    if (pm.getName().equalsIgnoreCase(pName) || pm.getId().equalsIgnoreCase(item.getProductId())) {
                        pm.setSalesCount(pm.getSalesCount() + qty);
                        double newStock = Math.max(0, pm.getStock() - qty);
                        pm.setStock(newStock);
                        productDAO.updateProduct(pm);
                        break;
                    }
                }
            }
        }

        // Save billing document to Firestore "billing" collection
        billingDAO.saveBilling(billing);

        // Mark related order as COMPLETED
        com.eudhari.dao.FirestoreOrderDAO orderDAO = new com.eudhari.dao.FirestoreOrderDAO();
        orderDAO.updateOrderStatus(order.getOrderId(), "COMPLETED");

        // Update customer purchases & pending udhari
        if (order.getCustomerId() != null && !order.getCustomerId().isBlank()) {
            CustomerModel cust = CustomerStore.getInstance().getCustomerById(order.getCustomerId());
            if (cust != null) {
                cust.setTotalPurchases(cust.getTotalPurchases() + order.getTotalAmount());
                if ("UDHARI".equals(method)) {
                    cust.setPendingUdhari(cust.getPendingUdhari() + order.getTotalAmount());
                }
                customerDAO.updateCustomer(cust);
            }
        }

        // If payment method is UDHARI, create Udhari record automatically
        if ("UDHARI".equals(method)) {
            UdhariController.getInstance().createUdhariRecord(billing);
        }

        // Create transaction entry for Billing History
        String itemsSummary = billing.getItemsSummary();
        int totalQty = 0;
        if (order.getItems() != null) {
            for (com.eudhari.model.OrderItemModel item : order.getItems()) {
                totalQty += item.getQuantity();
            }
        }
        String dateTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        String txStatus = "UDHARI".equals(method) ? "Udhari Pending" : "Paid";
        TransactionModel tx = new TransactionModel(billingId, order.getCustomerId(), order.getCustomerName(), itemsSummary, totalQty, order.getTotalAmount(), method, dateTime, txStatus);
        tx.setShopId(order.getShopId());
        tx.setOrderId(order.getOrderId());
        transactionDAO.saveTransaction(tx);
        transactionStore.getAllTransactions().add(0, tx);

        // Send notification to customer
        NotificationController.getInstance().sendNotification(
                order.getCustomerId(),
                "customer",
                order.getShopkeeperId(),
                "shopkeeper",
                "BILLING",
                "Bill Generated (" + method + ")",
                "Your order #" + order.getOrderId() + " was billed for ₹" + String.format("%.2f", order.getTotalAmount()) + " via " + method + " (" + pStatus + ").",
                billingId
        );

        return billing;
    }

    public List<BillingModel> getBillingForCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) return new ArrayList<>();
        return billingDAO.getBillingByCustomer(customerId);
    }

    public List<BillingModel> getBillingForShop(String shopId) {
        if (shopId == null || shopId.trim().isEmpty()) return new ArrayList<>();
        return billingDAO.getBillingByShop(shopId);
    }

    public List<BillingModel> getBillingForCustomerAndShop(String customerId, String shopId) {
        if (customerId == null || customerId.trim().isEmpty() || shopId == null || shopId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return billingDAO.getBillingByCustomerAndShop(customerId, shopId);
    }

    public TransactionModel processSubmitBill(CustomerModel customer, String paymentMethod) {
        return processSubmitBill(customer, paymentMethod, this.basket);
    }

    public TransactionModel processSubmitBill(CustomerModel customer, String paymentMethod, Map<String, Integer> itemsBasket) {
        Map<String, Integer> activeBasket = (itemsBasket != null && !itemsBasket.isEmpty()) ? itemsBasket : this.basket;
        if (activeBasket == null || activeBasket.isEmpty()) {
            System.err.println("[BillingController] Cannot process submit bill: Basket is empty");
            return null;
        }

        double grandTotal = 0.0;
        int totalQty = 0;
        List<com.eudhari.model.OrderItemModel> orderItems = new ArrayList<>();
        StringBuilder summaryBuilder = new StringBuilder();

        for (Map.Entry<String, Integer> entry : activeBasket.entrySet()) {
            String pName = entry.getKey();
            int qty = entry.getValue();
            totalQty += qty;
            double pPrice = 50.0;

            String pId = "";
            for (ProductModel pm : productStore.getAllProducts()) {
                if (pm.getName().equalsIgnoreCase(pName)) {
                    pId = pm.getId();
                    pPrice = pm.getPrice();
                    double newStock = Math.max(0, pm.getStock() - qty);
                    pm.setStock(newStock);
                    pm.setSalesCount(pm.getSalesCount() + qty);
                    productDAO.updateProduct(pm);
                    break;
                }
            }
            double subtotal = pPrice * qty;
            grandTotal += subtotal;
            orderItems.add(new com.eudhari.model.OrderItemModel(pId, pName, qty, pPrice, subtotal));
            summaryBuilder.append(pName).append(" x").append(qty).append(", ");
        }

        String summary = summaryBuilder.length() > 2 ? summaryBuilder.substring(0, summaryBuilder.length() - 2) : getBasketSummary();

        TransactionModel tx = transactionStore.addTransaction(customer, summary, totalQty, grandTotal, paymentMethod);

        UserModel curUser = ProfileController.getInstance().getCurrentUserProfile();
        if (curUser == null) {
            curUser = com.eudhari.config.UserSession.getInstance().getCurrentUser();
        }
        String curUid = curUser != null && curUser.getUid() != null ? curUser.getUid() : "";
        ShopModel shop = ShopController.getInstance().getShopByOwnerId(curUid);
        String shopId = shop != null && shop.getShopId() != null ? shop.getShopId() : "";
        String shopName = shop != null && shop.getShopName() != null ? shop.getShopName() : (curUser != null && curUser.getShopName() != null ? curUser.getShopName() : "Store");

        if (tx != null) {
            if (shopId != null && !shopId.isBlank()) {
                tx.setShopId(shopId);
            }
            transactionDAO.saveTransaction(tx);
        }

        String method = paymentMethod != null ? paymentMethod.trim().toUpperCase() : "CASH";
        if ("ONLINE PAYMENT".equalsIgnoreCase(method)) method = "ONLINE";
        String pStatus = "UDHARI".equals(method) ? "PENDING" : "PAID";

        int count = billingDAO.getBillingCount();
        String billingId = String.format("BIL%03d", count + 1);
        String createdAt = Instant.now().toString();

        String custId = customer != null ? (customer.getUid() != null && !customer.getUid().isBlank() ? customer.getUid() : customer.getId()) : "";
        String custName = customer != null ? customer.getName() : "Walk-in Customer";

        BillingModel billing = new BillingModel(
                billingId,
                tx != null ? tx.getBillId() : billingId,
                custId,
                custName,
                shopId,
                shopName,
                curUid,
                orderItems,
                grandTotal,
                method,
                pStatus,
                createdAt
        );
        billingDAO.saveBilling(billing);

        if (customer != null) {
            customer.setTotalPurchases(customer.getTotalPurchases() + grandTotal);
            if ("UDHARI".equals(method)) {
                customer.setPendingUdhari(customer.getPendingUdhari() + grandTotal);
            }
            customerDAO.updateCustomer(customer);
        }

        if ("UDHARI".equals(method)) {
            UdhariController.getInstance().createUdhariRecord(billing);
        }

        if (customer != null && customer.getId() != null) {
            NotificationController.getInstance().sendNotification(
                    customer.getId(),
                    "customer",
                    curUid,
                    "shopkeeper",
                    "BILLING",
                    "Bill Generated (" + method + ")",
                    "Your bill #" + billingId + " for ₹" + String.format("%.2f", grandTotal) + " was generated via " + method + ".",
                    billingId
            );
        }

        activeBasket.clear();
        this.basket.clear();
        return tx;
    }

    public void deleteTransaction(TransactionModel tx) {
        transactionStore.deleteTransaction(tx);
    }
}
