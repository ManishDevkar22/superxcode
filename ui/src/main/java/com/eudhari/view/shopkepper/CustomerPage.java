package com.eudhari.view.shopkepper;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.*;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.UserModel;
import com.eudhari.model.shopkeppermodel.*;

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

public class CustomerPage {

    public static Parent create(dashboard nav) {
        CustomerPage pageInstance = new CustomerPage();
        return pageInstance.buildRoot(nav);
    }

    public Parent buildRoot(dashboard nav) {
        CustomerController controller = CustomerController.getInstance();
        CustomerStore customerStore = nav.getCustomerStore();
        TransactionStore transactionStore = nav.getTransactionStore();

        // ================= TOP BAR =================
        Label tbTitle = new Label("Customers");
        tbTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label tbSubtitle = new Label("Manage store customers, Customer IDs, and linked billing/udhari records");
        tbSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox tbTitleBox = new VBox(2, tbTitle, tbSubtitle);

        Button tbNotifBtn = new Button("🔔 Notifications");
        tbNotifBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-weight:bold; -fx-background-radius:18; -fx-padding:8 14; -fx-border-color:"
                + Theme.SKY_BLUE + "; -fx-border-radius:18; -fx-cursor:hand;");
        tbNotifBtn.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        com.eudhari.model.UserModel cCurUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String cShopName = cCurUser != null && cCurUser.getShopName() != null && !cCurUser.getShopName().isBlank() ? cCurUser.getShopName() : (cCurUser != null && cCurUser.getName() != null ? cCurUser.getName() + "'s Store" : "Store");
        Button tbProfileBtn = new Button("👤 " + cShopName);
        tbProfileBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:6 12; -fx-cursor:hand;");
        tbProfileBtn.setOnAction(e -> nav.navigateTo(dashboard.PROFILE));

        HBox tbTopRight = new HBox(12, tbNotifBtn, tbProfileBtn);
        tbTopRight.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(20, tbTitleBox, tbTopRight);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 24, 15, 24));
        Theme.applyHeaderStyle(topBar);
        HBox.setHgrow(tbTopRight, Priority.ALWAYS);

        // ================= CUSTOMER LIST (LEFT PANEL) =================
        VBox customerListBox = new VBox(12);
        customerListBox.setPrefWidth(350);
        customerListBox.setPadding(new Insets(14));
        customerListBox.setStyle(Theme.STYLE_CARD);

        Button addCustomerBtn = new Button("+ Add New Customer");
        addCustomerBtn.setMaxWidth(Double.MAX_VALUE);
        addCustomerBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        TextField clSearch = new TextField();
        clSearch.setPromptText("Search by name, ID...");
        Theme.styleTextField(clSearch);

        HBox clFilterTabs = new HBox(6);
        Button tabAll = new Button("All");
        tabAll.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        Button tabUdhari = new Button("Udhari Pending");
        tabUdhari.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-text-fill:" + Theme.TEXT_SECONDARY
                + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:6; -fx-background-radius:6; -fx-cursor:hand;");
        clFilterTabs.getChildren().addAll(tabAll, tabUdhari);

        VBox cardsContainer = new VBox(10);
        ScrollPane leftScroll = new ScrollPane(cardsContainer);
        Theme.applyScrollDarkStyle(leftScroll);
        leftScroll.setPrefHeight(480);
        VBox.setVgrow(leftScroll, Priority.ALWAYS);

        customerListBox.getChildren().addAll(addCustomerBtn, clSearch, clFilterTabs, leftScroll);

        // ================= CUSTOMER DETAIL (RIGHT PANEL) =================
        VBox customerDetailBox = new VBox(15);
        customerDetailBox.setPadding(new Insets(20));
        customerDetailBox.setStyle(Theme.STYLE_CARD);
        HBox.setHgrow(customerDetailBox, Priority.ALWAYS);

        // Customer Details Placeholders
        Label cdAvatar = new Label("??");
        cdAvatar.setStyle("-fx-background-color: " + Theme.SKY_BLUE_BG + "; -fx-text-fill: " + Theme.SKY_BLUE
                + "; -fx-padding: 20; -fx-background-radius: 50; -fx-font-weight: bold; -fx-font-size:18px; -fx-border-color: "
                + Theme.SKY_BLUE + "; -fx-border-radius: 50;");

        Label cdName = new Label("Select a Customer");
        cdName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Label cdId = new Label("Customer ID: --");
        cdId.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.SKY_BLUE + "; -fx-font-weight: bold;");

        Label cdStatus = new Label("--");
        cdStatus.setStyle("-fx-background-color: " + Theme.SKY_BLUE_BG + "; -fx-text-fill: " + Theme.SKY_BLUE
                + "; -fx-padding: 3 8; -fx-background-radius: 10;");

        Label cdPhone = new Label("Phone: --");
        cdPhone.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        Label cdJoined = new Label("Joined: --");
        cdJoined.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        Button setLimitBtn = new Button("💳 Set Udhari Limit");
        setLimitBtn.setStyle("-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-text-fill:" + Theme.WARM_BROWN_TEXT
                + "; -fx-font-weight:bold; -fx-border-color:" + Theme.WARM_BROWN_BORDER + "; -fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;");

        Button editCustomerBtn = new Button("✎ Edit Customer");
        editCustomerBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-weight:bold; -fx-cursor:hand;");

        Button deleteCustomerBtn = new Button("🗑 Delete");
        deleteCustomerBtn.setStyle(
                "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-font-weight:bold; -fx-cursor:hand;");

        HBox actionRowHeader = new HBox(10, setLimitBtn, editCustomerBtn, deleteCustomerBtn);

        VBox cdNameBox = new VBox(4, new HBox(10, cdName, cdStatus), new HBox(12, cdId, cdPhone), cdJoined);
        HBox cdHeader = new HBox(20, cdAvatar, cdNameBox, dashboard.spacer(), actionRowHeader);
        cdHeader.setAlignment(Pos.CENTER_LEFT);

        // Stats boxes
        Label stat1Val = new Label("₹0");
        stat1Val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label stat1Lbl = new Label("Total Purchases");
        stat1Lbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox stat1 = new VBox(4, stat1Lbl, stat1Val);
        stat1.setPadding(new Insets(12));
        stat1.setStyle("-fx-background-color: " + Theme.BG_CARD_ALT + "; -fx-background-radius: 8; -fx-border-color: "
                + Theme.BORDER_DARK + "; -fx-border-radius: 8;");
        HBox.setHgrow(stat1, Priority.ALWAYS);

        Label stat2Val = new Label("0");
        stat2Val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label stat2Lbl = new Label("Total Bills");
        stat2Lbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox stat2 = new VBox(4, stat2Lbl, stat2Val);
        stat2.setPadding(new Insets(12));
        stat2.setStyle("-fx-background-color: " + Theme.BG_CARD_ALT + "; -fx-background-radius: 8; -fx-border-color: "
                + Theme.BORDER_DARK + "; -fx-border-radius: 8;");
        HBox.setHgrow(stat2, Priority.ALWAYS);

        Label stat3Val = new Label("₹0");
        stat3Val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#f87171;");
        Label stat3Lbl = new Label("Pending Udhari");
        stat3Lbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox stat3 = new VBox(4, stat3Lbl, stat3Val);
        stat3.setPadding(new Insets(12));
        stat3.setStyle("-fx-background-color: " + Theme.BG_CARD_ALT + "; -fx-background-radius: 8; -fx-border-color: "
                + Theme.BORDER_DARK + "; -fx-border-radius: 8;");
        HBox.setHgrow(stat3, Priority.ALWAYS);

        Label stat4Val = new Label("₹5,000.00");
        stat4Val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#38bdf8;");
        Label stat4Lbl = new Label("Max Udhari Limit");
        stat4Lbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox stat4 = new VBox(4, stat4Lbl, stat4Val);
        stat4.setPadding(new Insets(12));
        stat4.setStyle("-fx-background-color: " + Theme.BG_CARD_ALT + "; -fx-background-radius: 8; -fx-border-color: "
                + Theme.BORDER_DARK + "; -fx-border-radius: 8;");
        HBox.setHgrow(stat4, Priority.ALWAYS);

        HBox cdStats = new HBox(12, stat1, stat2, stat3, stat4);

        // Transaction History List
        Label historyHeading = new Label("Linked Purchase & History Records");
        historyHeading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        historyHeading.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        VBox cdHistoryBox = new VBox(8);
        ScrollPane historyScroll = new ScrollPane(cdHistoryBox);
        Theme.applyScrollDarkStyle(historyScroll);
        historyScroll.setPrefHeight(250);

        // Action Buttons
        Button newBillBtn = new Button("+ Generate New Bill");
        newBillBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        newBillBtn.setOnAction(e -> nav.navigateTo(dashboard.BILLING));

        Button viewUdhariBtn = new Button("📄 View Udhari Page");
        viewUdhariBtn.setStyle("-fx-background-color: " + Theme.WARM_BEIGE_BG + "; -fx-text-fill: "
                + Theme.WARM_BROWN_TEXT + "; -fx-padding: 10 16; -fx-font-weight:bold; -fx-border-color: "
                + Theme.WARM_BROWN_BORDER + "; -fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;");
        viewUdhariBtn.setOnAction(e -> nav.navigateTo(dashboard.UDHARI));

        HBox cdQuickActions = new HBox(12, newBillBtn, viewUdhariBtn);

        customerDetailBox.getChildren().addAll(cdHeader, cdStats, historyHeading, historyScroll, cdQuickActions);

        // Sync approved connected customers from connectionRequests collection
        UserModel currentSkUser = ProfileController.getInstance().getCurrentUserProfile();
        String currentSkUid = currentSkUser != null && currentSkUser.getUid() != null ? currentSkUser.getUid() : "";
        com.eudhari.model.ShopModel currentShopObj = com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().getShopByOwnerId(currentSkUid);
        String currentShopIdStr = currentShopObj != null && currentShopObj.getShopId() != null ? currentShopObj.getShopId() : "";

        java.util.Set<String> connectedCustomerIds = new java.util.HashSet<>();
        if (!currentSkUid.isBlank()) {
            java.util.List<ConnectionRequestModel> approvedReqs = ConnectionRequestController.getInstance().getApprovedCustomersForShopkeeper(currentSkUid);
            for (ConnectionRequestModel req : approvedReqs) {
                if (req.getCustomerId() != null) {
                    connectedCustomerIds.add(req.getCustomerId().toLowerCase().trim());
                }
                boolean exists = false;
                for (CustomerModel existing : controller.getActiveCustomers()) {
                    if ((existing.getUid() != null && existing.getUid().equalsIgnoreCase(req.getCustomerId())) ||
                        existing.getId().equalsIgnoreCase(req.getCustomerId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    CustomerModel newCust = new CustomerModel(
                            req.getCustomerId(),
                            req.getCustomerName() != null && !req.getCustomerName().isBlank() ? req.getCustomerName() : "Connected Customer",
                            "",
                            req.getRequestedAt() != null && req.getRequestedAt().length() >= 10 ? req.getRequestedAt().substring(0, 10) : "2026-08-21",
                            "Connected Customer",
                            0.0,
                            0.0
                    );
                    newCust.setUid(req.getCustomerId());
                    newCust.setShopId(req.getShopId() != null ? req.getShopId() : currentShopIdStr);
                    controller.getAllCustomers().add(newCust);
                }
            }
        }

        // Selected Customer Holder
        final CustomerModel[] selectedCustomer = new CustomerModel[] { null };

        FilteredList<CustomerModel> filteredList = new FilteredList<>(controller.getActiveCustomers(), c -> true);

        Runnable updateDetailPanel = () -> {
            CustomerModel c = selectedCustomer[0];
            if (c == null && !filteredList.isEmpty()) {
                c = filteredList.get(0);
                selectedCustomer[0] = c;
            }

            if (c != null) {
                String initials = c.getName().replaceAll("^(\\w)\\w*\\s*(\\w)?.*$", "$1$2").toUpperCase();
                if (initials.isBlank())
                    initials = "CU";
                cdAvatar.setText(initials);
                cdName.setText(c.getName());
                cdId.setText("Customer ID: " + c.getId());
                cdStatus.setText(c.getStatus());
                cdPhone.setText("Phone: " + c.getPhone());
                cdJoined.setText("Joined: " + c.getJoinedDate());

                stat1Val.setText(String.format("₹%.2f", c.getTotalPurchases()));
                stat3Val.setText(String.format("₹%.2f", c.getPendingUdhari()));
                stat4Val.setText(String.format("₹%.2f", c.getUdhariLimit()));

                com.eudhari.model.UserModel skUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
                String skUid = skUser != null && skUser.getUid() != null ? skUser.getUid() : "";
                com.eudhari.model.ShopModel currentShop = com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().getShopByOwnerId(skUid);
                String currentShopId = currentShop != null && currentShop.getShopId() != null ? currentShop.getShopId() : "";

                double totalRemainingUdhari = com.eudhari.controller.UdhariController.getInstance().getTotalRemainingUdhariForCustomer(c.getId(), currentShopId);
                stat3Val.setText(String.format("₹%.2f", totalRemainingUdhari));
                c.setPendingUdhari(totalRemainingUdhari);

                // Load linked billing history for customerId + shopId
                cdHistoryBox.getChildren().clear();
                List<com.eudhari.model.BillingModel> custBillings = com.eudhari.controller.shopkeppercontroller.BillingController.getInstance().getBillingForCustomerAndShop(c.getId(), currentShopId);
                int billCount = 0;
                double totalPurchasesSum = 0.0;

                if (custBillings != null && !custBillings.isEmpty()) {
                    for (com.eudhari.model.BillingModel b : custBillings) {
                        billCount++;
                        totalPurchasesSum += b.getTotalAmount();
                        String dt = b.getCreatedAt() != null && b.getCreatedAt().length() >= 10 ? b.getCreatedAt().substring(0, 10) : "Recent";
                        Label entry = new Label(dt + " | Bill #" + b.getBillingId() + " | Items: " + b.getItemsSummary() + " | Total: ₹" + String.format("%.2f", b.getTotalAmount()) + " (" + b.getPaymentMethod() + " - " + b.getPaymentStatus() + ")");
                        entry.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-size:12px;");

                        HBox row = new HBox(8, entry);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setStyle("-fx-padding: 8; -fx-background-color: " + Theme.BG_CARD_ALT
                                + "; -fx-background-radius: 6; -fx-border-color:" + Theme.BORDER_DARK
                                + "; -fx-border-radius:6;");
                        cdHistoryBox.getChildren().add(row);
                    }
                }
                stat1Val.setText(String.format("₹%.2f", totalPurchasesSum));
                stat2Val.setText(String.valueOf(billCount));

                if (billCount == 0) {
                    Label emptyHistory = new Label("No billing history linked to customer " + c.getName() + " [" + c.getId() + "] for this shop.");
                    emptyHistory.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-padding:10;");
                    cdHistoryBox.getChildren().add(emptyHistory);
                }
            }
        };

        Runnable refreshLeftCards = () -> {
            cardsContainer.getChildren().clear();
            String query = clSearch.getText() != null ? clSearch.getText().toLowerCase().trim() : "";
            boolean onlyUdhari = tabUdhari.getStyle().contains(Theme.SKY_BLUE_DARK);

            filteredList.setPredicate(c -> {
                // Remove dummy customers: only show if connected to shopkeeper or explicitly created for this shop
                boolean isConnected = false;
                if (c.getUid() != null && connectedCustomerIds.contains(c.getUid().toLowerCase().trim())) {
                    isConnected = true;
                }
                if (c.getId() != null && connectedCustomerIds.contains(c.getId().toLowerCase().trim())) {
                    isConnected = true;
                }
                if (!currentShopIdStr.isBlank() && c.getShopId() != null && c.getShopId().equalsIgnoreCase(currentShopIdStr)) {
                    isConnected = true;
                }
                // If connected list is empty or customer matches approved connections
                if (!connectedCustomerIds.isEmpty() && !isConnected) {
                    return false;
                }

                if (onlyUdhari && c.getPendingUdhari() <= 0)
                    return false;
                if (!query.isEmpty()) {
                    return c.getName().toLowerCase().contains(query) || c.getId().toLowerCase().contains(query)
                            || c.getPhone().contains(query);
                }
                return true;
            });

            for (CustomerModel c : filteredList) {
                VBox card = createCustomerCard(c, selectedCustomer, updateDetailPanel);
                cardsContainer.getChildren().add(card);
            }

            updateDetailPanel.run();
        };

        clSearch.textProperty().addListener((obs, oldVal, newVal) -> refreshLeftCards.run());

        tabAll.setOnAction(e -> {
            tabAll.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            tabUdhari.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-text-fill:" + Theme.TEXT_SECONDARY
                    + "; -fx-border-color:" + Theme.BORDER_DARK
                    + "; -fx-border-radius:6; -fx-background-radius:6; -fx-cursor:hand;");
            refreshLeftCards.run();
        });

        tabUdhari.setOnAction(e -> {
            tabUdhari.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            tabAll.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-text-fill:" + Theme.TEXT_SECONDARY
                    + "; -fx-border-color:" + Theme.BORDER_DARK
                    + "; -fx-border-radius:6; -fx-background-radius:6; -fx-cursor:hand;");
            refreshLeftCards.run();
        });

        customerStore.getAllCustomers().addListener((ListChangeListener<CustomerModel>) c -> refreshLeftCards.run());
        transactionStore.getAllTransactions()
                .addListener((ListChangeListener<TransactionModel>) c -> updateDetailPanel.run());

        addCustomerBtn.setOnAction(e -> showAddCustomerDialog(controller, selectedCustomer, updateDetailPanel));
        setLimitBtn.setOnAction(e -> {
            if (selectedCustomer[0] != null) {
                showSetUdhariLimitDialog(selectedCustomer[0], updateDetailPanel);
            }
        });
        editCustomerBtn.setOnAction(e -> {
            if (selectedCustomer[0] != null) {
                showEditCustomerDialog(controller, selectedCustomer[0], updateDetailPanel);
            }
        });
        deleteCustomerBtn.setOnAction(e -> {
            if (selectedCustomer[0] != null) {
                Alert confirm = new Alert(
                        Alert.AlertType.CONFIRMATION, "Are you sure you want to delete customer '"
                                + selectedCustomer[0].getName() + "' [" + selectedCustomer[0].getId() + "]?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(ans -> {
                    if (ans == ButtonType.YES) {
                        controller.deleteCustomer(selectedCustomer[0]);
                        selectedCustomer[0] = null;
                        refreshLeftCards.run();
                    }
                });
            }
        });

        refreshLeftCards.run();

        // ================= ROOT LAYOUT =================
        BorderPane borderpane = new BorderPane();
        borderpane.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        borderpane.setTop(topBar);

        HBox centerContent = new HBox(15, customerListBox, customerDetailBox);
        centerContent.setPadding(new Insets(15));
        HBox.setHgrow(customerDetailBox, Priority.ALWAYS);

        borderpane.setCenter(centerContent);
        return borderpane;
    }

    private static VBox createCustomerCard(CustomerModel c, CustomerModel[] selectedHolder, Runnable onSelect) {
        Label nameLbl = new Label(c.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size:14px; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label idLbl = new Label("[" + c.getId() + "]  " + c.getPhone());
        idLbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-size: 11px;");

        Label dueLbl = new Label(
                c.getPendingUdhari() > 0 ? String.format("₹%.0f Pending", c.getPendingUdhari()) : "No Due");
        dueLbl.setStyle(c.getPendingUdhari() > 0 ? "-fx-text-fill: #f87171; -fx-font-size: 11px; -fx-font-weight:bold;"
                : "-fx-text-fill: #4ade80; -fx-font-size: 11px;");

        VBox textBox = new VBox(3, nameLbl, idLbl);
        HBox row = new HBox(10, textBox, dashboard.spacer(), dueLbl);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        card.setPadding(new Insets(12));
        card.setCursor(javafx.scene.Cursor.HAND);
        boolean isSelected = selectedHolder[0] != null && selectedHolder[0].getId().equals(c.getId());
        card.setStyle(isSelected
                ? "-fx-background-color: " + Theme.SKY_BLUE_BG + "; -fx-background-radius: 8; -fx-border-color: "
                        + Theme.SKY_BLUE + "; -fx-border-radius: 8;"
                : "-fx-background-color: " + Theme.BG_CARD_ALT + "; -fx-background-radius: 8; -fx-border-color: "
                        + Theme.BORDER_DARK + "; -fx-border-radius: 8;");

        card.setOnMouseClicked(e -> {
            selectedHolder[0] = c;
            onSelect.run();
        });

        return card;
    }

    private static void showAddCustomerDialog(CustomerController controller, CustomerModel[] selectedHolder,
            Runnable refresh) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        ButtonType save = new ButtonType("Save Customer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        String autoId = controller.generateUniqueCustomerId();
        Label idVal = new Label(autoId);
        idVal.setStyle("-fx-font-weight:bold; -fx-text-fill:" + Theme.SKY_BLUE + ";");

        TextField name = new TextField();
        name.setPromptText("Customer Name");
        TextField phone = new TextField();
        phone.setPromptText("Phone Number");

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Regular Customer", "VIP Customer", "New Customer");
        status.getSelectionModel().selectFirst();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.addRow(0, new Label("Customer ID:"), idVal);
        form.addRow(1, new Label("Name:"), name);
        form.addRow(2, new Label("Phone:"), phone);
        form.addRow(3, new Label("Status:"), status);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !name.getText().isBlank()) {
                CustomerModel c = controller.addCustomer(name.getText().trim(), phone.getText().trim(),
                        status.getValue());
                selectedHolder[0] = c;
                refresh.run();
            }
        });
    }

    private static void showEditCustomerDialog(CustomerController controller, CustomerModel c, Runnable refresh) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer - " + c.getId());
        ButtonType save = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        Label idVal = new Label(c.getId());
        idVal.setStyle("-fx-font-weight:bold; -fx-text-fill:" + Theme.SKY_BLUE + ";");

        TextField name = new TextField(c.getName());
        TextField phone = new TextField(c.getPhone());

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Regular Customer", "VIP Customer", "New Customer");
        status.setValue(c.getStatus());

        TextField limitField = new TextField(String.valueOf(c.getUdhariLimit()));
        Theme.styleTextField(limitField);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.addRow(0, new Label("Customer ID:"), idVal);
        form.addRow(1, new Label("Name:"), name);
        form.addRow(2, new Label("Phone:"), phone);
        form.addRow(3, new Label("Status:"), status);
        form.addRow(4, new Label("Max Udhari Limit (₹):"), limitField);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !name.getText().isBlank()) {
                controller.updateCustomer(c, name.getText().trim(), phone.getText().trim(), status.getValue());
                try {
                    double lim = Double.parseDouble(limitField.getText().trim());
                    c.setUdhariLimit(lim);
                } catch (Exception ignored) {}
                refresh.run();
            }
        });
    }

    private static void showSetUdhariLimitDialog(CustomerModel c, Runnable refresh) {
        if (c == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Maximum Udhari Limit - " + c.getName());
        ButtonType save = new ButtonType("Save Limit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        Label customerInfo = new Label("Customer: " + c.getName() + " (" + c.getId() + ")\nCurrent Pending Udhari: ₹" + String.format("%.2f", c.getPendingUdhari()));
        customerInfo.setStyle("-fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        TextField limitField = new TextField(String.valueOf(c.getUdhariLimit()));
        limitField.setPromptText("Enter max Udhari limit in ₹ (e.g. 5000)");
        Theme.styleTextField(limitField);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(18));
        form.addRow(0, customerInfo);
        form.addRow(1, new Label("Max Udhari Limit (₹):"), limitField);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !limitField.getText().isBlank()) {
                try {
                    double limit = Double.parseDouble(limitField.getText().trim());
                    c.setUdhariLimit(limit);
                    refresh.run();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Udhari Limit for " + c.getName() + " updated to ₹" + String.format("%.2f", limit), ButtonType.OK);
                    alert.showAndWait();
                } catch (NumberFormatException ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Please enter a valid numeric value for Udhari limit.");
                    err.showAndWait();
                }
            }
        });
    }
}