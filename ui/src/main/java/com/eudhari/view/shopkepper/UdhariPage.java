package com.eudhari.view.shopkepper;

import com.eudhari.model.shopkeppermodel.TransactionModel;
import com.eudhari.model.shopkeppermodel.TransactionStore;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.List;
import javafx.scene.text.FontWeight;

public class UdhariPage {

    public static Parent create(dashboard nav) {
        UdhariPage instance = new UdhariPage();
        return instance.buildRoot(nav);
    }

    public Parent buildRoot(dashboard nav) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");

        TransactionStore txStore = nav.getTransactionStore();

        // Top Header
        Label title = new Label("Udhari Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label subtitle = new Label("Track customer credit balances, udhari records, and settlements");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox titleBox = new VBox(2, title, subtitle);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search Udhari by Customer Name or ID...");
        searchField.setPrefWidth(350);
        Theme.styleTextField(searchField);

        Button notifBtn = new Button("🔔 Notifications");
        notifBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + ";-fx-text-fill:" + Theme.SKY_BLUE
                + ";-fx-font-weight:bold;-fx-background-radius:18;-fx-padding:8 14;-fx-border-color:" + Theme.SKY_BLUE
                + ";-fx-border-radius:18;-fx-cursor:hand;");
        notifBtn.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        com.eudhari.model.UserModel uCurUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String uShopName = uCurUser != null && uCurUser.getShopName() != null && !uCurUser.getShopName().isBlank() ? uCurUser.getShopName() : (uCurUser != null && uCurUser.getName() != null ? uCurUser.getName() + "'s Store" : "Store");
        Button profileBtn = new Button("👤 " + uShopName);
        profileBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + ";-fx-text-fill:" + Theme.TEXT_PRIMARY
                + ";-fx-border-color:" + Theme.BORDER_DARK
                + ";-fx-border-radius:8;-fx-background-radius:8;-fx-padding:6 12;-fx-cursor:hand;");
        profileBtn.setOnAction(e -> nav.navigateTo(dashboard.PROFILE));

        HBox topRight = new HBox(15, searchField, notifBtn, profileBtn);
        topRight.setAlignment(Pos.CENTER_RIGHT);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(20, titleBox, topSpacer, topRight);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 24, 15, 24));
        Theme.applyHeaderStyle(topBar);
        borderPane.setTop(topBar);

        // Stats Row
        String cardStyle = Theme.STYLE_CARD;

        Label stat1Title = new Label("TOTAL OUTSTANDING UDHARI");
        stat1Title.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label stat1Val = new Label("₹0");
        stat1Val.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        stat1Val.setTextFill(Color.web("#f87171"));

        Label stat1Sub = new Label("Cumulative credit due");
        stat1Sub.setStyle("-fx-text-fill:#f87171; -fx-font-size:12px;");
        VBox stat1 = new VBox(8, stat1Title, stat1Val, stat1Sub);
        stat1.setStyle(cardStyle);
        HBox.setHgrow(stat1, Priority.ALWAYS);

        Label stat2Title = new Label("PENDING UDHARI RECORDS");
        stat2Title.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label stat2Val = new Label("0");
        stat2Val.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        stat2Val.setTextFill(Color.web(Theme.WARM_BROWN_TEXT));

        Label stat2Sub = new Label("Unsettled transactions");
        stat2Sub.setStyle("-fx-text-fill:" + Theme.WARM_BROWN_TEXT + "; -fx-font-size:12px;");
        VBox stat2 = new VBox(8, stat2Title, stat2Val, stat2Sub);
        stat2.setStyle(cardStyle);
        HBox.setHgrow(stat2, Priority.ALWAYS);

        Label stat3Title = new Label("SETTLED / PAID TODAY");
        stat3Title.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label stat3Val = new Label("₹0");
        stat3Val.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        stat3Val.setTextFill(Color.web("#4ade80"));

        Label stat3Sub = new Label("Cleared credit payments");
        stat3Sub.setStyle("-fx-text-fill:#4ade80; -fx-font-size:12px;");
        VBox stat3 = new VBox(8, stat3Title, stat3Val, stat3Sub);
        stat3.setStyle(cardStyle);
        HBox.setHgrow(stat3, Priority.ALWAYS);

        HBox statsRow = new HBox(18, stat1, stat2, stat3);

        // Header for Udhari list
        Label sectionHeading = new Label("Customer Udhari Records");
        sectionHeading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionHeading.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        ToggleButton filterAll = new ToggleButton("All Pending");
        filterAll.setSelected(true);
        filterAll.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        ToggleButton filterPaid = new ToggleButton("Settled / Paid History");
        filterPaid.setStyle(Theme.STYLE_BUTTON_SECONDARY);

        ToggleGroup filterGroup = new ToggleGroup();
        filterAll.setToggleGroup(filterGroup);
        filterPaid.setToggleGroup(filterGroup);

        HBox sectionHeader = new HBox(15, sectionHeading, dashboard.spacer(), filterAll, filterPaid);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        // VBox containing Udhari Record Cards
        VBox recordsBox = new VBox(14);
        recordsBox.setPadding(new Insets(10, 0, 10, 0));

        FilteredList<TransactionModel> filteredList = new FilteredList<>(txStore.getAllTransactions(), tx -> true);

        com.eudhari.model.UserModel skUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String skUid = skUser != null && skUser.getUid() != null ? skUser.getUid() : "";
        com.eudhari.model.ShopModel currentShop = com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().getShopByOwnerId(skUid);
        String currentShopId = currentShop != null && currentShop.getShopId() != null ? currentShop.getShopId() : "";

        Runnable[] refreshHolder = new Runnable[1];
        refreshHolder[0] = () -> {
            recordsBox.getChildren().clear();
            boolean showPaid = filterPaid.isSelected();
            String query = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";

            List<com.eudhari.model.UdhariModel> shopUdhari = com.eudhari.controller.UdhariController.getInstance().getUdhariForShop(currentShopId);
            double totalDue = 0.0;
            int pendingCount = 0;
            int settledCount = 0;

            if (shopUdhari != null) {
                for (com.eudhari.model.UdhariModel u : shopUdhari) {
                    if (u.getRemainingAmount() > 0) {
                        totalDue += u.getRemainingAmount();
                        pendingCount++;
                    } else {
                        settledCount++;
                    }

                    boolean isSettled = u.getRemainingAmount() <= 0 || "PAID".equalsIgnoreCase(u.getStatus());
                    if (showPaid != isSettled) continue;

                    if (!query.isEmpty()) {
                        boolean matchesCust = u.getCustomerName().toLowerCase().contains(query) || u.getCustomerId().toLowerCase().contains(query);
                        if (!matchesCust) continue;
                    }

                    HBox card = createFirestoreUdhariCard(u, () -> refreshHolder[0].run());
                    recordsBox.getChildren().add(card);
                }
            }

            stat1Val.setText(String.format("₹%.0f", totalDue));
            stat2Val.setText(String.valueOf(pendingCount));
            stat3Val.setText(String.valueOf(settledCount));

            if (recordsBox.getChildren().isEmpty()) {
                Label emptyLbl = new Label(
                        showPaid ? "No settled udhari history found." : "No pending customer udhari records found!");
                emptyLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-padding:30; -fx-font-size:14px;");
                recordsBox.getChildren().add(emptyLbl);
            }
        };

        filterAll.setOnAction(e -> {
            filterAll.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            filterPaid.setStyle(Theme.STYLE_BUTTON_SECONDARY);
            refreshHolder[0].run();
        });
        filterPaid.setOnAction(e -> {
            filterPaid.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            filterAll.setStyle(Theme.STYLE_BUTTON_SECONDARY);
            refreshHolder[0].run();
        });
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshHolder[0].run());

        refreshHolder[0].run();

        VBox mainContent = new VBox(20, statsRow, sectionHeader, recordsBox);
        mainContent.setPadding(new Insets(24, 28, 28, 28));

        ScrollPane scrollPane = new ScrollPane(mainContent);
        Theme.applyScrollDarkStyle(scrollPane);
        borderPane.setCenter(scrollPane);

        return borderPane;
    }

    private static HBox createUdhariCard(TransactionStore txStore, TransactionModel tx) {
        // Customer Name & Customer ID Box
        Label nameLbl = new Label(tx.getCustomerName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLbl.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label idBadge = new Label("ID: " + tx.getCustomerId());
        idBadge.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-size:11px; -fx-padding:2 7; -fx-background-radius:6;");

        HBox custHeader = new HBox(8, nameLbl, idBadge);
        custHeader.setAlignment(Pos.CENTER_LEFT);

        Label itemsLbl = new Label("Items: " + tx.getItemsSummary());
        itemsLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:13px;");

        Label dateLbl = new Label("Date: " + tx.getDateTime() + "  |  Bill #" + tx.getBillId());
        dateLbl.setStyle("-fx-text-fill:" + Theme.TEXT_MUTED + "; -fx-font-size:11px;");

        VBox leftBox = new VBox(6, custHeader, itemsLbl, dateLbl);

        // Amount & Status Box
        Label amountLbl = new Label(String.format("₹%.2f", tx.getTotalAmount()));
        amountLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        amountLbl.setTextFill(Color.web("#f87171"));

        Label statusBadge = new Label(tx.getStatus());
        if ("Paid".equalsIgnoreCase(tx.getStatus())) {
            statusBadge.setStyle(
                    "-fx-background-color:#14382c; -fx-text-fill:#4ade80; -fx-font-weight:bold; -fx-padding:4 10; -fx-background-radius:12;");
        } else {
            statusBadge
                    .setStyle("-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-text-fill:" + Theme.WARM_BROWN_TEXT
                            + "; -fx-font-weight:bold; -fx-padding:4 10; -fx-background-radius:12; -fx-border-color:"
                            + Theme.WARM_BROWN_BORDER + "; -fx-border-radius:12;");
        }

        VBox middleBox = new VBox(4, amountLbl, statusBadge);
        middleBox.setAlignment(Pos.CENTER_RIGHT);

        // Action Button
        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle(
                "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete Udhari record "
                    + tx.getBillId() + " for " + tx.getCustomerName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(ans -> {
                if (ans == ButtonType.YES) {
                    txStore.deleteTransaction(tx);
                }
            });
        });

        VBox actionBox = new VBox();
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setSpacing(8);
        if ("Udhari Pending".equalsIgnoreCase(tx.getStatus())) {
            Button settleBtn = new Button("✓ Mark as Paid");
            settleBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            settleBtn.setOnAction(e -> {
                txStore.markUdhariPaid(tx);
            });
            actionBox.getChildren().addAll(settleBtn, deleteBtn);
        } else {
            Label paidCheck = new Label("✓ Cleared");
            paidCheck.setStyle("-fx-text-fill:#4ade80; -fx-font-weight:bold;");
            actionBox.getChildren().addAll(paidCheck, deleteBtn);
        }

        HBox card = new HBox(20, leftBox, dashboard.spacer(), middleBox, actionBox);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle(Theme.STYLE_CARD);

        return card;
    }

    private static HBox createFirestoreUdhariCard(com.eudhari.model.UdhariModel u, Runnable refreshAction) {
        Label nameLbl = new Label(u.getCustomerName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLbl.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label idBadge = new Label("Cust ID: " + u.getCustomerId());
        idBadge.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-size:11px; -fx-padding:2 7; -fx-background-radius:6;");

        HBox custHeader = new HBox(8, nameLbl, idBadge);
        custHeader.setAlignment(Pos.CENTER_LEFT);

        Label billLbl = new Label("Bill #" + u.getBillingId() + "  |  Order #" + u.getOrderId());
        billLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:13px;");

        String dtStr = u.getCreatedAt() != null && u.getCreatedAt().length() >= 10 ? u.getCreatedAt().substring(0, 10) : "Recent";
        Label dateLbl = new Label("Created: " + dtStr + "  |  Total: ₹" + String.format("%.2f", u.getTotalAmount()));
        dateLbl.setStyle("-fx-text-fill:" + Theme.TEXT_MUTED + "; -fx-font-size:11px;");

        VBox leftBox = new VBox(6, custHeader, billLbl, dateLbl);

        Label amountLbl = new Label(String.format("Due: ₹%.2f", u.getRemainingAmount()));
        amountLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        amountLbl.setTextFill(Color.web("#f87171"));

        Label statusBadge = new Label(u.getStatus());
        if ("PAID".equalsIgnoreCase(u.getStatus()) || u.getRemainingAmount() <= 0) {
            statusBadge.setStyle(
                    "-fx-background-color:#14382c; -fx-text-fill:#4ade80; -fx-font-weight:bold; -fx-padding:4 10; -fx-background-radius:12;");
        } else {
            statusBadge
                    .setStyle("-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-text-fill:" + Theme.WARM_BROWN_TEXT
                            + "; -fx-font-weight:bold; -fx-padding:4 10; -fx-background-radius:12; -fx-border-color:"
                            + Theme.WARM_BROWN_BORDER + "; -fx-border-radius:12;");
        }

        VBox middleBox = new VBox(4, amountLbl, statusBadge);
        middleBox.setAlignment(Pos.CENTER_RIGHT);

        VBox actionBox = new VBox();
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setSpacing(8);

        if (u.getRemainingAmount() > 0 && !"PAID".equalsIgnoreCase(u.getStatus())) {
            Button settleBtn = new Button("✓ Mark as Paid");
            settleBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            settleBtn.setOnAction(e -> {
                com.eudhari.controller.UdhariController.getInstance().payUdhari(u.getUdhariId(), u.getRemainingAmount());
                refreshAction.run();
            });
            actionBox.getChildren().add(settleBtn);
        } else {
            Label paidCheck = new Label("✓ Cleared");
            paidCheck.setStyle("-fx-text-fill:#4ade80; -fx-font-weight:bold;");
            actionBox.getChildren().add(paidCheck);
        }

        HBox card = new HBox(20, leftBox, dashboard.spacer(), middleBox, actionBox);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle(Theme.STYLE_CARD);

        return card;
    }
}
