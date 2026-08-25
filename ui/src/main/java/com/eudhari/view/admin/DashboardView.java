package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.ActivityModel;
import com.eudhari.model.ShopkeeperModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardView {
    private final AdminController controller;
    private final ScrollPane rootPane;
    private final Runnable navigateToShops;
    private final Runnable navigateToCustomers;
    private final Runnable navigateToProducts;
    private final Runnable navigateToAnalysis;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String APP_BG = "#c1e1ff";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String PRIMARY_COLOR = "#3A57E8";

    public DashboardView(AdminController controller, Runnable navigateToShops, Runnable navigateToCustomers,
            Runnable navigateToProducts, Runnable navigateToAnalysis) {
        this.controller = controller;
        this.navigateToShops = navigateToShops;
        this.navigateToCustomers = navigateToCustomers;
        this.navigateToProducts = navigateToProducts;
        this.navigateToAnalysis = navigateToAnalysis;
        this.rootPane = buildView();
    }

    public Node getView() {
        return rootPane;
    }

    private ScrollPane buildView() {
        VBox container = new VBox(24);
        container.setPadding(new Insets(24, 32, 32, 32));
        container.setStyle("-fx-background-color: " + APP_BG + ";");

        // Header
        VBox headerBox = new VBox(4);
        Label title = new Label("Admin Dashboard");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Overview of Smart eUdhari platform performance and metrics.");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        headerBox.getChildren().addAll(title, subtitle);
        container.getChildren().add(headerBox);

        // Fetch real data from Firestore
        com.eudhari.dao.shopkepperdao.FirestoreShopDAO shopDAO = new com.eudhari.dao.shopkepperdao.FirestoreShopDAO();
        java.util.List<com.eudhari.model.ShopModel> allShops = shopDAO.getAllShops();
        int activeShopsCount = 0;
        if (allShops != null) {
            for (com.eudhari.model.ShopModel s : allShops) {
                if ("ACTIVE".equalsIgnoreCase(s.getStatus())) activeShopsCount++;
            }
        }

        com.eudhari.dao.FirestoreUserDAO userDAO = new com.eudhari.dao.FirestoreUserDAO();
        java.util.List<com.eudhari.model.UserModel> shopkeepersList = userDAO.getUsersByRole("shopkeeper");
        int shopkeepersCount = shopkeepersList != null ? shopkeepersList.size() : 0;

        java.util.List<com.eudhari.model.UserModel> customersList = userDAO.getUsersByRole("customer");
        int customersCount = customersList != null ? customersList.size() : 0;

        com.eudhari.dao.FirestoreBillingDAO billingDAO = new com.eudhari.dao.FirestoreBillingDAO();
        java.util.List<com.eudhari.model.BillingModel> allBillings = billingDAO.getAllBillings();
        double totalSalesRevenue = 0.0;
        if (allBillings != null) {
            for (com.eudhari.model.BillingModel b : allBillings) {
                totalSalesRevenue += b.getTotalAmount();
            }
        }

        // Top Grid Section: Metric Cards
        HBox topSection = new HBox(16);
        topSection.setAlignment(Pos.CENTER_LEFT);

        HBox cardShops = createMetricCard("🏪", "#EEF2FF", "TOTAL ACTIVE SHOPS", String.valueOf(activeShopsCount), "Live", true, navigateToShops);
        HBox cardShopkeepers = createMetricCard("👨‍💼", "#F3E8FF", "TOTAL SHOPKEEPERS", String.valueOf(shopkeepersCount), "Live", true, navigateToShops);
        HBox cardCustomers = createMetricCard("👥", "#DCFCE7", "TOTAL CUSTOMERS", String.valueOf(customersCount), "Live", true, navigateToCustomers);
        HBox cardRevenue = createMetricCard("💰", "#FEF3C7", "TOTAL REVENUE", String.format("₹%.2f", totalSalesRevenue), "Live", true, null);

        HBox.setHgrow(cardShops, Priority.ALWAYS);
        HBox.setHgrow(cardShopkeepers, Priority.ALWAYS);
        HBox.setHgrow(cardCustomers, Priority.ALWAYS);
        HBox.setHgrow(cardRevenue, Priority.ALWAYS);

        topSection.getChildren().addAll(cardShops, cardShopkeepers, cardCustomers, cardRevenue);
        container.getChildren().add(topSection);

        // Bottom Section: Recent Activity & Top Performing Shops
        HBox bottomSection = new HBox(20);

        // Recent Activity Card
        VBox activityCard = createCard();
        activityCard.setPrefWidth(460);
        HBox.setHgrow(activityCard, Priority.ALWAYS);

        HBox activityHeader = new HBox();
        activityHeader.setAlignment(Pos.CENTER_LEFT);
        Label actTitle = new Label("Recent Activity");
        actTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region actSpacer = new Region();
        HBox.setHgrow(actSpacer, Priority.ALWAYS);
        Label viewAllAct = new Label("View All");
        viewAllAct.setStyle(FONT + "-fx-text-fill: " + PRIMARY_COLOR
                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
        if (navigateToCustomers != null) {
            viewAllAct.setOnMouseClicked(e -> navigateToCustomers.run());
        }
        activityHeader.getChildren().addAll(actTitle, actSpacer, viewAllAct);

        VBox activityList = new VBox(12);
        activityList.setPadding(new Insets(12, 0, 0, 0));

        for (ActivityModel act : controller.getAllActivities()) {
            HBox itemRow = new HBox(12);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setPadding(new Insets(8, 0, 8, 0));
            itemRow.setStyle("-fx-border-color: transparent transparent #F1F5F9 transparent;");

            String iconBg = act.getIconType().equals("payment") ? "#DCFCE7"
                    : (act.getIconType().equals("udhari") ? "#FEF3C7" : "#EEF2FF");
            String iconFg = act.getIconType().equals("payment") ? "#16A34A"
                    : (act.getIconType().equals("udhari") ? "#D97706" : "#3A57E8");
            String iconSymbol = act.getIconType().equals("payment") ? "💳"
                    : (act.getIconType().equals("udhari") ? "📝" : "👤");

            Label iconBadge = new Label(iconSymbol);
            iconBadge.setAlignment(Pos.CENTER);
            iconBadge.setPrefSize(34, 34);
            iconBadge.setMinSize(34, 34);
            iconBadge.setStyle(FONT + "-fx-background-color: " + iconBg + "; -fx-text-fill: " + iconFg
                    + "; -fx-background-radius: 50%; -fx-font-size: 14px;");

            VBox textCol = new VBox(3);
            Label desc = new Label(act.getActivity());
            desc.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #0F172A;");
            Label time = new Label(act.getTimeAgo());
            time.setStyle(FONT + "-fx-font-size: 10px; -fx-text-fill: #64748B;");
            textCol.getChildren().addAll(desc, time);

            itemRow.getChildren().addAll(iconBadge, textCol);
            activityList.getChildren().add(itemRow);
        }

        activityCard.getChildren().addAll(activityHeader, activityList);

        // Top Performing Shops Card
        VBox shopsCard = createCard();
        HBox.setHgrow(shopsCard, Priority.ALWAYS);

        HBox shopsHeader = new HBox();
        shopsHeader.setAlignment(Pos.CENTER_LEFT);
        Label topShopsTitle = new Label("Top Performing Shops");
        topShopsTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region shopsSpacer = new Region();
        HBox.setHgrow(shopsSpacer, Priority.ALWAYS);
        Label viewAllShops = new Label("View All");
        viewAllShops.setStyle(FONT + "-fx-text-fill: " + PRIMARY_COLOR
                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
        if (navigateToShops != null) {
            viewAllShops.setOnMouseClicked(e -> navigateToShops.run());
        }
        shopsHeader.getChildren().addAll(topShopsTitle, shopsSpacer, viewAllShops);

        TableView<ShopkeeperModel> shopsTable = new TableView<>();
        styleTableView(shopsTable);
        shopsTable.setPrefHeight(220);

        TableColumn<ShopkeeperModel, String> colName = new TableColumn<>("SHOP NAME");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShopName()));
        colName.setPrefWidth(160);

        TableColumn<ShopkeeperModel, String> colTxn = new TableColumn<>("TRANSACTIONS");
        colTxn.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTransactionsCount())));
        colTxn.setPrefWidth(100);

        TableColumn<ShopkeeperModel, String> colVol = new TableColumn<>("VOLUME");
        colVol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVolumeAmount()));
        colVol.setPrefWidth(90);

        TableColumn<ShopkeeperModel, String> colStatus = new TableColumn<>("STATUS");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<ShopkeeperModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createBadge(item));
                }
            }
        });
        colStatus.setPrefWidth(85);

        shopsTable.getColumns().addAll(colName, colTxn, colVol, colStatus);
        shopsTable.setItems(controller.getAllShopkeepers());

        shopsCard.getChildren().addAll(shopsHeader, new Region() {
            {
                setPrefHeight(8);
            }
        }, shopsTable);

        bottomSection.getChildren().addAll(activityCard, shopsCard);
        container.getChildren().add(bottomSection);

        ScrollPane sp = new ScrollPane(container);
        sp.setFitToWidth(true);
        sp.setStyle(
                "-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");
        return sp;
    }

    private VBox createCard(Node... children) {
        VBox card = new VBox(children);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12px; " +
                "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-padding: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 3);");
        return card;
    }

    private HBox createMetricCard(String icon, String iconBg, String title, String value, String growthText,
            boolean hasGrowth, Runnable onClick) {
        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setPrefSize(44, 44);
        iconLabel.setMinSize(44, 44);
        iconLabel.setStyle(FONT + "-fx-font-size: 18px; -fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-background-color: " + iconBg + "; -fx-background-radius: 10px; -fx-font-weight: bold;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle(FONT + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle(FONT + "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        VBox textCol = new VBox(4, titleLabel, valueLabel);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        HBox card = new HBox(14, iconLabel, textCol);
        card.setAlignment(Pos.CENTER_LEFT);

        if (hasGrowth && growthText != null && !growthText.isEmpty()) {
            Label growthBadge = new Label(growthText);
            growthBadge.setStyle(FONT + "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; " +
                    "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-padding: 2px 8px;");
            VBox growthBox = new VBox(growthBadge);
            growthBox.setAlignment(Pos.TOP_RIGHT);
            card.getChildren().add(growthBox);
        }

        String baseStyle = "-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12px; " +
                "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-padding: 18px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);";
        card.setStyle(baseStyle);

        if (onClick != null) {
            card.setStyle(baseStyle + "-fx-cursor: hand;");
            card.setOnMouseEntered(
                    e -> card.setStyle(baseStyle + "-fx-border-color: " + PRIMARY_COLOR + "; -fx-cursor: hand;"));
            card.setOnMouseExited(e -> card.setStyle(baseStyle + "-fx-cursor: hand;"));
            card.setOnMouseClicked(e -> onClick.run());
        }

        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        String bg = "#F1F5F9";
        String fg = "#475569";
        String t = text.toLowerCase();

        if (t.contains("active") || t.contains("available") || t.contains("completed") || t.contains("settled")
                || t.contains("credit") || t.contains("ready")) {
            bg = "#DCFCE7";
            fg = "#16A34A";
        } else if (t.contains("pending") || t.contains("review") || t.contains("low stock")) {
            bg = "#FEF3C7";
            fg = "#D97706";
        } else if (t.contains("overdue") || t.contains("out of stock") || t.contains("suspended")
                || t.contains("debit")) {
            bg = "#FEE2E2";
            fg = "#DC2626";
        }

        badge.setStyle(FONT + "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 3px 10px;");
        return badge;
    }

    private void styleTableView(TableView<?> table) {
        table.setStyle(FONT + "-fx-background-color: white; -fx-background-radius: 8px; " +
                "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; -fx-padding: 0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
