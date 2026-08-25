package com.eudhari.controller.shopkeppercontroller;

import com.eudhari.dao.BillingDAO;
import com.eudhari.dao.FirestoreBillingDAO;
import com.eudhari.dao.shopkepperdao.*;
import com.eudhari.model.BillingModel;
import com.eudhari.model.shopkeppermodel.*;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalesReportController {
    private static SalesReportController instance;
    private final TransactionDAO transactionDAO;
    private final ProductDAO productDAO;
    private final BillingDAO billingDAO;
    private final TransactionStore transactionStore;
    private final ProductStore productStore;

    private SalesReportController() {
        this.transactionDAO = DAOFactory.getTransactionDAO();
        this.productDAO = DAOFactory.getProductDAO();
        this.billingDAO = new FirestoreBillingDAO();
        this.transactionStore = TransactionStore.getInstance();
        this.productStore = ProductStore.getInstance();
    }

    public static synchronized SalesReportController getInstance() {
        if (instance == null) {
            instance = new SalesReportController();
        }
        return instance;
    }

    public ObservableList<TransactionModel> getAllTransactions() {
        return transactionStore.getAllTransactions();
    }

    public List<TransactionModel> getTransactionsForShop(String shopId) {
        List<TransactionModel> list = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return transactionStore.getAllTransactions();
        }
        // Try DAO first
        list = transactionDAO.getTransactionsByShopId(shopId.trim());
        if (list.isEmpty()) {
            // Fallback filter from store
            for (TransactionModel tx : transactionStore.getAllTransactions()) {
                if (shopId.equalsIgnoreCase(tx.getShopId())) {
                    list.add(tx);
                }
            }
        }
        return list;
    }

    public double getTotalSales() {
        double total = 0;
        for (TransactionModel tx : transactionStore.getAllTransactions()) {
            total += tx.getTotalAmount();
        }
        return total;
    }

    public double getTodaySales() {
        return getDailySalesForShop("");
    }

    public Map<String, Object> getWeeklyPerformanceMetrics() {
        return getWeeklyPerformanceMetricsForShop("");
    }

    public double getTotalSalesForShop(String shopId) {
        double total = 0;
        List<TransactionModel> txs = getTransactionsForShop(shopId);
        for (TransactionModel tx : txs) {
            total += tx.getTotalAmount();
        }
        return total;
    }

    public double getDailySalesForShop(String shopId) {
        double total = 0;
        LocalDate today = LocalDate.now();
        List<TransactionModel> txs = getTransactionsForShop(shopId);
        for (TransactionModel tx : txs) {
            if (isDateMatch(tx.getDateTime(), today)) {
                total += tx.getTotalAmount();
            }
        }
        return total;
    }

    public double getWeeklySalesForShop(String shopId) {
        double total = 0;
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        List<TransactionModel> txs = getTransactionsForShop(shopId);
        for (TransactionModel tx : txs) {
            LocalDate dt = parseDate(tx.getDateTime());
            if (dt != null && !dt.isBefore(sevenDaysAgo) && !dt.isAfter(today)) {
                total += tx.getTotalAmount();
            }
        }
        return total;
    }

    public double getMonthlySalesForShop(String shopId) {
        double total = 0;
        LocalDate today = LocalDate.now();
        List<TransactionModel> txs = getTransactionsForShop(shopId);
        for (TransactionModel tx : txs) {
            LocalDate dt = parseDate(tx.getDateTime());
            if (dt != null && dt.getYear() == today.getYear() && dt.getMonthValue() == today.getMonthValue()) {
                total += tx.getTotalAmount();
            }
        }
        return total;
    }

    public List<ProductModel> getProductsForShop(String shopId) {
        List<ProductModel> products = new ArrayList<>();
        if (shopId == null || shopId.trim().isEmpty()) {
            return productStore.getAllProducts();
        }
        products = productDAO.getProductsByShopId(shopId.trim());
        if (products.isEmpty()) {
            for (ProductModel p : productStore.getAllProducts()) {
                if (shopId.equalsIgnoreCase(p.getShopId())) {
                    products.add(p);
                }
            }
        }
        return products;
    }

    /**
     * Map representing product sales info for a shop:
     * Product name -> Map with "units", "revenue", "category", "price"
     */
    public List<Map<String, Object>> getProductWiseSalesForShop(String shopId) {
        List<ProductModel> products = getProductsForShop(shopId);
        List<TransactionModel> txs = getTransactionsForShop(shopId);

        Map<String, Integer> productUnits = new HashMap<>();

        // Calculate units sold from transactions itemsSummary
        for (TransactionModel tx : txs) {
            String summary = tx.getItemsSummary();
            if (summary != null && !summary.isBlank()) {
                String[] parts = summary.split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.contains(" x")) {
                        int lastIdx = part.lastIndexOf(" x");
                        String name = part.substring(0, lastIdx).trim();
                        try {
                            int qty = Integer.parseInt(part.substring(lastIdx + 2).trim());
                            productUnits.put(name, productUnits.getOrDefault(name, 0) + qty);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ProductModel p : products) {
            int unitsFromTx = productUnits.getOrDefault(p.getName(), 0);
            int unitsSold = Math.max(p.getSalesCount(), unitsFromTx);
            double revenue = unitsSold * p.getPrice();

            Map<String, Object> item = new HashMap<>();
            item.put("name", p.getName());
            item.put("category", p.getCategory() != null && !p.getCategory().isBlank() ? p.getCategory() : "General");
            item.put("price", p.getPrice());
            item.put("unit", p.getUnit());
            item.put("unitsSold", unitsSold);
            item.put("revenue", revenue);
            result.add(item);
        }

        // Also add products mentioned in txs but not in product catalog
        for (Map.Entry<String, Integer> entry : productUnits.entrySet()) {
            boolean exists = false;
            for (ProductModel p : products) {
                if (p.getName().equalsIgnoreCase(entry.getKey())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("category", "General");
                item.put("price", 0.0);
                item.put("unit", "pcs");
                item.put("unitsSold", entry.getValue());
                item.put("revenue", 0.0);
                result.add(item);
            }
        }

        // Sort by units sold descending
        result.sort((a, b) -> Integer.compare((Integer) b.get("unitsSold"), (Integer) a.get("unitsSold")));
        return result;
    }

    public Map<String, Object> getBestSellingProductForShop(String shopId) {
        List<Map<String, Object>> productSales = getProductWiseSalesForShop(shopId);
        if (!productSales.isEmpty()) {
            return productSales.get(0);
        }
        Map<String, Object> empty = new HashMap<>();
        empty.put("name", "N/A");
        empty.put("unitsSold", 0);
        empty.put("revenue", 0.0);
        return empty;
    }

    public Map<String, Double> getCategoryWiseSalesForShop(String shopId) {
        List<Map<String, Object>> productSales = getProductWiseSalesForShop(shopId);
        Map<String, Double> catMap = new LinkedHashMap<>();

        for (Map<String, Object> item : productSales) {
            String cat = (String) item.get("category");
            double rev = (Double) item.get("revenue");
            catMap.put(cat, catMap.getOrDefault(cat, 0.0) + rev);
        }
        return catMap;
    }

    public Map<String, Object> getWeeklyPerformanceMetricsForShop(String shopId) {
        Map<String, Object> metrics = new HashMap<>();

        double currentWeekSales = getWeeklySalesForShop(shopId);
        double totalSales = getTotalSalesForShop(shopId);
        double lastWeekSales = Math.max(0, totalSales - currentWeekSales);
        double growthPercentage = lastWeekSales > 0 ? ((currentWeekSales - lastWeekSales) / lastWeekSales) * 100.0 : 0.0;

        metrics.put("currentWeekSales", currentWeekSales);
        metrics.put("lastWeekSales", lastWeekSales);
        metrics.put("growthPercentage", growthPercentage);

        return metrics;
    }

    private boolean isDateMatch(String dateTimeStr, LocalDate date) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return false;
        try {
            LocalDate parsed = parseDate(dateTimeStr);
            return date.equals(parsed);
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDate parseDate(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            if (str.length() >= 10 && str.charAt(4) == '-' && str.charAt(7) == '-') {
                return LocalDate.parse(str.substring(0, 10));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            String datePart = str.split(",")[0].trim();
            return LocalDate.parse(datePart, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
