package com.eudhari.dao.shopkepperdao;

/**
 * Factory class providing single point of access to DAO implementations.
 * Configured to supply Firestore DAOs for data persistence.
 */
public class DAOFactory {
    private static ProductDAO productDAO;
    private static CustomerDAO customerDAO;
    private static TransactionDAO transactionDAO;
    private static ShopDAO shopDAO;
    private static CategoryDAO categoryDAO;

    public static synchronized CategoryDAO getCategoryDAO() {
        if (categoryDAO == null) {
            categoryDAO = new FirestoreCategoryDAO();
        }
        return categoryDAO;
    }

    public static synchronized ProductDAO getProductDAO() {
        if (productDAO == null) {
            productDAO = new FirestoreProductDAO();
        }
        return productDAO;
    }

    public static synchronized CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            customerDAO = new FirestoreCustomerDAO();
        }
        return customerDAO;
    }

    public static synchronized TransactionDAO getTransactionDAO() {
        if (transactionDAO == null) {
            transactionDAO = new FirestoreTransactionDAO();
        }
        return transactionDAO;
    }

    public static synchronized ShopDAO getShopDAO() {
        if (shopDAO == null) {
            shopDAO = new FirestoreShopDAO();
        }
        return shopDAO;
    }
}
