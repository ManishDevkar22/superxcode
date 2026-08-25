package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.ShopkeeperModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ShopManagementView {
    private final AdminController controller;
    private final Runnable onBack;
    private final StackPane rootPane;
    private TableView<ShopkeeperModel> table;
    private FilteredList<ShopkeeperModel> filteredList;
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
    private TextField ownerNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    private TextField regDateField;
    private ComboBox<String> statusCombo;
    private ShopkeeperModel currentEditingShopkeeper;
    private boolean isNewShopkeeper = false;
    private Label drawerTitle;

    public ShopManagementView(AdminController controller, Runnable onBack) {
        this.controller = controller;
        this.onBack = onBack;
        this.rootPane = new StackPane();
        buildView();
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
        Label title = new Label("Shopkeeper Management");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Manage, filter, and onboard new shopkeepers onto the platform.");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = createPrimaryButton("+ Add Shopkeeper");
        addBtn.setOnAction(e -> openDrawer(null));

        headerRow.getChildren().addAll(titleBox, spacer, addBtn);
        mainContent.getChildren().add(headerRow);

        // Filter Bar Card
        VBox filterCard = createCard();
        HBox filterRow = new HBox(16);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox searchCol = new VBox(6);
        Label searchLbl = new Label("SEARCH SHOPKEEPER");
        searchLbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        TextField searchField = createSearchField("Search by name, ID, or phone...");
        searchField.setPrefWidth(320);
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

        filteredList = controller.filterShopkeepers("", "All Statuses");

        TableColumn<ShopkeeperModel, String> colId = new TableColumn<>("SHOPKEEPER ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShopkeeperId()));
        colId.setPrefWidth(120);

        TableColumn<ShopkeeperModel, String> colOwner = new TableColumn<>("NAME");
        colOwner.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOwnerName()));
        colOwner.setPrefWidth(160);

        TableColumn<ShopkeeperModel, String> colEmail = new TableColumn<>("EMAIL");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colEmail.setPrefWidth(190);

        TableColumn<ShopkeeperModel, String> colPhone = new TableColumn<>("PHONE NUMBER");
        colPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        colPhone.setPrefWidth(140);

        TableColumn<ShopkeeperModel, String> colAddress = new TableColumn<>("ADDRESS");
        colAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colAddress.setPrefWidth(170);

        TableColumn<ShopkeeperModel, String> colDate = new TableColumn<>("REGISTRATION DATE");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegistrationDate()));
        colDate.setPrefWidth(140);

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
        colStatus.setPrefWidth(100);

        TableColumn<ShopkeeperModel, Void> colAction = new TableColumn<>("ACTION");
        colAction.setCellFactory(col -> new TableCell<ShopkeeperModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox btnBox = new HBox(8, editBtn, delBtn);

            {
                editBtn.setStyle(FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");
                delBtn.setStyle(FONT + "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    ShopkeeperModel s = getTableView().getItems().get(getIndex());
                    openDrawer(s);
                });

                delBtn.setOnAction(e -> {
                    ShopkeeperModel s = getTableView().getItems().get(getIndex());
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
        colAction.setPrefWidth(140);

        table.getColumns().addAll(colId, colOwner, colEmail, colPhone, colAddress, colDate, colStatus, colAction);
        table.setItems(filteredList);

        searchField.textProperty().addListener((obs, oldV, newV) -> updateFilter(newV, statusFilter.getValue()));
        statusFilter.valueProperty().addListener((obs, oldV, newV) -> updateFilter(searchField.getText(), newV));

        tableCard.getChildren().add(table);
        mainContent.getChildren().add(tableCard);

        ScrollPane mainScroll = new ScrollPane(mainContent);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");

        // Build Right Slide-Over Drawer
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
                    s.getShopkeeperId().toLowerCase().contains(query.toLowerCase()) ||
                    s.getPhone().contains(query);
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

        // Drawer Header
        HBox dHeader = new HBox();
        dHeader.setAlignment(Pos.CENTER_LEFT);

        VBox dTitleBox = new VBox(2);
        drawerTitle = new Label("Edit Shopkeeper");
        drawerTitle.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label dSubtitle = new Label("Update details for the selected shopkeeper.");
        dSubtitle.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748B;");
        dTitleBox.getChildren().addAll(drawerTitle, dSubtitle);

        Region dSpacer = new Region();
        HBox.setHgrow(dSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(FONT + "-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #64748B; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> closeDrawer());

        dHeader.getChildren().addAll(dTitleBox, dSpacer, closeBtn);
        drawerPanel.getChildren().add(dHeader);

        // Fields
        VBox idBox = createFieldGroup("SHOPKEEPER ID", idField = createFormField("", false));
        VBox shopBox = createFieldGroup("SHOP NAME *", shopNameField = createFormField("", true));
        VBox ownerBox = createFieldGroup("OWNER NAME *", ownerNameField = createFormField("", true));

        HBox emailPhoneRow = new HBox(12);
        VBox emailBox = createFieldGroup("EMAIL", emailField = createFormField("", true));
        VBox phoneBox = createFieldGroup("PHONE *", phoneField = createFormField("", true));
        HBox.setHgrow(emailBox, Priority.ALWAYS);
        HBox.setHgrow(phoneBox, Priority.ALWAYS);
        emailPhoneRow.getChildren().addAll(emailBox, phoneBox);

        VBox addrBox = createFieldGroup("ADDRESS", addressField = createFormField("", true));

        HBox dateStatusRow = new HBox(12);
        VBox dateBox = createFieldGroup("REGISTRATION DATE", regDateField = createFormField("", true));
        statusCombo = new ComboBox<>(FXCollections.observableArrayList("Active", "Pending", "Suspended"));
        statusCombo.setStyle(FONT + "-fx-background-color: #F8FAFC; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        VBox statusBox = createFieldGroup("ACCOUNT STATUS", statusCombo);
        HBox.setHgrow(dateBox, Priority.ALWAYS);
        HBox.setHgrow(statusBox, Priority.ALWAYS);
        dateStatusRow.getChildren().addAll(dateBox, statusBox);

        drawerPanel.getChildren().addAll(idBox, shopBox, ownerBox, emailPhoneRow, addrBox, dateStatusRow);

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        drawerPanel.getChildren().add(bottomSpacer);

        // Action buttons
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = createSecondaryButton("Cancel");
        cancelBtn.setOnAction(e -> closeDrawer());

        Button updateBtn = createPrimaryButton("Update");
        updateBtn.setOnAction(e -> saveDrawerData());

        actionRow.getChildren().addAll(cancelBtn, updateBtn);
        drawerPanel.getChildren().add(actionRow);
    }

    private VBox createFieldGroup(String labelText, Node fieldNode) {
        Label lbl = new Label(labelText);
        lbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        VBox box = new VBox(5, lbl, fieldNode);
        return box;
    }

    private void openDrawer(ShopkeeperModel model) {
        if (model == null) {
            isNewShopkeeper = true;
            currentEditingShopkeeper = null;
            drawerTitle.setText("Add Shopkeeper");
            idField.setText("SH00" + (controller.getAllShopkeepers().size() + 1));
            shopNameField.clear();
            ownerNameField.clear();
            emailField.clear();
            phoneField.clear();
            addressField.clear();
            regDateField.setText("18 Aug 2026");
            statusCombo.setValue("Active");
        } else {
            isNewShopkeeper = false;
            currentEditingShopkeeper = model;
            drawerTitle.setText("Edit Shopkeeper");
            idField.setText(model.getShopkeeperId());
            shopNameField.setText(model.getShopName());
            ownerNameField.setText(model.getOwnerName());
            emailField.setText(model.getEmail());
            phoneField.setText(model.getPhone());
            addressField.setText(model.getAddress());
            regDateField.setText(model.getRegistrationDate());
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
        if (shopNameField.getText().trim().isEmpty() || ownerNameField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Shop Name and Owner Name are required!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (isNewShopkeeper) {
            ShopkeeperModel s = new ShopkeeperModel(
                    idField.getText(),
                    shopNameField.getText().trim(),
                    ownerNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    regDateField.getText().trim(),
                    statusCombo.getValue()
            );
            controller.saveOrUpdateShopkeeper(s, true);
        } else if (currentEditingShopkeeper != null) {
            currentEditingShopkeeper.setShopName(shopNameField.getText().trim());
            currentEditingShopkeeper.setOwnerName(ownerNameField.getText().trim());
            currentEditingShopkeeper.setEmail(emailField.getText().trim());
            currentEditingShopkeeper.setPhone(phoneField.getText().trim());
            currentEditingShopkeeper.setAddress(addressField.getText().trim());
            currentEditingShopkeeper.setRegistrationDate(regDateField.getText().trim());
            currentEditingShopkeeper.setStatus(statusCombo.getValue());
            controller.saveOrUpdateShopkeeper(currentEditingShopkeeper, false);
            table.refresh();
        }

        closeDrawer();
    }

    private void showDeleteConfirmation(ShopkeeperModel s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Shopkeeper");
        alert.setContentText("Are you sure you want to delete '" + s.getShopName() + "'?");

        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                controller.deleteShopkeeper(s);
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

        if (t.contains("active") || t.contains("available") || t.contains("completed") || t.contains("settled") || t.contains("credit") || t.contains("ready")) {
            bg = "#DCFCE7";
            fg = "#16A34A";
        } else if (t.contains("pending") || t.contains("review") || t.contains("low stock")) {
            bg = "#FEF3C7";
            fg = "#D97706";
        } else if (t.contains("overdue") || t.contains("out of stock") || t.contains("suspended") || t.contains("debit")) {
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
