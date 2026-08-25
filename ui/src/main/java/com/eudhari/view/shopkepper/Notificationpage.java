package com.eudhari.view.shopkepper;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.ProfileController;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class Notificationpage {

    public static Parent create(dashboard nav) {
        Notificationpage pageInstance = new Notificationpage();
        return pageInstance.buildRoot(nav);
    }

    public Parent buildRoot(dashboard nav) {
        VBox contentBox = new VBox();
        contentBox.setStyle("-fx-background-color:" + Theme.BG_DARK + ";");

        // TOP BAR
        Button searchBtn = new Button("🔍   Search notifications...");
        searchBtn.setPrefWidth(320);
        searchBtn.setAlignment(Pos.CENTER_LEFT);
        searchBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_MUTED
                + "; -fx-background-radius:20; -fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-radius:20;");

        Button bellBtn = new Button("🔔");
        Button helpBtn = new Button("❓");
        Button settingsBtn = new Button("⚙");
        Button profileBtn = new Button("👤  Shopkeeper ⌄");
        profileBtn.setOnAction(e -> nav.navigateTo(dashboard.PROFILE));

        bellBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-font-size:16px; -fx-cursor:hand;");
        helpBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-font-size:16px; -fx-cursor:hand;");
        settingsBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-font-size:16px; -fx-cursor:hand;");
        profileBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:6 12; -fx-cursor:hand;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(15, searchBtn, topSpacer, bellBtn, helpBtn, settingsBtn, profileBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 24, 15, 24));
        Theme.applyHeaderStyle(topBar);

        // PAGE HEADER
        Text pageTitle = new Text("Notifications");
        pageTitle.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");
        Text pageSubtitle = new Text("Manage your alerts, updates, and financial reminders.");
        pageSubtitle.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        VBox pageTitleBox = new VBox(4, pageTitle, pageSubtitle);

        Button markReadBtn = new Button("✓✓   Mark all as read");
        markReadBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);

        Button clearAllBtn = new Button("🗑   Clear all");
        clearAllBtn.setStyle(
                "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-border-color:#b91c1c; -fx-border-radius:6; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:8 14;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox pageHeader = new HBox(12, pageTitleBox, headerSpacer, markReadBtn, clearAllBtn);
        pageHeader.setAlignment(Pos.CENTER_LEFT);
        pageHeader.setPadding(new Insets(20, 0, 10, 0));

        // FILTER TABS
        Button tabAll = new Button("All");
        Button tabCredit = new Button("Credit Alerts");
        Button tabOrders = new Button("Order Updates");
        Button tabPayments = new Button("Payments");

        String activeTabStyle = Theme.STYLE_BUTTON_PRIMARY;
        String inactiveTabStyle = "-fx-background-color:transparent; -fx-text-fill:" + Theme.TEXT_SECONDARY
                + "; -fx-background-radius:6; -fx-cursor:hand;";

        tabAll.setStyle(activeTabStyle);
        tabCredit.setStyle(inactiveTabStyle);
        tabOrders.setStyle(inactiveTabStyle);
        tabPayments.setStyle(inactiveTabStyle);

        HBox tabsRow = new HBox(8, tabAll, tabCredit, tabOrders, tabPayments);
        tabsRow.setPadding(new Insets(10, 12, 10, 12));
        tabsRow.setAlignment(Pos.CENTER_LEFT);
        tabsRow.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8;");

        // CUSTOMER NOTIFICATION SECTION
        Button notifyCustomerBtn = new Button("📢   Notify Customer");
        notifyCustomerBtn.setPrefWidth(300);
        notifyCustomerBtn.setPrefHeight(45);
        notifyCustomerBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        VBox notifySection = new VBox(notifyCustomerBtn);
        notifySection.setAlignment(Pos.CENTER);
        notifySection.setPadding(new Insets(15, 0, 15, 0));

        // NOTIFICATION CARDS
        VBox icon1 = new VBox(new Text("📈"));
        icon1.setAlignment(Pos.CENTER);
        icon1.setPrefSize(40, 40);
        icon1.setStyle("-fx-background-color:#14382c; -fx-background-radius:20;");

        Text type1 = new Text("CREDIT ALERT");
        type1.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-fill:#4ade80;");
        Text time1 = new Text("2 hours ago");
        time1.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_MUTED + ";");
        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);
        HBox typeRow1 = new HBox(type1, r1, time1);
        typeRow1.setAlignment(Pos.CENTER_LEFT);

        Text title1 = new Text("Credit Limit Increased");
        title1.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");

        Text desc1 = new Text(
                "Credit limit increased to ₹50,000 by SuperMart Distributors. Your purchasing power has been successfully upgraded.");
        desc1.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        desc1.setWrappingWidth(760);

        VBox textCol1 = new VBox(6, typeRow1, title1, desc1);
        HBox.setHgrow(textCol1, Priority.ALWAYS);
        HBox cardRow1 = new HBox(15, icon1, textCol1);
        cardRow1.setAlignment(Pos.TOP_LEFT);

        VBox card1 = new VBox(cardRow1);
        card1.setPadding(new Insets(18));
        card1.setStyle(
                "-fx-background-color:" + Theme.BG_CARD
                        + "; -fx-border-width:0 0 0 4; -fx-border-color:#22c55e; -fx-background-radius:8; -fx-border-radius:8;");

        VBox icon2 = new VBox(new Text("⏰"));
        icon2.setAlignment(Pos.CENTER);
        icon2.setPrefSize(40, 40);
        icon2.setStyle("-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-background-radius:20;");

        Text type2 = new Text("PAYMENT REMINDER");
        type2.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-fill:" + Theme.WARM_BROWN_TEXT + ";");
        Text time2 = new Text("5 hours ago");
        time2.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_MUTED + ";");
        Region r2 = new Region();
        HBox.setHgrow(r2, Priority.ALWAYS);
        HBox typeRow2 = new HBox(type2, r2, time2);
        typeRow2.setAlignment(Pos.CENTER_LEFT);

        Text title2 = new Text("Upcoming Due Date");
        title2.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");

        Text desc2 = new Text("Upcoming udhari payment of ₹2,500 for Aggarwal General Store is due in 2 days.");
        desc2.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        desc2.setWrappingWidth(760);

        Button payNowBtn = new Button("Pay Now");
        payNowBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        Button snoozeBtn = new Button("Snooze");
        snoozeBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
        HBox actionRow2 = new HBox(10, payNowBtn, snoozeBtn);

        VBox textCol2 = new VBox(6, typeRow2, title2, desc2, actionRow2);
        HBox.setHgrow(textCol2, Priority.ALWAYS);
        HBox cardRow2 = new HBox(15, icon2, textCol2);
        cardRow2.setAlignment(Pos.TOP_LEFT);

        VBox card2 = new VBox(cardRow2);
        card2.setPadding(new Insets(18));
        card2.setStyle(
                "-fx-background-color:" + Theme.BG_CARD + "; -fx-border-width:0 0 0 4; -fx-border-color:"
                        + Theme.WARM_BROWN_TEXT + "; -fx-background-radius:8; -fx-border-radius:8;");

        ObservableList<VBox> allCards = FXCollections.observableArrayList(card1, card2);

        UserModel currentSk = ProfileController.getInstance().getCurrentUserProfile();
        String currentSkId = currentSk != null && currentSk.getUid() != null ? currentSk.getUid() : "";

        ListView<VBox> notificationList = new ListView<>();
        notificationList.setCellFactory(lv -> new ListCell<VBox>() {
            @Override
            protected void updateItem(VBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color:transparent;");
                } else {
                    setGraphic(item);
                    setText(null);
                    setPadding(new Insets(0, 4, 12, 0));
                    setStyle(
                            "-fx-background-color:transparent; -fx-selection-bar:transparent; -fx-selection-bar-non-focused:transparent;");
                }
            }
        });
        notificationList.setFocusTraversable(false);
        notificationList.setPrefHeight(430);
        notificationList.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        VBox.setVgrow(notificationList, Priority.ALWAYS);

        Runnable refreshNotifications = new Runnable() {
            @Override
            public void run() {
                ObservableList<VBox> displayCards = FXCollections.observableArrayList();
                if (!currentSkId.isBlank()) {
                    List<ConnectionRequestModel> pendingRequests = ConnectionRequestController.getInstance().getPendingRequestsForShopkeeper(currentSkId);
                    for (ConnectionRequestModel req : pendingRequests) {
                        displayCards.add(createPendingRequestCard(req, this));
                    }

                    List<com.eudhari.model.OrderModel> customerOrders = com.eudhari.controller.OrderController.getInstance().getOrdersForShopkeeper(currentSkId);
                    if (customerOrders != null) {
                        for (com.eudhari.model.OrderModel ord : customerOrders) {
                            if ("PENDING".equalsIgnoreCase(ord.getStatus())) {
                                displayCards.add(createOrderRequestCard(ord, this, nav));
                            }
                        }
                    }

                    List<com.eudhari.model.NotificationModel> skNotifs = com.eudhari.controller.NotificationController.getInstance().getNotificationsForUser(currentSkId);
                    if (skNotifs != null) {
                        for (com.eudhari.model.NotificationModel n : skNotifs) {
                            displayCards.add(createGeneralNotificationCard(n));
                        }
                    }
                }
                displayCards.addAll(allCards);
                notificationList.setItems(displayCards);
            }
        };

        refreshNotifications.run();

        tabAll.setOnAction(e -> {
            tabAll.setStyle(activeTabStyle);
            tabCredit.setStyle(inactiveTabStyle);
            tabOrders.setStyle(inactiveTabStyle);
            tabPayments.setStyle(inactiveTabStyle);
            refreshNotifications.run();
        });

        markReadBtn.setOnAction(e -> {
            for (VBox c : allCards)
                c.setOpacity(0.55);
            markReadBtn.setText("✓  All marked read");
        });

        clearAllBtn.setOnAction(e -> {
            allCards.clear();
            notificationList.setItems(FXCollections.observableArrayList(allCards));
        });

        payNowBtn.setOnAction(e -> payNowBtn.setText("✓ Paid"));
        snoozeBtn.setOnAction(e -> snoozeBtn.setText("Snoozed"));

        // PROMO STRIP
        Text protectTitle = new Text("Stay Protected");
        protectTitle.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-fill:white;");
        Text protectDesc = new Text(
                "Enable 2-factor authentication to secure your shop account and receive instant alerts for suspicious activity.");
        protectDesc.setStyle("-fx-font-size:11px; -fx-fill:#dbeafe;");
        protectDesc.setWrappingWidth(330);
        Button enable2FABtn = new Button("Enable Now");
        enable2FABtn.setStyle("-fx-background-color:white; -fx-text-fill:" + Theme.SKY_BLUE_DARK
                + "; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand;");
        VBox protectBox = new VBox(10, protectTitle, protectDesc, enable2FABtn);
        protectBox.setPadding(new Insets(20));
        protectBox.setPrefWidth(420);
        HBox.setHgrow(protectBox, Priority.ALWAYS);
        protectBox.setStyle("-fx-background-color:" + Theme.SKY_BLUE_DARK + "; -fx-background-radius:10;");

        Text tipTitle = new Text("💡  Notification Tip");
        tipTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.WARM_BROWN_TEXT + ";");
        Text tipDesc = new Text(
                "You can customize notification preferences for WhatsApp and SMS alerts from Settings.");
        tipDesc.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        tipDesc.setWrappingWidth(280);
        VBox tipBox = new VBox(8, tipTitle, tipDesc);
        tipBox.setPadding(new Insets(20));
        tipBox.setPrefWidth(340);
        HBox.setHgrow(tipBox, Priority.ALWAYS);
        tipBox.setStyle("-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-border-color:" + Theme.WARM_BROWN_BORDER
                + "; -fx-border-radius:10; -fx-background-radius:10;");

        HBox promoRow = new HBox(15, protectBox, tipBox);
        promoRow.setPadding(new Insets(15, 0, 20, 0));

        VBox mainArea = new VBox(15, pageHeader, tabsRow, notifySection, notificationList, promoRow);
        mainArea.setPadding(new Insets(0, 24, 0, 24));
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        contentBox.getChildren().addAll(topBar, mainArea);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        Theme.applyScrollDarkStyle(scrollPane);
        return scrollPane;
    }

    private VBox createPendingRequestCard(ConnectionRequestModel req, Runnable onAction) {
        VBox icon = new VBox(new Text("👤"));
        icon.setAlignment(Pos.CENTER);
        icon.setPrefSize(40, 40);
        icon.setStyle("-fx-background-color:#1e3a8a; -fx-background-radius:20;");

        Text type = new Text("CONNECTION REQUEST");
        type.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-fill:#60a5fa;");
        Text time = new Text(req.getRequestedAt() != null && req.getRequestedAt().length() >= 10 ? req.getRequestedAt().substring(0, 10) : "Recent");
        time.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_MUTED + ";");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        HBox typeRow = new HBox(type, r, time);
        typeRow.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Customer Connection Request: " + req.getCustomerName());
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");

        Text desc = new Text(req.getCustomerName() + " (ID: " + req.getCustomerId() + ") has requested to connect with your shop '" + req.getShopName() + "'.");
        desc.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        desc.setWrappingWidth(760);

        Button acceptBtn = new Button("✓  Accept");
        acceptBtn.setStyle("-fx-background-color:#059669; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:8 16;");
        
        Button rejectBtn = new Button("✕  Reject");
        rejectBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:8 16;");

        acceptBtn.setOnAction(e -> {
            ConnectionRequestController.getInstance().acceptRequest(req.getRequestId());
            onAction.run();
        });

        rejectBtn.setOnAction(e -> {
            ConnectionRequestController.getInstance().rejectRequest(req.getRequestId());
            onAction.run();
        });

        HBox btnRow = new HBox(12, acceptBtn, rejectBtn);

        VBox textCol = new VBox(6, typeRow, title, desc, btnRow);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        HBox cardRow = new HBox(15, icon, textCol);
        cardRow.setAlignment(Pos.TOP_LEFT);

        VBox card = new VBox(cardRow);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-width:0 0 0 4; -fx-border-color:#2563eb; -fx-background-radius:8; -fx-border-radius:8;");
        return card;
    }

    private VBox createOrderRequestCard(com.eudhari.model.OrderModel order, Runnable onAction, dashboard nav) {
        VBox icon = new VBox(new Text("🛒"));
        icon.setAlignment(Pos.CENTER);
        icon.setPrefSize(40, 40);
        icon.setStyle("-fx-background-color:#4338ca; -fx-background-radius:20;");

        Text type = new Text("CUSTOMER ORDER REQUEST");
        type.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-fill:#a5b4fc;");
        Text time = new Text("Order #" + order.getOrderId());
        time.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_MUTED + ";");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        HBox typeRow = new HBox(type, r, time);
        typeRow.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("New Order Request from " + order.getCustomerName());
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");

        StringBuilder itemsSummary = new StringBuilder();
        if (order.getItems() != null) {
            for (com.eudhari.model.OrderItemModel item : order.getItems()) {
                itemsSummary.append(item.getProductName()).append(" x").append(item.getQuantity()).append(", ");
            }
        }
        String itemsStr = itemsSummary.length() > 2 ? itemsSummary.substring(0, itemsSummary.length() - 2) : "No items";

        Text desc = new Text("Requested Products: " + itemsStr + "\nTotal Amount: ₹" + String.format("%.2f", order.getTotalAmount()));
        desc.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        desc.setWrappingWidth(760);

        Button acceptBtn = new Button("✓  Approve");
        acceptBtn.setStyle("-fx-background-color:#059669; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:8 16;");

        Button rejectBtn = new Button("✕  Reject Order");
        rejectBtn.setStyle("-fx-background-color:#dc2626; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand; -fx-padding:8 16;");

        acceptBtn.setOnAction(e -> {
            com.eudhari.controller.OrderController.getInstance().approveOrder(order.getOrderId());
            com.eudhari.model.OrderModel updatedOrder = com.eudhari.controller.OrderController.getInstance().getOrderById(order.getOrderId());
            onAction.run();
            if (nav != null) {
                nav.navigateToBillingWithOrder(updatedOrder != null ? updatedOrder : order);
            }
        });

        rejectBtn.setOnAction(e -> {
            com.eudhari.controller.OrderController.getInstance().rejectOrder(order.getOrderId());
            onAction.run();
        });

        HBox btnRow = new HBox(12, acceptBtn, rejectBtn);

        VBox textCol = new VBox(6, typeRow, title, desc, btnRow);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        HBox cardRow = new HBox(15, icon, textCol);
        cardRow.setAlignment(Pos.TOP_LEFT);

        VBox card = new VBox(cardRow);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-width:0 0 0 4; -fx-border-color:#6366f1; -fx-background-radius:8; -fx-border-radius:8;");
        return card;
    }

    private VBox createGeneralNotificationCard(com.eudhari.model.NotificationModel n) {
        VBox icon = new VBox(new Text("🔔"));
        icon.setAlignment(Pos.CENTER);
        icon.setPrefSize(40, 40);
        icon.setStyle("-fx-background-color:#1e293b; -fx-background-radius:20;");

        Text type = new Text(n.getType() != null ? n.getType().toUpperCase() : "NOTIFICATION");
        type.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-fill:#38bdf8;");
        Text time = new Text(n.getCreatedAt() != null && n.getCreatedAt().length() >= 10 ? n.getCreatedAt().substring(0, 10) : "Recent");
        time.setStyle("-fx-font-size:11px; -fx-fill:" + Theme.TEXT_MUTED + ";");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        HBox typeRow = new HBox(type, r, time);
        typeRow.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text(n.getTitle());
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");

        Text desc = new Text(n.getMessage());
        desc.setStyle("-fx-font-size:12px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");
        desc.setWrappingWidth(760);

        VBox textCol = new VBox(6, typeRow, title, desc);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        HBox cardRow = new HBox(15, icon, textCol);
        cardRow.setAlignment(Pos.TOP_LEFT);

        VBox card = new VBox(cardRow);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-width:0 0 0 4; -fx-border-color:#38bdf8; -fx-background-radius:8; -fx-border-radius:8;");
        return card;
    }
}
