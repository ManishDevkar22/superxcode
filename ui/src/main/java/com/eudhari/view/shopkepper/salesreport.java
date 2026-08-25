package com.eudhari.view.shopkepper;

import com.eudhari.controller.shopkeppercontroller.SalesReportController;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.controller.ProfileController;
import com.eudhari.config.UserSession;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class salesreport {

    public static Parent create(dashboard nav) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + Theme.BG_DARK + ";");

        SalesReportController controller = SalesReportController.getInstance();

        // ---------------- Current Shop & User Context ----------------
        UserModel currentSkUser = ProfileController.getInstance().getCurrentUserProfile();
        if (currentSkUser == null) {
            currentSkUser = UserSession.getInstance().getCurrentUser();
        }
        String currentSkUid = currentSkUser != null && currentSkUser.getUid() != null ? currentSkUser.getUid() : "";
        ShopModel currentShop = ShopController.getInstance().getShopByOwnerId(currentSkUid);
        String currentShopId = currentShop != null && currentShop.getShopId() != null ? currentShop.getShopId() : "";

        String shopDisplayName = currentShop != null && currentShop.getShopName() != null && !currentShop.getShopName().isBlank()
                ? currentShop.getShopName()
                : (currentSkUser != null && currentSkUser.getShopName() != null && !currentSkUser.getShopName().isBlank()
                ? currentSkUser.getShopName()
                : "Shopkeeper Store");

        // ---------------- HEADER BAR ----------------
        Label search = new Label("🔍 Search sales reports, products, transactions...");
        search.setPrefWidth(450);
        search.setStyle("-fx-background-color:" + Theme.BG_CARD + ";-fx-text-fill:" + Theme.TEXT_MUTED
                + ";-fx-background-radius:20;-fx-padding:10 20;-fx-border-color:" + Theme.BORDER_DARK
                + ";-fx-border-radius:20;");

        Button notify = new Button("🔔 Notifications");
        notify.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + ";-fx-text-fill:" + Theme.SKY_BLUE
                + ";-fx-font-weight:bold;-fx-background-radius:18;-fx-padding:8 14;-fx-border-color:"
                + Theme.SKY_BLUE + ";-fx-border-radius:18;-fx-cursor:hand;");
        notify.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        String userNameDisplay = currentSkUser != null && currentSkUser.getName() != null ? currentSkUser.getName() : "Shopkeeper";
        VBox admin = new VBox(2, new Label(userNameDisplay), new Label(shopDisplayName));
        admin.getChildren().get(0).setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
        admin.getChildren().get(1).setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px;");

        String initials = userNameDisplay.length() >= 2 ? userNameDisplay.substring(0, 2).toUpperCase() : "SK";
        Label avatar = new Label(initials);
        avatar.setStyle("-fx-background-color:" + Theme.SKY_BLUE_DARK
                + ";-fx-text-fill:white;-fx-background-radius:20;-fx-padding:10 12;-fx-cursor:hand;-fx-font-weight:bold;");
        avatar.setOnMouseClicked(e -> nav.navigateTo(dashboard.PROFILE));

        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox(Theme.SKY_BLUE, "-fx-background-color:" + Theme.BG_CARD + "; -fx-padding:6 12; -fx-background-radius:18; -fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-radius:18;");

        HBox topSpace = new HBox();
        HBox.setHgrow(topSpace, Priority.ALWAYS);
        HBox header = new HBox(18, topSpace, search, clockWidget, notify, admin, avatar);
        header.setPadding(new Insets(16, 30, 14, 30));
        header.setAlignment(Pos.CENTER_RIGHT);
        Theme.applyHeaderStyle(header);
        root.setTop(header);

        String cardStyle = Theme.STYLE_CARD;

        // ---------------- DYNAMIC SALES METRICS FROM FIRESTORE ----------------
        double dailySales = controller.getDailySalesForShop(currentShopId);
        double weeklySales = controller.getWeeklySalesForShop(currentShopId);
        double monthlySales = controller.getMonthlySalesForShop(currentShopId);
        double totalRevenue = controller.getTotalSalesForShop(currentShopId);

        // ---------------- 1. TOP CARDS (4 Summary Boxes) ----------------
        VBox todaySalesBox = createSummaryCard("DAILY\nSALES", String.format("₹%.2f", dailySales), "Sales today", "#4ade80", cardStyle);
        VBox weeklySalesBox = createSummaryCard("WEEKLY\nSALES", String.format("₹%.2f", weeklySales), "Last 7 days", "#4ade80", cardStyle);
        VBox monthlySalesBox = createSummaryCard("MONTHLY\nSALES", String.format("₹%.2f", monthlySales), "This month", Theme.TEXT_SECONDARY, cardStyle);
        VBox totalRevenueBox = createSummaryCard("TOTAL\nREVENUE", String.format("₹%.2f", totalRevenue), "All time sales", Theme.SKY_BLUE, cardStyle);

        HBox cards = new HBox(20, todaySalesBox, weeklySalesBox, monthlySalesBox, totalRevenueBox);
        cards.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(todaySalesBox, Priority.ALWAYS);
        HBox.setHgrow(weeklySalesBox, Priority.ALWAYS);
        HBox.setHgrow(monthlySalesBox, Priority.ALWAYS);
        HBox.setHgrow(totalRevenueBox, Priority.ALWAYS);

        // ---------------- 2. MIDDLE PANELS: PERFORMANCE & BEST SELLING ----------------
        Map<String, Object> weeklyMetrics = controller.getWeeklyPerformanceMetricsForShop(currentShopId);
        double currentWeek = (Double) weeklyMetrics.get("currentWeekSales");
        double lastWeek = (Double) weeklyMetrics.get("lastWeekSales");
        double growthPct = (Double) weeklyMetrics.get("growthPercentage");

        Label weeklyCompTitle = new Label(
                String.format("Weekly Sales History: Current Week (₹%.2f) vs Previous (₹%.2f) [%+.1f%%]",
                        currentWeek, lastWeek, growthPct));
        weeklyCompTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        weeklyCompTitle.setStyle("-fx-text-fill:#4ade80;");

        Label perfLbl = new Label("Sales Performance Summary");
        perfLbl.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label shopSubtitle = new Label("Live shop revenue metrics & sales trends for shop ID: " + (currentShopId.isBlank() ? "Default" : currentShopId));
        shopSubtitle.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");

        VBox perfContent = new VBox(12, perfLbl, shopSubtitle, weeklyCompTitle);
        perfContent.setPadding(new Insets(24));
        perfContent.setStyle(cardStyle);
        perfContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(perfContent, Priority.ALWAYS);

        // Best-Selling Product Section (Dynamic from Firestore)
        Label bestTitle = new Label("Best-Selling Products");
        bestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        bestTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        VBox bestSellingList = new VBox(10);
        List<Map<String, Object>> productSalesList = controller.getProductWiseSalesForShop(currentShopId);

        if (productSalesList.isEmpty()) {
            Label noBest = new Label("No sales recorded yet for this shop.");
            noBest.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");
            bestSellingList.getChildren().add(noBest);
        } else {
            int count = 0;
            for (Map<String, Object> pMap : productSalesList) {
                if (count >= 4) break;
                String pName = (String) pMap.get("name");
                int uSold = (Integer) pMap.get("unitsSold");
                double rev = (Double) pMap.get("revenue");

                Label pNameLbl = new Label((count + 1) + ". " + pName);
                pNameLbl.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
                Label pValLbl = new Label(String.format("%d units  |  ₹%.2f", uSold, rev));
                pValLbl.setStyle("-fx-text-fill:" + Theme.SKY_BLUE + "; -fx-font-size:12px;");

                HBox pRow = new HBox(10, pNameLbl, dashboard.spacer(), pValLbl);
                pRow.setPadding(new Insets(6, 10, 6, 10));
                pRow.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-background-radius:8; -fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-radius:8;");
                bestSellingList.getChildren().add(pRow);
                count++;
            }
        }

        Button viewProdBtn = new Button("View All Inventory  →");
        viewProdBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
        viewProdBtn.setMaxWidth(Double.MAX_VALUE);
        viewProdBtn.setOnAction(e -> nav.navigateTo(dashboard.PRODUCTS));

        VBox sellingCard = new VBox(14, bestTitle, bestSellingList, viewProdBtn);
        sellingCard.setPadding(new Insets(24));
        sellingCard.setStyle(cardStyle);
        sellingCard.setMinWidth(350);
        HBox.setHgrow(sellingCard, Priority.ALWAYS);

        HBox middlePanels = new HBox(22, perfContent, sellingCard);
        middlePanels.setMaxWidth(Double.MAX_VALUE);

        // ---------------- 3. LOWER PANELS: PRODUCT-WISE & CATEGORY-WISE SALES ----------------
        // Product-Wise Sales Table (Full dynamic table)
        Label prodWiseTitle = new Label("Product-Wise Sales Breakdown");
        prodWiseTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        prodWiseTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        TableView<Map<String, Object>> prodTable = new TableView<>();
        prodTable.setPrefHeight(260);
        prodTable.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-border-color:" + Theme.BORDER_DARK + ";");

        TableColumn<Map<String, Object>, String> colName = new TableColumn<>("Product Name");
        colName.setPrefWidth(220);
        colName.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper((String) data.getValue().get("name")));

        TableColumn<Map<String, Object>, String> colCat = new TableColumn<>("Category");
        colCat.setPrefWidth(140);
        colCat.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper((String) data.getValue().get("category")));

        TableColumn<Map<String, Object>, String> colUnits = new TableColumn<>("Units Sold");
        colUnits.setPrefWidth(120);
        colUnits.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(String.valueOf(data.getValue().get("unitsSold"))));

        TableColumn<Map<String, Object>, String> colRev = new TableColumn<>("Revenue (₹)");
        colRev.setPrefWidth(150);
        colRev.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(String.format("₹%.2f", (Double) data.getValue().get("revenue"))));

        prodTable.getColumns().addAll(colName, colCat, colUnits, colRev);
        prodTable.setItems(FXCollections.observableArrayList(productSalesList));

        VBox productWiseBox = new VBox(14, prodWiseTitle, prodTable);
        productWiseBox.setPadding(new Insets(24));
        productWiseBox.setStyle(cardStyle);
        productWiseBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(productWiseBox, Priority.ALWAYS);

        // Category-Wise Sales Panel (Full dynamic breakdown)
        Label categoryTitle = new Label("Category-Wise Sales");
        categoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        categoryTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        VBox categoryList = new VBox(14);
        Map<String, Double> categorySalesMap = controller.getCategoryWiseSalesForShop(currentShopId);

        if (categorySalesMap.isEmpty()) {
            Label noCat = new Label("No category sales recorded yet.");
            noCat.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");
            categoryList.getChildren().add(noCat);
        } else {
            double totalCatSales = categorySalesMap.values().stream().mapToDouble(Double::doubleValue).sum();
            String[] accentColors = new String[] { Theme.SKY_BLUE, "#4ade80", Theme.WARM_BROWN_TEXT, "#f59e0b", "#ec4899" };
            int cIdx = 0;

            for (Map.Entry<String, Double> catEntry : categorySalesMap.entrySet()) {
                String catName = catEntry.getKey();
                double catRev = catEntry.getValue();
                double pct = totalCatSales > 0 ? (catRev / totalCatSales) : 0.0;
                String color = accentColors[cIdx % accentColors.length];

                Label catNameLbl = new Label(catName);
                catNameLbl.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
                Label catRevLbl = new Label(String.format("₹%.2f", catRev));
                catRevLbl.setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold;");

                HBox catTopRow = new HBox(catNameLbl, dashboard.spacer(), catRevLbl);

                ProgressBar catBar = new ProgressBar(pct);
                catBar.setMaxWidth(Double.MAX_VALUE);
                catBar.setPrefHeight(8);
                catBar.setStyle("-fx-accent:" + color + ";");

                VBox catBox = new VBox(6, catTopRow, catBar);
                categoryList.getChildren().add(catBox);
                cIdx++;
            }
        }

        VBox categorySalesBox = new VBox(16, categoryTitle, categoryList);
        categorySalesBox.setPadding(new Insets(24));
        categorySalesBox.setStyle(cardStyle);
        categorySalesBox.setMinWidth(380);
        HBox.setHgrow(categorySalesBox, Priority.ALWAYS);

        HBox lowerPanels = new HBox(22, productWiseBox, categorySalesBox);
        lowerPanels.setMaxWidth(Double.MAX_VALUE);

        // ---------------- PAGE LAYOUT ----------------
        VBox page = new VBox(24, cards, middlePanels, lowerPanels);
        page.setPadding(new Insets(28, 32, 30, 32));
        page.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        Theme.applyScrollDarkStyle(scrollPane);
        root.setCenter(scrollPane);
        return root;
    }

    private static VBox createSummaryCard(String title, String value, String subtitle, String accentColor, String cardStyle) {
        Label salesTitle = new Label(title);
        salesTitle.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px; -fx-font-weight:bold;");
        Label salesValue = new Label(value);
        salesValue.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        salesValue.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label salesSub = new Label(subtitle);
        salesSub.setStyle("-fx-text-fill:" + accentColor + "; -fx-font-size:11px;");

        VBox box = new VBox(12, salesTitle, salesValue, salesSub);
        box.setPadding(new Insets(20));
        box.setPrefSize(145, 155);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(cardStyle);
        return box;
    }
}
