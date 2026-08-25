package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.ShopModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ShopsManagementView {
    private final AdminController controller;
    private final Runnable onBack;
    private final StackPane rootPane;
    private TableView<ShopModel> table;
    private ObservableList<ShopModel> shopsList;
    private FilteredList<ShopModel> filteredList;
    private VBox drawerPanel;
    private boolean isDrawerOpen = false;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String APP_BG = "#c1e1ff";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String PRIMARY_COLOR = "#3A57E8";
    private static final String PRIMARY_HOVER = "#2D44C2";

    // Form inputs
    private TextField idField;
    private TextField shopNameField;
    private TextField addressField;
    private TextField ownerField;
    private TextField gpayField;
    private ComboBox<String> categoryCombo;
    private ComboBox<String> statusCombo;
    private ShopModel currentEditingShop;
    private boolean isNewShop = false;
    private Label drawerTitle;

    public ShopsManagementView(AdminController controller, Runnable onBack) {
        this.controller = controller;
        this.onBack = onBack;
        this.rootPane = new StackPane();
        initSampleShops();
        buildView();
    }

    private void initSampleShops() {
        com.eudhari.dao.shopkepperdao.FirestoreShopDAO shopDAO = new com.eudhari.dao.shopkepperdao.FirestoreShopDAO();
        java.util.List<ShopModel> realShops = shopDAO.getAllShops();
        shopsList = FXCollections.observableArrayList(realShops != null ? realShops : new java.util.ArrayList<>());
    }

    public Node getView() {
        return rootPane;
    }

    private void buildView() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + APP_BG + ";");

        // Header with title, Back Button and Action Button
        HBox headerRow = new HBox(14);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        if (onBack != null) {
            Button backBtn = createBackButton(onBack);
            headerRow.getChildren().add(backBtn);
        }

        VBox titleBox = new VBox(4);
        Label title = new Label("Shops Management");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("View and manage shop details, category, GPay ID, and business status across the platform.");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = createPrimaryButton("+ Add New Shop");
        addBtn.setOnAction(e -> openDrawer(null));

        headerRow.getChildren().addAll(titleBox, spacer, addBtn);
        mainContent.getChildren().add(headerRow);

        // Filter Bar Card
        VBox filterCard = createCard();
        HBox filterRow = new HBox(16);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox searchCol = new VBox(6);
        Label searchLbl = new Label("SEARCH SHOPS");
        searchLbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        TextField searchField = createSearchField("Search by shop name, ID, owner, or category...");
        searchField.setPrefWidth(340);
        searchCol.getChildren().addAll(searchLbl, searchField);

        VBox statusCol = new VBox(6);
        Label statusLbl = new Label("FILTER BY STATUS");
        statusLbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("All Statuses", "Active", "Pending", "Suspended"));
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle(FONT + "-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
        statusCol.getChildren().addAll(statusLbl, statusFilter);

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        Button refreshBtn = createSecondaryButton("⟳ Refresh");
        refreshBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Statuses");
            updateFilter("", "All Statuses");
        });

        filterRow.getChildren().addAll(searchCol, statusCol, filterSpacer, refreshBtn);
        filterCard.getChildren().add(filterRow);
        mainContent.getChildren().add(filterCard);

        // TableView Card
        VBox tableCard = createCard();
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        table = new TableView<>();
        styleTableView(table);
        table.setPrefHeight(420);

        filteredList = new FilteredList<>(shopsList, s -> true);

        // Required columns: Shop ID, Shop Name, Address, Owner/Shopkeeper, GPay ID, Type/Business Category, Status, Action
        TableColumn<ShopModel, String> colId = new TableColumn<>("SHOP ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShopId()));
        colId.setPrefWidth(90);

        TableColumn<ShopModel, String> colShop = new TableColumn<>("SHOP NAME");
        colShop.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShopName()));
        colShop.setPrefWidth(160);

        TableColumn<ShopModel, String> colAddr = new TableColumn<>("ADDRESS");
        colAddr.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colAddr.setPrefWidth(160);

        TableColumn<ShopModel, String> colOwner = new TableColumn<>("OWNER / SHOPKEEPER");
        colOwner.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOwnerName()));
        colOwner.setPrefWidth(140);

        TableColumn<ShopModel, String> colGpay = new TableColumn<>("GPAY ID");
        colGpay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGpayId()));
        colGpay.setPrefWidth(130);

        TableColumn<ShopModel, String> colCat = new TableColumn<>("TYPE / CATEGORY");
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBusinessCategory()));
        colCat.setPrefWidth(130);

        TableColumn<ShopModel, String> colStatus = new TableColumn<>("STATUS");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<ShopModel, String>() {
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
        colStatus.setPrefWidth(100);

        TableColumn<ShopModel, Void> colAction = new TableColumn<>("ACTION");
        colAction.setCellFactory(col -> new TableCell<ShopModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox btnBox = new HBox(8, editBtn, delBtn);

            {
                editBtn.setStyle(FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");
                delBtn.setStyle(FONT + "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    ShopModel s = getTableView().getItems().get(getIndex());
                    openDrawer(s);
                });

                delBtn.setOnAction(e -> {
                    ShopModel s = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        });
        colAction.setPrefWidth(130);

        table.getColumns().addAll(colId, colShop, colAddr, colOwner, colGpay, colCat, colStatus, colAction);
        table.setItems(filteredList);

        searchField.textProperty().addListener((obs, oldV, newV) -> updateFilter(newV, statusFilter.getValue()));
        statusFilter.valueProperty().addListener((obs, oldV, newV) -> updateFilter(searchField.getText(), newV));

        tableCard.getChildren().add(table);
        mainContent.getChildren().add(tableCard);

        ScrollPane mainScroll = new ScrollPane(mainContent);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");

        buildDrawerPanel();

        rootPane.getChildren().addAll(mainScroll, drawerPanel);
        StackPane.setAlignment(drawerPanel, Pos.TOP_RIGHT);
        drawerPanel.setVisible(false);
    }

    private void updateFilter(String query, String status) {
        filteredList.setPredicate(s -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    s.getShopName().toLowerCase().contains(query.toLowerCase()) ||
                    s.getOwnerName().toLowerCase().contains(query.toLowerCase()) ||
                    s.getShopId().toLowerCase().contains(query.toLowerCase()) ||
                    s.getBusinessCategory().toLowerCase().contains(query.toLowerCase());
            boolean matchesStatus = status == null || status.equals("All Statuses") || s.getStatus().equalsIgnoreCase(status);
            return matchesQuery && matchesStatus;
        });
    }

    private void buildDrawerPanel() {
        drawerPanel = new VBox(16);
        drawerPanel.setPrefWidth(420);
        drawerPanel.setMaxWidth(420);
        drawerPanel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent transparent #CBD5E1; " +
                "-fx-border-width: 0 0 0 1; -fx-padding: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 18, 0, -4, 0);");

        HBox dHeader = new HBox();
        dHeader.setAlignment(Pos.CENTER_LEFT);

        VBox dTitleBox = new VBox(2);
        drawerTitle = new Label("Edit Shop Details");
        drawerTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label dSubtitle = new Label("Update shop information and merchant configuration.");
        dSubtitle.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748B;");
        dTitleBox.getChildren().addAll(drawerTitle, dSubtitle);

        Region dSpacer = new Region();
        HBox.setHgrow(dSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(FONT + "-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #64748B; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> closeDrawer());

        dHeader.getChildren().addAll(dTitleBox, dSpacer, closeBtn);
        drawerPanel.getChildren().add(dHeader);

        VBox idBox = createFieldGroup("SHOP ID", idField = createFormField("", false));
        VBox shopBox = createFieldGroup("SHOP NAME *", shopNameField = createFormField("", true));
        VBox addrBox = createFieldGroup("ADDRESS *", addressField = createFormField("", true));
        VBox ownerBox = createFieldGroup("OWNER / SHOPKEEPER *", ownerField = createFormField("", true));
        VBox gpayBox = createFieldGroup("GPAY ID", gpayField = createFormField("", true));

        categoryCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Grocery Store", "Clothing Shop", "Electronics", "Pharmacy", "Bakery", "Hardware Store", "Books Store", "General Store", "Other"
        ));
        categoryCombo.setStyle(FONT + "-fx-background-color: #F8FAFC; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        VBox catBox = createFieldGroup("BUSINESS CATEGORY", categoryCombo);

        statusCombo = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusCombo.setStyle(FONT + "-fx-background-color: #F8FAFC; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        VBox statusBox = createFieldGroup("STATUS", statusCombo);

        drawerPanel.getChildren().addAll(idBox, shopBox, addrBox, ownerBox, gpayBox, catBox, statusBox);

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        drawerPanel.getChildren().add(bottomSpacer);

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> closeDrawer());

        Button updateBtn = createPrimaryButton("Save");
        updateBtn.setOnAction(e -> saveDrawerData());

        actionRow.getChildren().addAll(cancelBtn, updateBtn);
        drawerPanel.getChildren().add(actionRow);
    }

    private VBox createFieldGroup(String labelText, Node fieldNode) {
        Label lbl = new Label(labelText);
        lbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        return new VBox(5, lbl, fieldNode);
    }

    private void openDrawer(ShopModel model) {
        if (model == null) {
            isNewShop = true;
            currentEditingShop = null;
            drawerTitle.setText("Add New Shop");
            idField.setText("SHP00" + (shopsList.size() + 1));
            shopNameField.clear();
            addressField.clear();
            ownerField.clear();
            gpayField.clear();
            categoryCombo.setValue("Grocery Store");
            statusCombo.setValue("ACTIVE");
        } else {
            isNewShop = false;
            currentEditingShop = model;
            drawerTitle.setText("Edit Shop Details");
            idField.setText(model.getShopId());
            shopNameField.setText(model.getShopName());
            addressField.setText(model.getAddress());
            ownerField.setText(model.getOwnerName());
            gpayField.setText(model.getGpayId());
            categoryCombo.setValue(model.getBusinessCategory());
            statusCombo.setValue(model.getStatus());
        }
        drawerPanel.setVisible(true);
        isDrawerOpen = true;
    }

    private void closeDrawer() {
        drawerPanel.setVisible(false);
        isDrawerOpen = false;
    }

    private void saveDrawerData() {
        if (shopNameField.getText().trim().isEmpty() || ownerField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Shop Name and Owner Name are required!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        com.eudhari.dao.shopkepperdao.FirestoreShopDAO shopDAO = new com.eudhari.dao.shopkepperdao.FirestoreShopDAO();

        if (isNewShop) {
            ShopModel s = new ShopModel(
                    idField.getText(),
                    shopNameField.getText().trim(),
                    addressField.getText().trim(),
                    ownerField.getText().trim(),
                    gpayField.getText().trim(),
                    categoryCombo.getValue(),
                    statusCombo.getValue()
            );
            shopsList.add(s);
            shopDAO.saveShop(s);
        } else if (currentEditingShop != null) {
            currentEditingShop.setShopName(shopNameField.getText().trim());
            currentEditingShop.setAddress(addressField.getText().trim());
            currentEditingShop.setOwnerName(ownerField.getText().trim());
            currentEditingShop.setGpayId(gpayField.getText().trim());
            currentEditingShop.setBusinessCategory(categoryCombo.getValue());
            currentEditingShop.setStatus(statusCombo.getValue());
            shopDAO.updateShop(currentEditingShop);
            table.refresh();
        }

        closeDrawer();
    }

    private void showDeleteConfirmation(ShopModel s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Shop");
        alert.setContentText("Are you sure you want to delete '" + s.getShopName() + "'?");

        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                shopsList.remove(s);
            }
        });
    }

    private VBox createCard(Node... children) {
        VBox card = new VBox(children);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12px; " +
                "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px; -fx-padding: 20px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 3);");
        return card;
    }

    private Button createBackButton(Runnable onBack) {
        Button btn = new Button("← Back");
        String normalStyle = FONT + "-fx-background-color: #FFFFFF; -fx-text-fill: #334155; " +
                "-fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; " +
                "-fx-border-radius: 8px; -fx-padding: 6px 14px; -fx-cursor: hand;";
        String hoverStyle = FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: " + PRIMARY_COLOR + "; " +
                "-fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-border-color: #C7D2FE; " +
                "-fx-border-radius: 8px; -fx-padding: 6px 14px; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        if (onBack != null) {
            btn.setOnAction(e -> onBack.run());
        }
        return btn;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        String normalStyle = FONT + "-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 18px; -fx-cursor: hand;";
        String hoverStyle = FONT + "-fx-background-color: " + PRIMARY_HOVER + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 18px; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        String normalStyle = FONT + "-fx-background-color: #FFFFFF; -fx-text-fill: #334155; " +
                "-fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; " +
                "-fx-border-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;";
        String hoverStyle = FONT + "-fx-background-color: #F1F5F9; -fx-text-fill: #0F172A; " +
                "-fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8px; -fx-border-color: #94A3B8; " +
                "-fx-border-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    private TextField createSearchField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(FONT + "-fx-background-color: #F1F5F9; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 8px; -fx-padding: 8px 14px; -fx-font-size: 12px; -fx-pref-width: 260px; -fx-prompt-text-fill: #94A3B8;");
        tf.focusedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                tf.setStyle(FONT + "-fx-background-color: #FFFFFF; -fx-background-radius: 8px; -fx-border-color: " + PRIMARY_COLOR + "; " +
                        "-fx-border-radius: 8px; -fx-padding: 8px 14px; -fx-font-size: 12px; -fx-pref-width: 260px; -fx-prompt-text-fill: #94A3B8;");
            } else {
                tf.setStyle(FONT + "-fx-background-color: #F1F5F9; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; " +
                        "-fx-border-radius: 8px; -fx-padding: 8px 14px; -fx-font-size: 12px; -fx-pref-width: 260px; -fx-prompt-text-fill: #94A3B8;");
            }
        });
        return tf;
    }

    private TextField createFormField(String text, boolean editable) {
        TextField tf = new TextField(text);
        tf.setEditable(editable);
        String baseBg = editable ? "#F8FAFC" : "#F1F5F9";
        tf.setStyle(FONT + "-fx-background-color: " + baseBg + "; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; " +
                "-fx-border-radius: 6px; -fx-padding: 8px 12px; -fx-font-size: 12px;");
        return tf;
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        String bg = "#F1F5F9";
        String fg = "#475569";
        String t = text.toLowerCase();

        if (t.contains("active") || t.contains("available") || t.contains("completed") || t.contains("settled")) {
            bg = "#DCFCE7";
            fg = "#16A34A";
        } else if (t.contains("pending") || t.contains("review") || t.contains("low stock")) {
            bg = "#FEF3C7";
            fg = "#D97706";
        } else if (t.contains("overdue") || t.contains("out of stock") || t.contains("suspended")) {
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
