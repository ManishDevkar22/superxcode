package com.eudhari.view.customer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import java.util.List;
import javafx.stage.Stage;

public class History {

    // MAIN COMPONENTS

    private ScrollPane rootScroll;
    private VBox mainContent;

    private TableView<HistoryModel> historyTable;
    private TextField searchField;
    private ComboBox<String> typeComboBox;
    private ComboBox<String> statusComboBox;
    private ComboBox<String> dateComboBox;

    private Label totalTransactionsLabel;
    private Label totalUdhariLabel;
    private Label totalPaymentsLabel;
    private Label pendingAmountLabel;

    private Label showingLabel;
    private Pagination pagination;

    // NAVIGATION CALLBACKS

    private Runnable goToDashboard;
    private Runnable goToConnectedShops;
    private Runnable goToMyUdhari;
    private Runnable goToPayUdhari;
    private Runnable goToMyOrders;
    private Runnable goToHistory;
    private Runnable goToNotifications;
    private Runnable goToSettings;
    private Runnable goToHelp;

    // DATA

    private final ObservableList<HistoryModel> allHistory = FXCollections.observableArrayList();
    private final ObservableList<HistoryModel> filteredHistory = FXCollections.observableArrayList();

    // THEME COLORS (Matching Homepage.java)

    private final String BG_White = "#c1e1ff";
    private final String CARD_BG = "#0e1726";
    private final String INPUT_BG = "#131e33";
    private final String BORDER_COLOR = "#1e293b";

    private final String ACCENT_BLUE = "#2563eb";
    private final String ACCENT_CYAN = "#38bdf8";

    private final String TEXT_PRIMARY = "#080707";
    private final String TEXT_MUTED = "#94a3b8";

    private final String SUCCESS = "#4ade80";
    private final String WARNING = "#fbbf24";
    private final String ERROR = "#ef4444";

    // CONSTRUCTORS

    public History() {
        this(null);
    }

    public History(Runnable goToDashboard) {
        this.goToDashboard = goToDashboard;

        createSampleData();
        createLayout();
        applyFilters();
    }

    // GET VIEW (Loaded into bp.setCenter(...) in Homepage)

    public Node getView() {
        return rootScroll;
    }

    // CREATE MAIN LAYOUT

    private void createLayout() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24));
        mainContent.setStyle("-fx-background-color: " + BG_White + ";");

        // 1. Header (Title + Back Button + Refresh)
        mainContent.getChildren().add(createPageHeader());

        // 2. Summary Stats Cards
        mainContent.getChildren().add(createSummaryCards());

        // 3. Table Card with Filter Bar & Pagination
        VBox tableCard = createTableCard();
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        mainContent.getChildren().add(tableCard);

        rootScroll = new ScrollPane(mainContent);
        rootScroll.setFitToWidth(true);
        rootScroll.setStyle(
                "-fx-background-color: transparent; -fx-background: " + BG_White + "; -fx-border-color: transparent;");
    }

    // PAGE HEADER

    private HBox createPageHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("📜  Transaction & Udhaari History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label subtitle = new Label("Track all your purchases, payments, credit limits, and settlements in one place.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("↻  Refresh");
        refreshBtn.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + ACCENT_CYAN
                + "; -fx-font-weight: bold; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> {
            loadHistory();
            applyFilters();
        });

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: " + BORDER_COLOR + "; -fx-text-fill: " + TEXT_MUTED
                + "; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (goToDashboard != null) {
                goToDashboard.run();
            }
        });

        header.getChildren().addAll(titleBox, spacer, refreshBtn, backBtn);
        return header;
    }

    // SUMMARY STATS CARDS

    private HBox createSummaryCards() {
        HBox cards = new HBox(15);

        VBox card1 = createStatCard("TOTAL TRANSACTIONS", "1,248", ACCENT_CYAN);
        VBox card2 = createStatCard("TOTAL UDHARI TAKEN", "₹45,200", "#a78bfa");
        VBox card3 = createStatCard("TOTAL PAYMENTS MADE", "₹32,800", SUCCESS);
        VBox card4 = createStatCard("PENDING DUES", "₹12,400", WARNING);

        totalTransactionsLabel = (Label) card1.getChildren().get(1);
        totalUdhariLabel = (Label) card2.getChildren().get(1);
        totalPaymentsLabel = (Label) card3.getChildren().get(1);
        pendingAmountLabel = (Label) card4.getChildren().get(1);

        cards.getChildren().addAll(card1, card2, card3, card4);
        return cards;
    }

    private VBox createStatCard(String label, String value, String colorHex) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";");

        Label v = new Label(value);
        v.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");

        VBox box = new VBox(6, l, v);
        box.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    // TABLE CARD CONTAINER

    private VBox createTableCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-padding: 18px;");

        // Filter bar
        HBox filterBar = createFilterBar();

        // Table
        historyTable = createHistoryTable();
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        // Pagination footer
        HBox footer = createPaginationFooter();

        card.getChildren().addAll(filterBar, historyTable, footer);
        return card;
    }

    // FILTER BAR

    private HBox createFilterBar() {
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("🔍  Search shop, type, or note...");
        searchField.setPrefHeight(38);
        searchField.setStyle("-fx-background-color: " + INPUT_BG
                + "; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 0 12;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        typeComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Type: All", "Udhari", "Payment", "Order", "Refund"));
        typeComboBox.setValue("Type: All");
        styleComboBox(typeComboBox);

        statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Status: All", "Paid", "Pending", "Partially Paid"));
        statusComboBox.setValue("Status: All");
        styleComboBox(statusComboBox);

        dateComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Date: This Month", "Today", "This Week", "All Time"));
        dateComboBox.setValue("Date: This Month");
        styleComboBox(dateComboBox);

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED
                + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            typeComboBox.setValue("Type: All");
            statusComboBox.setValue("Status: All");
            dateComboBox.setValue("Date: This Month");
            applyFilters();
        });

        typeComboBox.setOnAction(e -> applyFilters());
        statusComboBox.setOnAction(e -> applyFilters());
        dateComboBox.setOnAction(e -> applyFilters());

        filterBar.getChildren().addAll(searchField, typeComboBox, statusComboBox, dateComboBox, clearBtn);
        return filterBar;
    }

    private void styleComboBox(ComboBox<String> cb) {
        cb.setPrefHeight(38);
        cb.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-text-fill: white;");
    }

    // HISTORY TABLE

    private TableView<HistoryModel> createHistoryTable() {
        TableView<HistoryModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(380);

        table.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                        "-fx-control-inner-background: " + INPUT_BG + ";" +
                        "-fx-table-cell-border-color: " + BORDER_COLOR + ";" +
                        "-fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: " + BORDER_COLOR
                        + ";");

        // Date Column
        TableColumn<HistoryModel, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> d.getValue().dateProperty());
        dateCol.setPrefWidth(110);

        // Shop Name Column
        TableColumn<HistoryModel, String> shopCol = new TableColumn<>("Shop Name");
        shopCol.setCellValueFactory(d -> d.getValue().shopNameProperty());
        shopCol.setPrefWidth(160);

        // Type Column
        TableColumn<HistoryModel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> d.getValue().typeProperty());
        typeCol.setPrefWidth(90);

        // Description Column
        TableColumn<HistoryModel, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> d.getValue().descriptionProperty());
        descCol.setPrefWidth(160);

        // Amount Column
        TableColumn<HistoryModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(d -> d.getValue().amountProperty());
        amountCol.setPrefWidth(100);

        // Method Column
        TableColumn<HistoryModel, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(d -> d.getValue().methodProperty());
        methodCol.setPrefWidth(90);

        // Status Badge Column
        TableColumn<HistoryModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> d.getValue().statusProperty());
        statusCol.setPrefWidth(110);
        statusCol.setCellFactory(col -> new TableCell<HistoryModel, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    if (status.equalsIgnoreCase("Paid")) {
                        badge.setStyle(
                                "-fx-background-color: #064e3b; -fx-text-fill: #4ade80; -fx-padding: 3 8; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    } else if (status.equalsIgnoreCase("Pending")) {
                        badge.setStyle(
                                "-fx-background-color: #451a03; -fx-text-fill: #fb923c; -fx-padding: 3 8; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    } else {
                        badge.setStyle(
                                "-fx-background-color: #3b0764; -fx-text-fill: #c084fc; -fx-padding: 3 8; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Action Column
        TableColumn<HistoryModel, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(90);
        actionCol.setCellFactory(col -> new TableCell<HistoryModel, Void>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.setStyle("-fx-background-color: " + ACCENT_BLUE
                        + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6px; -fx-cursor: hand;");
                viewBtn.setOnAction(e -> {
                    HistoryModel model = getTableView().getItems().get(getIndex());
                    showTransactionDetails(model);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        table.getColumns().addAll(dateCol, shopCol, typeCol, descCol, amountCol, methodCol, statusCol, actionCol);

        Label placeholder = new Label("No transactions match your search.");
        placeholder.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
        table.setPlaceholder(placeholder);

        return table;
    }

    // PAGINATION FOOTER

    private HBox createPaginationFooter() {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 5, 0, 5));

        showingLabel = new Label("Showing 1 to 5 of 5 entries");
        showingLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        pagination = new Pagination(1, 0);
        pagination.setPageFactory(pageIndex -> new VBox());
        pagination.setStyle("-fx-page-information-visible: false;");

        footer.getChildren().addAll(showingLabel, spacer, pagination);
        return footer;
    }

    // SAMPLE DATA

    private void createSampleData() {
        loadHistory();
    }

    private void loadHistory() {
        allHistory.clear();
        com.eudhari.model.UserModel currentCust = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        List<com.eudhari.model.BillingModel> billingList = com.eudhari.controller.shopkeppercontroller.BillingController.getInstance().getBillingForCustomer(currentCustId);
        if (billingList != null) {
            for (com.eudhari.model.BillingModel b : billingList) {
                String dtStr = b.getCreatedAt() != null && b.getCreatedAt().length() >= 10 ? b.getCreatedAt().substring(0, 10) : "Recent";
                allHistory.add(new HistoryModel(
                        dtStr,
                        b.getShopName(),
                        b.getPaymentMethod(),
                        b.getItemsSummary(),
                        String.format("₹%.2f", b.getTotalAmount()),
                        b.getPaymentMethod(),
                        b.getPaymentStatus()
                ));
            }
        }
    }

    // FILTER LOGIC

    private void applyFilters() {
        if (historyTable == null)
            return;

        String search = searchField == null ? "" : searchField.getText().toLowerCase().trim();
        String type = typeComboBox == null ? "Type: All" : typeComboBox.getValue();
        String status = statusComboBox == null ? "Status: All" : statusComboBox.getValue();

        filteredHistory.clear();

        for (HistoryModel model : allHistory) {
            boolean matchesSearch = search.isEmpty() ||
                    model.getShopName().toLowerCase().contains(search) ||
                    model.getType().toLowerCase().contains(search) ||
                    model.getDescription().toLowerCase().contains(search);

            boolean matchesType = type.equals("Type: All") || model.getType().equalsIgnoreCase(type);
            boolean matchesStatus = status.equals("Status: All") || model.getStatus().equalsIgnoreCase(status);

            if (matchesSearch && matchesType && matchesStatus) {
                filteredHistory.add(model);
            }
        }

        historyTable.setItems(filteredHistory);
        updateSummary();
        updatePagination();
    }

    // UPDATE SUMMARY STATS

    private void updateSummary() {
        int total = filteredHistory.size();
        double totalUdhari = 0;
        double totalPayments = 0;
        double pendingAmount = 0;

        for (HistoryModel model : filteredHistory) {
            double amount = parseAmount(model.getAmount());
            if (model.getType().equalsIgnoreCase("Udhari")) {
                totalUdhari += amount;
            }
            if (model.getType().equalsIgnoreCase("Payment")) {
                totalPayments += amount;
            }
            if (model.getStatus().equalsIgnoreCase("Pending")) {
                pendingAmount += amount;
            }
        }

        if (totalTransactionsLabel != null)
            totalTransactionsLabel.setText(String.valueOf(total));
        if (totalUdhariLabel != null)
            totalUdhariLabel.setText(formatCurrency(totalUdhari));
        if (totalPaymentsLabel != null)
            totalPaymentsLabel.setText(formatCurrency(totalPayments));
        if (pendingAmountLabel != null)
            pendingAmountLabel.setText(formatCurrency(pendingAmount));
    }

    private double parseAmount(String amount) {
        try {
            return Double.parseDouble(amount.replace("₹", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatCurrency(double amount) {
        return String.format("₹%,.0f", amount);
    }

    private void updatePagination() {
        int total = filteredHistory.size();
        int pageSize = 6;
        int pageCount = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (pagination != null) {
            pagination.setPageCount(pageCount);
            pagination.setMaxPageIndicatorCount(5);
        }

        int from = total == 0 ? 0 : 1;
        int to = Math.min(pageSize, total);

        if (showingLabel != null) {
            showingLabel.setText("Showing " + from + " to " + to + " of " + total + " entries");
        }
    }

    // TRANSACTION DETAILS MODAL

    private void showTransactionDetails(HistoryModel model) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Transaction Details - " + model.getShopName());

        VBox box = new VBox(14);
        box.setPadding(new Insets(24));
        box.setPrefWidth(440);
        box.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR + ";");

        Label title = new Label("📄 Transaction Summary");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_CYAN + ";");

        box.getChildren().add(title);
        addDetailRow(box, "Date", model.getDate());
        addDetailRow(box, "Shop Name", model.getShopName());
        addDetailRow(box, "Type", model.getType());
        addDetailRow(box, "Description", model.getDescription());
        addDetailRow(box, "Amount", model.getAmount());
        addDetailRow(box, "Payment Method", model.getMethod());
        addDetailRow(box, "Status", model.getStatus());

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button receiptBtn = new Button("View Receipt");
        receiptBtn.setStyle("-fx-background-color: " + ACCENT_BLUE
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        receiptBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Receipt");
            alert.setHeaderText("Digital e-Receipt");
            alert.setContentText("Transaction for " + model.getShopName() + " (" + model.getAmount() + ") verified.");
            alert.showAndWait();
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-text-fill: " + TEXT_MUTED
                + "; -fx-border-color: " + BORDER_COLOR
                + "; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());

        buttons.getChildren().addAll(receiptBtn, closeBtn);
        box.getChildren().add(buttons);

        Scene scene = new Scene(box);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void addDetailRow(VBox parent, String label, String value) {
        HBox row = new HBox(10);
        Label l = new Label(label + ":");
        l.setPrefWidth(130);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;");

        Label v = new Label(value);
        v.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px;");

        row.getChildren().addAll(l, v);
        parent.getChildren().add(row);
    }

    // NAVIGATION SETTERS

    public void setGoToDashboard(Runnable action) {
        this.goToDashboard = action;
    }

    public void setGoToConnectedShops(Runnable action) {
        this.goToConnectedShops = action;
    }

    public void setGoToMyUdhari(Runnable action) {
        this.goToMyUdhari = action;
    }

    public void setGoToPayUdhari(Runnable action) {
        this.goToPayUdhari = action;
    }

    public void setGoToMyOrders(Runnable action) {
        this.goToMyOrders = action;
    }

    public void setGoToHistory(Runnable action) {
        this.goToHistory = action;
    }

    public void setGoToNotifications(Runnable action) {
        this.goToNotifications = action;
    }

    public void setGoToSettings(Runnable action) {
        this.goToSettings = action;
    }

    public void setGoToHelp(Runnable action) {
        this.goToHelp = action;
    }

    // HISTORY MODEL

    public static class HistoryModel {
        private final SimpleStringProperty date;
        private final SimpleStringProperty shopName;
        private final SimpleStringProperty type;
        private final SimpleStringProperty description;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty method;
        private final SimpleStringProperty status;

        public HistoryModel(String date, String shopName, String type, String description, String amount, String method,
                String status) {
            this.date = new SimpleStringProperty(date);
            this.shopName = new SimpleStringProperty(shopName);
            this.type = new SimpleStringProperty(type);
            this.description = new SimpleStringProperty(description);
            this.amount = new SimpleStringProperty(amount);
            this.method = new SimpleStringProperty(method);
            this.status = new SimpleStringProperty(status);
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public String getShopName() {
            return shopName.get();
        }

        public SimpleStringProperty shopNameProperty() {
            return shopName;
        }

        public String getType() {
            return type.get();
        }

        public SimpleStringProperty typeProperty() {
            return type;
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public String getAmount() {
            return amount.get();
        }

        public SimpleStringProperty amountProperty() {
            return amount;
        }

        public String getMethod() {
            return method.get();
        }

        public SimpleStringProperty methodProperty() {
            return method;
        }

        public String getStatus() {
            return status.get();
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }
    }
}