package com.eudhari.view.shopkepper;

import com.eudhari.controller.shopkeppercontroller.*;
import com.eudhari.model.shopkeppermodel.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingPage {

    private static final Map<String, Double> PRICE_MAP = new HashMap<>();
    static {
        PRICE_MAP.put("Rice (Premium)", 65.0);
        PRICE_MAP.put("Sugar", 45.0);
        PRICE_MAP.put("Sunflower Oil (1L)", 135.0);
        PRICE_MAP.put("Wheat Flour (Atta)", 40.0);
        PRICE_MAP.put("Toor Dal", 110.0);
        PRICE_MAP.put("Masoor Dal", 85.0);
        PRICE_MAP.put("Chana Dal", 95.0);
        PRICE_MAP.put("Salt", 20.0);
        PRICE_MAP.put("Tea (250g)", 90.0);
        PRICE_MAP.put("Poha (500g)", 35.0);
        PRICE_MAP.put("Fresh Whole Milk", 35.0);
        PRICE_MAP.put("Whole Wheat Bread", 40.0);
    }

    public static Parent create(dashboard nav) {
        BorderPane borderpane = new BorderPane();
        borderpane.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");

        CustomerStore customerStore = nav.getCustomerStore();
        TransactionStore transactionStore = nav.getTransactionStore();
        ProductStore productStore = nav.getProductStore();
        BillingController billingController = BillingController.getInstance();
        CustomerController customerController = CustomerController.getInstance();

        // ---------------- TOP BAR ----------------
        Label title = new Label("Billing");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label subtitle = new Label("Add products, select customer, and generate bill");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox titleBox = new VBox(title, subtitle);

        Button notifBtn = new Button("🔔 Notifications");
        notifBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + ";-fx-text-fill:" + Theme.SKY_BLUE
                + ";-fx-font-weight:bold;-fx-background-radius:18;-fx-padding:8 14;-fx-border-color:" + Theme.SKY_BLUE
                + ";-fx-border-radius:18;-fx-cursor:hand;");
        notifBtn.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        com.eudhari.model.UserModel bCurUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String bShopName = bCurUser != null && bCurUser.getShopName() != null && !bCurUser.getShopName().isBlank() ? bCurUser.getShopName() : (bCurUser != null && bCurUser.getName() != null ? bCurUser.getName() + "'s Store" : "Store");
        Button profileBtn = new Button("👤 " + bShopName + " ⌄");
        profileBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + ";-fx-text-fill:" + Theme.TEXT_PRIMARY
                + ";-fx-border-color:" + Theme.BORDER_DARK
                + ";-fx-border-radius:8;-fx-background-radius:8;-fx-padding:6 12;-fx-cursor:hand;");
        profileBtn.setOnAction(e -> nav.navigateTo(dashboard.PROFILE));

        HBox topRight = new HBox(15, notifBtn, profileBtn);
        topRight.setAlignment(Pos.CENTER_RIGHT);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topBar = new HBox(20, titleBox, topSpacer, topRight);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 24, 15, 24));
        Theme.applyHeaderStyle(topBar);
        borderpane.setTop(topBar);

        
        // Customer Selection Component
        Text custLabel = new Text("Customer (Linked to Customer ID)");
        custLabel.setStyle("-fx-font-size: 11px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");

        ComboBox<CustomerModel> customerCombo = new ComboBox<>(customerStore.getActiveCustomers());
        customerCombo.setPrefWidth(280);
        Theme.styleComboBox(customerCombo);

        customerCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CustomerModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " [" + item.getId() + "] - " + item.getPhone());
                }
            }
        });
        customerCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CustomerModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Customer...");
                } else {
                    setText(item.getName() + " [" + item.getId() + "]");
                }
            }
        });

        if (!customerStore.getActiveCustomers().isEmpty()) {
            customerCombo.getSelectionModel().selectFirst();
        }

        Button newCustBtn = new Button("+ New Customer");
        newCustBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        newCustBtn.setOnAction(e -> showAddNewCustomerDialog(customerController, customerCombo));

        // APPROVED / ACCEPTED Customer Orders Selector for Billing
        com.eudhari.model.UserModel skUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentSkId = skUser != null && skUser.getUid() != null ? skUser.getUid() : "";

        List<com.eudhari.model.OrderModel> acceptedOrders = new java.util.ArrayList<>();
        if (!currentSkId.isBlank()) {
            List<com.eudhari.model.OrderModel> allSkOrders = com.eudhari.controller.OrderController.getInstance().getOrdersForShopkeeper(currentSkId);
            if (allSkOrders != null) {
                for (com.eudhari.model.OrderModel o : allSkOrders) {
                    if ("APPROVED".equalsIgnoreCase(o.getStatus()) || "ACCEPTED".equalsIgnoreCase(o.getStatus())) {
                        acceptedOrders.add(o);
                    }
                }
            }
        }

        ComboBox<com.eudhari.model.OrderModel> acceptedOrderCombo = new ComboBox<>();
        acceptedOrderCombo.setPrefWidth(400);
        Theme.styleComboBox(acceptedOrderCombo);
        acceptedOrderCombo.setPromptText(acceptedOrders.isEmpty() ? "No APPROVED customer orders pending billing" : "Select APPROVED Customer Order to Bill...");

        for (com.eudhari.model.OrderModel o : acceptedOrders) {
            acceptedOrderCombo.getItems().add(o);
        }

        acceptedOrderCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(com.eudhari.model.OrderModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Order #" + item.getOrderId() + " - " + item.getCustomerName() + " (₹" + String.format("%.2f", item.getTotalAmount()) + ")");
                }
            }
        });
        acceptedOrderCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(com.eudhari.model.OrderModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(acceptedOrders.isEmpty() ? "No APPROVED orders pending billing" : "Select APPROVED Customer Order...");
                } else {
                    setText("Order #" + item.getOrderId() + " - " + item.getCustomerName() + " (₹" + String.format("%.2f", item.getTotalAmount()) + ")");
                }
            }
        });

        VBox orderSelectBox = new VBox(6);
        Label orderSelLabel = new Label("📦 Process Bill for APPROVED Customer Order:");
        orderSelLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        orderSelectBox.getChildren().addAll(orderSelLabel, acceptedOrderCombo);
        orderSelectBox.setPadding(new Insets(15));
        orderSelectBox.setStyle(Theme.STYLE_CARD);

        VBox custInfoBox = new VBox(4, custLabel, customerCombo);
        Text custIcon = new Text("👤");
        custIcon.setStyle("-fx-fill:" + Theme.SKY_BLUE + "; -fx-font-size:18px;");
        HBox customerBox = new HBox(12, custIcon, custInfoBox, dashboard.spacer(), newCustBtn);
        customerBox.setAlignment(Pos.CENTER_LEFT);
        customerBox.setPadding(new Insets(15));
        customerBox.setStyle(Theme.STYLE_CARD);

        // Selected products header
        Label productsTitle = new Label("Selected Products");
        productsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label colProduct = new Label("Product");
        colProduct.setPrefWidth(220);
        colProduct.setStyle("-fx-font-size: 12px; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        Label colPrice = new Label("Price");
        colPrice.setPrefWidth(120);
        colPrice.setStyle("-fx-font-size: 12px; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        Label colQty = new Label("Qty");
        colQty.setPrefWidth(150);
        colQty.setStyle("-fx-font-size: 12px; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        Label colTotal = new Label("Total");
        colTotal.setPrefWidth(100);
        colTotal.setStyle("-fx-font-size: 12px; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        HBox productHeader = new HBox();
        productHeader.getChildren().addAll(colProduct, colPrice, colQty, colTotal);
        productHeader.setPadding(new Insets(8, 0, 8, 0));

        Map<String, Integer> currentBasket = nav.getBasket();
        VBox productRows = new VBox(10);
        double[] grandTotalAmountHolder = new double[] { 0.0 };
        int[] totalQuantityCountHolder = new int[] { 0 };
        int[] totalItemsCountHolder = new int[] { currentBasket.size() };

        Text totalItemsVal = new Text(String.valueOf(totalItemsCountHolder[0]));
        totalItemsVal.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");

        Text totalQtyVal = new Text(String.valueOf(totalQuantityCountHolder[0]));
        totalQtyVal.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");

        Text subtotalVal = new Text(String.format("₹%.2f", grandTotalAmountHolder[0]));
        subtotalVal.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + ";");

        Text grandVal = new Text(String.format("₹%.2f", grandTotalAmountHolder[0]));
        grandVal.setStyle("-fx-font-size: 16px; -fx-font-weight:bold; -fx-fill:" + Theme.SKY_BLUE + ";");

        Runnable populateFromBasket = () -> {
            productRows.getChildren().clear();
            grandTotalAmountHolder[0] = 0.0;
            totalQuantityCountHolder[0] = 0;
            totalItemsCountHolder[0] = currentBasket.size();

            for (Map.Entry<String, Integer> entry : currentBasket.entrySet()) {
                String pName = entry.getKey();
                int pQty = entry.getValue();

                double pPrice = 50.0;
                for (ProductModel pm : productStore.getAllProducts()) {
                    if (pm.getName().equalsIgnoreCase(pName)) {
                        pPrice = pm.getPrice();
                        break;
                    }
                }
                if (pPrice == 50.0) pPrice = PRICE_MAP.getOrDefault(pName, 50.0);

                double pTotal = pPrice * pQty;
                grandTotalAmountHolder[0] += pTotal;
                totalQuantityCountHolder[0] += pQty;

                Text rowNameText = new Text(pName);
                rowNameText.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
                Text rowPriceSub = new Text("₹" + (int) pPrice + " / unit");
                rowPriceSub.setStyle("-fx-font-size: 11px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");

                VBox rowNameBox = new VBox(2, rowNameText, rowPriceSub);
                rowNameBox.setPrefWidth(220);

                Text rowPriceVal = new Text(String.format("₹%.2f", pPrice));
                rowPriceVal.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + ";");
                VBox rowPriceBox = new VBox(rowPriceVal);
                rowPriceBox.setPrefWidth(120);

                Button rowMinusBtn = new Button("−");
                rowMinusBtn.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-cursor:hand;");
                rowMinusBtn.setOnAction(e -> {
                    nav.changeQuantity(pName, -1);
                    nav.navigateTo(dashboard.BILLING);
                });

                TextField rowQtyField = new TextField(String.valueOf(pQty));
                rowQtyField.setPrefWidth(40);
                Theme.styleTextField(rowQtyField);

                Button rowPlusBtn = new Button("+");
                rowPlusBtn.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-cursor:hand;");
                rowPlusBtn.setOnAction(e -> {
                    nav.addToBasket(pName);
                    nav.navigateTo(dashboard.BILLING);
                });

                HBox rowQtyBox = new HBox(8, rowMinusBtn, rowQtyField, rowPlusBtn);
                rowQtyBox.setPrefWidth(150);
                rowQtyBox.setAlignment(Pos.CENTER_LEFT);

                Text rowTotalText = new Text(String.format("₹%.2f", pTotal));
                rowTotalText.setStyle("-fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");
                VBox rowTotalBox = new VBox(rowTotalText);
                rowTotalBox.setPrefWidth(100);

                Button rowDeleteBtn = new Button("🗑");
                rowDeleteBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:#f87171; -fx-cursor:hand;");
                rowDeleteBtn.setOnAction(e -> {
                    nav.changeQuantity(pName, -pQty);
                    nav.navigateTo(dashboard.BILLING);
                });

                HBox row = new HBox(rowNameBox, rowPriceBox, rowQtyBox, rowTotalBox, rowDeleteBtn);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 0, 8, 0));
                row.setStyle("-fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-width:0 0 1 0;");
                productRows.getChildren().add(row);
            }

            totalItemsVal.setText(String.valueOf(totalItemsCountHolder[0]));
            totalQtyVal.setText(String.valueOf(totalQuantityCountHolder[0]));
            subtotalVal.setText(String.format("₹%.2f", grandTotalAmountHolder[0]));
            grandVal.setText(String.format("₹%.2f", grandTotalAmountHolder[0]));
        };

        populateFromBasket.run();

        // When an APPROVED/ACCEPTED order is selected, load customer & items directly into Billing
        acceptedOrderCombo.setOnAction(e -> {
            com.eudhari.model.OrderModel order = acceptedOrderCombo.getValue();
            if (order != null) {
                // Select matching customer or create customer model if missing
                CustomerModel matchedCust = null;
                for (CustomerModel cm : customerCombo.getItems()) {
                    if ((cm.getId() != null && cm.getId().equalsIgnoreCase(order.getCustomerId()))
                            || (cm.getName() != null && cm.getName().equalsIgnoreCase(order.getCustomerName()))) {
                        matchedCust = cm;
                        break;
                    }
                }
                if (matchedCust != null) {
                    customerCombo.setValue(matchedCust);
                } else {
                    CustomerModel newTempCust = new CustomerModel(
                            order.getCustomerId() != null ? order.getCustomerId() : "CUST001",
                            order.getCustomerName() != null && !order.getCustomerName().isBlank() ? order.getCustomerName() : "Customer",
                            "N/A", "Today", "ACTIVE", 0.0, 0.0
                    );
                    customerCombo.getItems().add(newTempCust);
                    customerCombo.setValue(newTempCust);
                }

                // Render order items directly from Firestore
                productRows.getChildren().clear();
                grandTotalAmountHolder[0] = order.getTotalAmount();
                int totalQty = 0;
                int itemsCount = order.getItems() != null ? order.getItems().size() : 0;

                if (order.getItems() != null) {
                    for (com.eudhari.model.OrderItemModel item : order.getItems()) {
                        totalQty += item.getQuantity();

                        Text rowNameText = new Text(item.getProductName());
                        rowNameText.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
                        Text rowPriceSub = new Text("₹" + String.format("%.2f", item.getPrice()) + " / unit");
                        rowPriceSub.setStyle("-fx-font-size: 11px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");

                        VBox rowNameBox = new VBox(2, rowNameText, rowPriceSub);
                        rowNameBox.setPrefWidth(220);

                        Text rowPriceVal = new Text(String.format("₹%.2f", item.getPrice()));
                        rowPriceVal.setStyle("-fx-fill:" + Theme.TEXT_PRIMARY + ";");
                        VBox rowPriceBox = new VBox(rowPriceVal);
                        rowPriceBox.setPrefWidth(120);

                        Label rowQtyLabel = new Label("x" + item.getQuantity());
                        rowQtyLabel.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold; -fx-font-size:13px;");
                        HBox rowQtyBox = new HBox(rowQtyLabel);
                        rowQtyBox.setPrefWidth(150);
                        rowQtyBox.setAlignment(Pos.CENTER_LEFT);

                        Text rowTotalText = new Text(String.format("₹%.2f", item.getSubtotal()));
                        rowTotalText.setStyle("-fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");
                        VBox rowTotalBox = new VBox(rowTotalText);
                        rowTotalBox.setPrefWidth(100);

                        HBox row = new HBox(rowNameBox, rowPriceBox, rowQtyBox, rowTotalBox);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setPadding(new Insets(8, 0, 8, 0));
                        row.setStyle("-fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-width:0 0 1 0;");
                        productRows.getChildren().add(row);
                    }
                }

                totalItemsVal.setText(String.valueOf(itemsCount));
                totalQtyVal.setText(String.valueOf(totalQty));
                subtotalVal.setText(String.format("₹%.2f", order.getTotalAmount()));
                grandVal.setText(String.format("₹%.2f", order.getTotalAmount()));
            } else {
                populateFromBasket.run();
            }
        });

        // Auto-select target approved order if passed via navigation, or first approved order
        com.eudhari.model.OrderModel preselectedOrder = nav.getSelectedOrderForBilling();
        if (preselectedOrder != null) {
            com.eudhari.model.OrderModel freshOrder = com.eudhari.controller.OrderController.getInstance().getOrderById(preselectedOrder.getOrderId());
            if (freshOrder != null) {
                preselectedOrder = freshOrder;
            }
            boolean exists = false;
            for (com.eudhari.model.OrderModel o : acceptedOrderCombo.getItems()) {
                if (o.getOrderId().equalsIgnoreCase(preselectedOrder.getOrderId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                acceptedOrderCombo.getItems().add(0, preselectedOrder);
            }
            acceptedOrderCombo.setValue(preselectedOrder);
            nav.clearSelectedOrderForBilling();
        }

        Button addMoreBtn = new Button("🛒 Add More Products");
        addMoreBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
        addMoreBtn.setOnAction(e -> nav.navigateTo(dashboard.PRODUCTS));

        // Totals summary
        Text totalItemsLbl = new Text("Total Items");
        totalItemsLbl.setStyle("-fx-fill:" + Theme.TEXT_SECONDARY + ";");
        VBox totalItemsBox = new VBox(4, totalItemsLbl, totalItemsVal);

        Text totalQtyLbl = new Text("Total Quantity");
        totalQtyLbl.setStyle("-fx-fill:" + Theme.TEXT_SECONDARY + ";");
        VBox totalQtyBox = new VBox(4, totalQtyLbl, totalQtyVal);

        HBox summaryLeft = new HBox(40, totalItemsBox, totalQtyBox);
        summaryLeft.setPadding(new Insets(15));
        summaryLeft.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-border-color:" + Theme.BORDER_DARK + ";");

        Text subtotalLbl = new Text("Subtotal");
        subtotalLbl.setStyle("-fx-fill:" + Theme.TEXT_SECONDARY + ";");
        Region subtotalSpacer = new Region();
        HBox.setHgrow(subtotalSpacer, Priority.ALWAYS);
        HBox subtotalRow = new HBox(subtotalLbl, subtotalSpacer, subtotalVal);

        Text grandLbl = new Text("Grand Total");
        grandLbl.setStyle("-fx-font-size: 14px; -fx-font-weight:bold; -fx-fill:" + Theme.SKY_BLUE + ";");
        Region grandSpacer = new Region();
        HBox.setHgrow(grandSpacer, Priority.ALWAYS);
        HBox grandRow = new HBox(grandLbl, grandSpacer, grandVal);

        VBox summaryRight = new VBox(10, subtotalRow, grandRow);
        summaryRight.setPadding(new Insets(15));

        HBox summaryBox = new HBox(30, summaryLeft, summaryRight);
        summaryBox.setAlignment(Pos.CENTER_LEFT);

        VBox productsCard = new VBox(15, productsTitle, productHeader, productRows, addMoreBtn, summaryBox);
        productsCard.setPadding(new Insets(15));
        productsCard.setStyle(Theme.STYLE_CARD);
        productsCard.setMaxWidth(Double.MAX_VALUE);

        // Payment type selection
        Label paymentTitle = new Label("Select Payment Type & Submit");
        paymentTitle.setStyle("-fx-font-size: 14px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        final String[] selectedPaymentMethod = new String[] { "Cash" };

        // Cash option
        VBox cashOption = createPaymentOptionCard("💰", "Cash", "Payment received now");
        // Udhari option
        VBox udhariOption = createPaymentOptionCard("📄", "Udhari (Credit)", "Record as udhari payment");
        // Online option
        VBox onlineOption = createPaymentOptionCard("💳", "Online Payment", "Pay via UPI / Card");

        // Highlight Cash by default
        cashOption.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-border-color:" + Theme.SKY_BLUE
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;");

        Runnable resetPaymentStyles = () -> {
            String defaultStyle = "-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-border-color:"
                    + Theme.BORDER_DARK
                    + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;";
            cashOption.setStyle(defaultStyle);
            udhariOption.setStyle(defaultStyle);
            onlineOption.setStyle(defaultStyle);
        };

        cashOption.setOnMouseClicked(e -> {
            resetPaymentStyles.run();
            cashOption.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-border-color:" + Theme.SKY_BLUE
                    + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;");
            selectedPaymentMethod[0] = "Cash";
        });

        Label udhariLimitWarning = new Label();
        udhariLimitWarning.setStyle("-fx-text-fill:#f87171; -fx-font-size:12px; -fx-font-weight:bold;");
        udhariLimitWarning.setVisible(false);
        udhariLimitWarning.setManaged(false);

        Runnable checkUdhariLimit = () -> {
            CustomerModel customer = customerCombo.getValue();
            if (customer != null && customer.isUdhariLimitReached()) {
                udhariOption.setDisable(true);
                udhariOption.setOpacity(0.4);
                udhariLimitWarning.setText(String.format("⚠️ Customer Udhari limit reached (Max ₹%.2f, Pending ₹%.2f). Udhari payment is unavailable.", customer.getUdhariLimit(), customer.getPendingUdhari()));
                udhariLimitWarning.setVisible(true);
                udhariLimitWarning.setManaged(true);

                if ("Udhari".equalsIgnoreCase(selectedPaymentMethod[0])) {
                    selectedPaymentMethod[0] = "Cash";
                    resetPaymentStyles.run();
                    cashOption.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-border-color:" + Theme.SKY_BLUE
                            + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;");
                }
            } else {
                udhariOption.setDisable(false);
                udhariOption.setOpacity(1.0);
                udhariLimitWarning.setVisible(false);
                udhariLimitWarning.setManaged(false);
            }
        };

        customerCombo.valueProperty().addListener((obs, oldVal, newVal) -> checkUdhariLimit.run());
        checkUdhariLimit.run();

        udhariOption.setOnMouseClicked(e -> {
            if (udhariOption.isDisable()) return;
            resetPaymentStyles.run();
            udhariOption.setStyle(
                    "-fx-background-color:" + Theme.WARM_BEIGE_BG + "; -fx-border-color:" + Theme.WARM_BROWN_BORDER
                            + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;");
            selectedPaymentMethod[0] = "Udhari";
        });

        onlineOption.setOnMouseClicked(e -> {
            resetPaymentStyles.run();
            onlineOption.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-border-color:" + Theme.SKY_BLUE
                    + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15; -fx-cursor:hand;");
            selectedPaymentMethod[0] = "Online Payment";
        });

        HBox paymentOptions = new HBox(15, cashOption, udhariOption, onlineOption);
        paymentOptions.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cashOption, Priority.ALWAYS);
        HBox.setHgrow(udhariOption, Priority.ALWAYS);
        HBox.setHgrow(onlineOption, Priority.ALWAYS);

        // Submit Button
        Button submitBillBtn = new Button("✓ SUBMIT BILL & GENERATE TRANSACTION");
        submitBillBtn.setPrefWidth(450);
        submitBillBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        Text noteText = new Text(
                "ℹ️ Submitting will deduct product stock, log transaction history, and post to Udhari if selected.");
        noteText.setStyle("-fx-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px;");

        submitBillBtn.setOnAction(e -> {
            Map<String, Integer> b = nav.getBasket();
            com.eudhari.model.OrderModel selectedOrder = acceptedOrderCombo.getValue();

            if (!b.isEmpty()) {
                CustomerModel customer = customerCombo.getValue();
                if (customer == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please select or create a customer first!");
                    alert.showAndWait();
                    return;
                }

                if ("Udhari".equalsIgnoreCase(selectedPaymentMethod[0]) && customer.isUdhariLimitReached()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Cannot record Udhari transaction: Customer's pending Udhari balance (₹"
                                    + String.format("%.2f", customer.getPendingUdhari())
                                    + ") has reached/exceeded their maximum allowed limit of ₹"
                                    + String.format("%.2f", customer.getUdhariLimit()) + ".");
                    alert.showAndWait();
                    return;
                }

                String payMethod = selectedPaymentMethod[0];
                double totalAmt = grandTotalAmountHolder[0];

                TransactionModel tx = billingController.processSubmitBill(customer, payMethod, b);

                if (tx != null) {
                    nav.saveCurrentBill();

                    String msg = "Bill #" + tx.getBillId() + " generated successfully for " + customer.getName() + " ["
                            + customer.getId() + "]!\n" +
                            "Total Amount: ₹" + String.format("%.2f", totalAmt) + "\nPayment Method: " + payMethod;
                    if ("Udhari".equalsIgnoreCase(payMethod)) {
                        msg += "\n\nRecord has been automatically posted to Udhari Page & Firestore.";
                    }

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, msg);
                    successAlert.setTitle("Bill Submitted");
                    successAlert.showAndWait();

                    nav.navigateTo(dashboard.BILLING);
                } else {
                    Alert errAlert = new Alert(Alert.AlertType.ERROR, "Failed to process submit bill.");
                    errAlert.showAndWait();
                }
            } else if (selectedOrder != null) {
                String payMethod = selectedPaymentMethod[0];
                if ("Online Payment".equalsIgnoreCase(payMethod)) payMethod = "ONLINE";
                if ("Cash".equalsIgnoreCase(payMethod)) payMethod = "CASH";
                if ("Udhari".equalsIgnoreCase(payMethod)) payMethod = "UDHARI";

                com.eudhari.model.BillingModel billing = billingController.processOrderBilling(selectedOrder.getOrderId(), payMethod);

                if (billing != null) {
                    String msg = "Bill #" + billing.getBillingId() + " generated for Order #" + selectedOrder.getOrderId() +
                            "\nCustomer: " + selectedOrder.getCustomerName() +
                            "\nTotal Amount: ₹" + String.format("%.2f", billing.getTotalAmount()) +
                            "\nPayment Method: " + payMethod + " (" + billing.getPaymentStatus() + ")";
                    if ("UDHARI".equalsIgnoreCase(payMethod)) {
                        msg += "\n\nRecord has been automatically posted to Udhari in Firestore.";
                    }

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, msg);
                    successAlert.setTitle("Order Billed & Completed");
                    successAlert.showAndWait();

                    nav.navigateTo(dashboard.BILLING);
                } else {
                    Alert errAlert = new Alert(Alert.AlertType.ERROR, "Failed to process order billing.");
                    errAlert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Please select products for basket or select an APPROVED Customer Order above before submitting.");
                alert.showAndWait();
            }
        });

        VBox paymentCard = new VBox(15, paymentTitle, udhariLimitWarning, paymentOptions, submitBillBtn, noteText);
        paymentCard.setPadding(new Insets(20));
        paymentCard.setMaxWidth(Double.MAX_VALUE);
        paymentCard.setStyle(Theme.STYLE_CARD);

        VBox centerContent = new VBox(15, customerBox, productsCard, paymentCard);
        centerContent.setPadding(new Insets(15));
        centerContent.setPrefWidth(750);
        centerContent.setMaxWidth(Double.MAX_VALUE);

        // ---------------- RIGHT: BILLING HISTORY ----------------
        Label historyTitle = new Label("Billing History");
        historyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        TextField historySearch = new TextField();
        historySearch.setPromptText("🔍 Search history...");
        Theme.styleTextField(historySearch);
        HBox.setHgrow(historySearch, Priority.ALWAYS);

        HBox historyHeader = new HBox(10, historyTitle, historySearch);
        historyHeader.setAlignment(Pos.CENTER_LEFT);

        ListView<TransactionModel> historyList = new ListView<>();
        historyList.setPrefHeight(500);
        historyList.setStyle(
                "-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-border-color:" + Theme.BORDER_DARK + ";");

        historyList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TransactionModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String idStr = item.getOrderId() != null && !item.getOrderId().isBlank()
                            ? "Order #" + item.getOrderId()
                            : "Bill #" + item.getBillId();

                    Label numLabel = new Label(idStr);
                    numLabel.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold; -fx-font-size:12px;");

                    Label itemsLabel = new Label("Items: " + (item.getItemsSummary() != null && !item.getItemsSummary().isBlank() ? item.getItemsSummary() : "N/A"));
                    itemsLabel.setWrapText(true);
                    itemsLabel.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px;");

                    Label amountLabel = new Label(String.format("Total Amount: ₹%.2f", item.getTotalAmount()));
                    amountLabel.setStyle("-fx-text-fill:" + Theme.SKY_BLUE + "; -fx-font-weight:bold; -fx-font-size:12px;");

                    VBox infoBox = new VBox(3, numLabel, itemsLabel, amountLabel);
                    infoBox.setPrefWidth(270);

                    Button delBtn = new Button("🗑");
                    delBtn.setStyle(
                            "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-font-size:12px; -fx-cursor:hand;");
                    delBtn.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "Delete bill record " + item.getBillId() + "?", ButtonType.YES, ButtonType.NO);
                        confirm.showAndWait().ifPresent(ans -> {
                            if (ans == ButtonType.YES) {
                                billingController.deleteTransaction(item);
                            }
                        });
                    });

                    HBox row = new HBox(8, infoBox, dashboard.spacer(), delBtn);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(6));
                    setGraphic(row);
                }
            }
        });

        Runnable refreshHistory = () -> {
            historyList.getItems().clear();
            String query = historySearch.getText() != null ? historySearch.getText().trim().toLowerCase() : "";
            for (TransactionModel tx : transactionStore.getAllTransactions()) {
                if (query.isEmpty() ||
                        (tx.getBillId() != null && tx.getBillId().toLowerCase().contains(query)) ||
                        (tx.getOrderId() != null && tx.getOrderId().toLowerCase().contains(query)) ||
                        (tx.getItemsSummary() != null && tx.getItemsSummary().toLowerCase().contains(query)) ||
                        String.valueOf(tx.getTotalAmount()).contains(query)) {
                    historyList.getItems().add(tx);
                }
            }
        };

        historySearch.textProperty().addListener((obs, oldV, newV) -> refreshHistory.run());
        transactionStore.getAllTransactions()
                .addListener((ListChangeListener<TransactionModel>) c -> refreshHistory.run());
        transactionStore.loadFromDAO();
        refreshHistory.run();

        Button viewAllBtn = new Button("View Customer Records  →");
        viewAllBtn.setMaxWidth(Double.MAX_VALUE);
        viewAllBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
        viewAllBtn.setOnAction(e -> nav.navigateTo(dashboard.CUSTOMERS));

        VBox rightBox = new VBox(15, historyHeader, historyList, viewAllBtn);
        rightBox.setPadding(new Insets(15));
        rightBox.setPrefWidth(400);
        VBox.setVgrow(historyList, Priority.ALWAYS);
        rightBox.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-width:0 0 0 1;");

        // Combine Center + Right
        HBox mainContent = new HBox(centerContent, rightBox);
        HBox.setHgrow(centerContent, Priority.ALWAYS);
        mainContent.setFillHeight(true);

        ScrollPane contentScrollPane = new ScrollPane(mainContent);
        Theme.applyScrollDarkStyle(contentScrollPane);
        borderpane.setCenter(contentScrollPane);

        return borderpane;
    }

    private static VBox createPaymentOptionCard(String icon, String title, String desc) {
        Text iconText = new Text(icon);
        iconText.setStyle("-fx-font-size: 18px;");
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-weight:bold; -fx-fill:" + Theme.TEXT_PRIMARY + ";");
        Text descText = new Text(desc);
        descText.setStyle("-fx-font-size: 11px; -fx-fill:" + Theme.TEXT_SECONDARY + ";");

        VBox box = new VBox(5, iconText, titleText, descText);
        box.setPadding(new Insets(15));
        box.setMinWidth(0);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color:" + Theme.BG_CARD_ALT + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;");
        return box;
    }

    private static void showAddNewCustomerDialog(CustomerController controller, ComboBox<CustomerModel> combo) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        ButtonType save = new ButtonType("Save Customer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        String autoId = controller.generateUniqueCustomerId();
        Label idLbl = new Label(autoId);
        idLbl.setStyle("-fx-font-weight:bold; -fx-text-fill:" + Theme.SKY_BLUE + ";");

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
        form.addRow(0, new Label("Generated Customer ID:"), idLbl);
        form.addRow(1, new Label("Name:"), name);
        form.addRow(2, new Label("Phone:"), phone);
        form.addRow(3, new Label("Status:"), status);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !name.getText().isBlank()) {
                CustomerModel c = controller.addCustomer(name.getText().trim(), phone.getText().trim(),
                        status.getValue());
                combo.setValue(c);
            }
        });
    }
}
