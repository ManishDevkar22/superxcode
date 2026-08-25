package com.eudhari.view.customer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CustomerExpenses {

    private final BorderPane root;

    public CustomerExpenses(Runnable backAction) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #070d18; -fx-font-family: 'Segoe UI', sans-serif;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));

        // 1. Header Row
        Label headerTitle = new Label("💰 Customer Expenses & Credit Overview");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-font-weight: bold; " +
                "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (backAction != null) backAction.run();
        });

        HBox topRow = new HBox(headerTitle, backBtn);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);
        topRow.setStyle("-fx-alignment: center-left;");

        com.eudhari.model.UserModel currentCust = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        java.util.List<com.eudhari.model.BillingModel> billings = com.eudhari.controller.shopkeppercontroller.BillingController.getInstance().getBillingForCustomer(currentCustId);

        double dailyTotal = 0.0;
        double weeklyTotal = 0.0;
        double monthlyTotal = 0.0;
        double grandTotal = 0.0;
        java.util.Map<String, Double> shopExpensesMap = new java.util.HashMap<>();

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate sevenDaysAgo = today.minusDays(7);
        String currentMonthStr = String.format("%04d-%02d", today.getYear(), today.getMonthValue());

        if (billings != null) {
            for (com.eudhari.model.BillingModel b : billings) {
                double amt = b.getTotalAmount();
                grandTotal += amt;

                String sName = b.getShopName() != null && !b.getShopName().isBlank() ? b.getShopName() : "Store";
                shopExpensesMap.put(sName, shopExpensesMap.getOrDefault(sName, 0.0) + amt);

                if (b.getCreatedAt() != null && b.getCreatedAt().length() >= 10) {
                    try {
                        String dtStr = b.getCreatedAt().substring(0, 10);
                        java.time.LocalDate bDate = java.time.LocalDate.parse(dtStr);
                        if (bDate.isEqual(today)) {
                            dailyTotal += amt;
                        }
                        if (!bDate.isBefore(sevenDaysAgo)) {
                            weeklyTotal += amt;
                        }
                        if (dtStr.startsWith(currentMonthStr)) {
                            monthlyTotal += amt;
                        }
                    } catch (Exception ignored) {}
                }
            }
        }

        // 2. Summary Stats Cards Row
        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
                createStatCard("DAILY EXPENSES", String.format("₹%.2f", dailyTotal), "#38bdf8"),
                createStatCard("WEEKLY EXPENSES", String.format("₹%.2f", weeklyTotal), "#4ade80"),
                createStatCard("MONTHLY EXPENSES", String.format("₹%.2f", monthlyTotal), "#fbbf24"),
                createStatCard("TOTAL EXPENSES", String.format("₹%.2f", grandTotal), "#a78bfa")
        );

        // 3. Shop-Wise Expenses Breakdown Card
        VBox categoryCard = new VBox(14);
        categoryCard.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 20;");

        Label catTitle = new Label("Shop-Wise Expenses Breakdown");
        catTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        VBox catList = new VBox(12);
        if (shopExpensesMap.isEmpty()) {
            Label emptyShopLbl = new Label("No shop-wise expense records found.");
            emptyShopLbl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            catList.getChildren().add(emptyShopLbl);
        } else {
            for (java.util.Map.Entry<String, Double> entry : shopExpensesMap.entrySet()) {
                double progress = grandTotal > 0 ? entry.getValue() / grandTotal : 0.0;
                catList.getChildren().add(createCategoryBar("🏪 " + entry.getKey(), String.format("₹%.2f", entry.getValue()), progress, "#38bdf8"));
            }
        }
        categoryCard.getChildren().addAll(catTitle, catList);

        // 4. Expenses Transaction Table
        VBox tableCard = new VBox(14);
        tableCard.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 20;");

        Label tableTitle = new Label("Recent Billing Expenses");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        VBox transList = new VBox(10);
        if (billings != null && !billings.isEmpty()) {
            for (com.eudhari.model.BillingModel b : billings) {
                String dtStr = b.getCreatedAt() != null && b.getCreatedAt().length() >= 10 ? b.getCreatedAt().substring(0, 10) : "Recent";
                transList.getChildren().add(createTransactionRow(
                        dtStr,
                        b.getShopName(),
                        b.getItemsSummary(),
                        b.getPaymentMethod() + " (" + b.getPaymentStatus() + ")",
                        String.format("₹%.2f", b.getTotalAmount()),
                        "PAID".equalsIgnoreCase(b.getPaymentStatus()) ? "#4ade80" : "#fbbf24"
                ));
            }
        } else {
            Label emptyTransLbl = new Label("No recent expense transactions found.");
            emptyTransLbl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
            transList.getChildren().add(emptyTransLbl);
        }
        tableCard.getChildren().addAll(tableTitle, transList);

        mainContent.getChildren().addAll(topRow, statsRow, categoryCard, tableCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #c1e1ff; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
    }

    public BorderPane getView() {
        return root;
    }

    private VBox createStatCard(String label, String value, String colorHex) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        VBox box = new VBox(6, l, v);
        box.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox createCategoryBar(String category, String amount, double progress, String colorHex) {
        Label name = new Label(category);
        name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        Label val = new Label(amount);
        val.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        HBox textRow = new HBox(name, new Region(), val);
        HBox.setHgrow(textRow.getChildren().get(1), Priority.ALWAYS);

        ProgressBar bar = new ProgressBar(progress);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + colorHex + "; -fx-control-inner-background: #1e293b;");

        VBox box = new VBox(4, textRow, bar);
        return box;
    }

    private HBox createTransactionRow(String date, String shop, String details, String payType, String amount, String colorHex) {
        VBox left = new VBox(2);
        Label shopLbl = new Label(shop);
        shopLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label detailsLbl = new Label(date + " • " + details);
        detailsLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        left.getChildren().addAll(shopLbl, detailsLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox right = new VBox(2);
        right.setAlignment(Pos.CENTER_RIGHT);
        Label amtLbl = new Label(amount);
        amtLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        Label badge = new Label(payType);
        badge.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #cbd5e1; -fx-font-size: 10px; " +
                "-fx-padding: 2 6; -fx-background-radius: 4;");
        right.getChildren().addAll(amtLbl, badge);

        HBox row = new HBox(12, left, spacer, right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #131e33; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 14;");
        return row;
    }
}
