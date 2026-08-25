package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.shopkeppermodel.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerManagementView {
    private final AdminController controller;
    private final Runnable onBack;
    private final ScrollPane rootPane;
    private TableView<CustomerModel> table;
    private FilteredList<CustomerModel> filteredList;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String APP_BG = "#c1e1ff";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String PRIMARY_COLOR = "#3A57E8";
    private static final String PRIMARY_HOVER = "#2D44C2";

    public CustomerManagementView(AdminController controller, Runnable onBack) {
        this.controller = controller;
        this.onBack = onBack;
        this.rootPane = buildView();
    }

    public Node getView() {
        return rootPane;
    }

    private ScrollPane buildView() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));
        mainContent.setStyle("-fx-background-color: " + APP_BG + ";");

        // Header
        HBox headerRow = new HBox(14);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        if (onBack != null) {
            Button backBtn = createBackButton(onBack);
            headerRow.getChildren().add(backBtn);
        }

        VBox titleBox = new VBox(4);
        Label title = new Label("Customer Management");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Manage platform customers, credit limits, and udhari balances.");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = createPrimaryButton("+ Add Customer");
        addBtn.setOnAction(e -> showCustomerDialog(null));

        headerRow.getChildren().addAll(titleBox, spacer, addBtn);
        mainContent.getChildren().add(headerRow);

        // Filter Bar Card
        VBox filterCard = createCard();
        HBox filterRow = new HBox(16);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox searchCol = new VBox(6);
        Label searchLbl = new Label("SEARCH CUSTOMER");
        searchLbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        TextField searchField = createSearchField("Name, Email, or Phone...");
        searchField.setPrefWidth(300);
        searchCol.getChildren().addAll(searchLbl, searchField);

        VBox statusCol = new VBox(6);
        Label statusLbl = new Label("FILTER BY STATUS");
        statusLbl.setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        ComboBox<String> statusFilter = new ComboBox<>(
                FXCollections.observableArrayList("All Statuses", "Active", "Pending", "Overdue"));
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle(FONT
                + "-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
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
        table.setPrefHeight(440);

        com.eudhari.dao.FirestoreUserDAO userDAO = new com.eudhari.dao.FirestoreUserDAO();
        java.util.List<com.eudhari.model.UserModel> realCustUsers = userDAO.getUsersByRole("customer");

        ObservableList<CustomerModel> custObsList = FXCollections.observableArrayList();
        if (realCustUsers != null) {
            for (com.eudhari.model.UserModel u : realCustUsers) {
                String uCode = u.getUserCode() != null && !u.getUserCode().isBlank() ? u.getUserCode() : u.getUid();
                String dtStr = u.getCreatedAt() != null && u.getCreatedAt().length() >= 10 ? u.getCreatedAt().substring(0, 10) : "Recent";
                CustomerModel cm = new CustomerModel(uCode, u.getName(), u.getEmail(), u.getPhone(), "Connected Shop", "₹0", dtStr, u.getStatus());
                cm.setUid(u.getUid());
                custObsList.add(cm);
            }
        }

        filteredList = new FilteredList<>(custObsList, c -> true);

        TableColumn<CustomerModel, String> colId = new TableColumn<>("CUSTOMER ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerId()));
        colId.setPrefWidth(110);

        TableColumn<CustomerModel, String> colName = new TableColumn<>("CUSTOMER NAME");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colName.setPrefWidth(160);

        TableColumn<CustomerModel, String> colEmail = new TableColumn<>("EMAIL");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colEmail.setPrefWidth(180);

        TableColumn<CustomerModel, String> colPhone = new TableColumn<>("PHONE");
        colPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        colPhone.setPrefWidth(140);

        TableColumn<CustomerModel, String> colShop = new TableColumn<>("CONNECTED SHOP");
        colShop.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getConnectedShop()));
        colShop.setPrefWidth(160);

        TableColumn<CustomerModel, String> colBal = new TableColumn<>("UDHARI BALANCE");
        colBal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUdhariBalance()));
        colBal.setPrefWidth(130);

        TableColumn<CustomerModel, String> colDate = new TableColumn<>("REGISTRATION DATE");
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegistrationDate()));
        colDate.setPrefWidth(130);

        TableColumn<CustomerModel, String> colStatus = new TableColumn<>("STATUS");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<CustomerModel, String>() {
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

        TableColumn<CustomerModel, Void> colAction = new TableColumn<>("ACTION");
        colAction.setCellFactory(col -> new TableCell<CustomerModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox btnBox = new HBox(8, editBtn, delBtn);

            {
                editBtn.setStyle(FONT
                        + "-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");
                delBtn.setStyle(FONT
                        + "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 4px 10px; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    CustomerModel c = getTableView().getItems().get(getIndex());
                    showCustomerDialog(c);
                });

                delBtn.setOnAction(e -> {
                    CustomerModel c = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(c);
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

        table.getColumns().addAll(colId, colName, colEmail, colPhone, colShop, colBal, colDate, colStatus, colAction);
        table.setItems(filteredList);

        searchField.textProperty().addListener((obs, oldV, newV) -> updateFilter(newV, statusFilter.getValue()));
        statusFilter.valueProperty().addListener((obs, oldV, newV) -> updateFilter(searchField.getText(), newV));

        tableCard.getChildren().add(table);
        mainContent.getChildren().add(tableCard);

        ScrollPane sp = new ScrollPane(mainContent);
        sp.setFitToWidth(true);
        sp.setStyle(
                "-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");
        return sp;
    }

    private void updateFilter(String query, String status) {
        filteredList.setPredicate(c -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    c.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                    c.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    c.getCustomerId().toLowerCase().contains(query.toLowerCase()) ||
                    c.getPhone().contains(query);
            boolean matchesStatus = status == null || status.equals("All Statuses")
                    || c.getStatus().equalsIgnoreCase(status);
            return matchesQuery && matchesStatus;
        });
    }

    private void showCustomerDialog(CustomerModel customer) {
        boolean isNew = (customer == null);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isNew ? "Add Customer" : "Edit Customer Status");
        dialog.setHeaderText(isNew ? "Add New Customer to Smart eUdhari" : "Update Customer Account Status");

        ButtonType saveBtnType = new ButtonType(isNew ? "Add" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField nameTf = createFormField(isNew ? "" : customer.getCustomerName(), true);
        TextField emailTf = createFormField(isNew ? "" : customer.getEmail(), true);
        TextField phoneTf = createFormField(isNew ? "" : customer.getPhone(), true);
        ComboBox<String> statusCb = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusCb.setValue(isNew ? "ACTIVE" : customer.getStatus());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameTf, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailTf, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneTf, 1, 2);
        grid.add(new Label("Account Status:"), 0, 3);
        grid.add(statusCb, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == saveBtnType && customer != null) {
                customer.setStatus(statusCb.getValue());
                if (customer.getUid() != null && !customer.getUid().isBlank()) {
                    new com.eudhari.dao.FirestoreUserDAO().updateUserStatus(customer.getUid(), statusCb.getValue());
                }
                table.refresh();
            }
        });
    }

    private void showDeleteConfirmation(CustomerModel c) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Customer");
        alert.setContentText("Are you sure you want to delete '" + c.getCustomerName() + "'?");

        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                controller.deleteCustomer(c);
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
                tf.setStyle(FONT + "-fx-background-color: #FFFFFF; -fx-background-radius: 8px; -fx-border-color: "
                        + PRIMARY_COLOR + "; " +
                        "-fx-border-radius: 8px; -fx-padding: 8px 14px; -fx-font-size: 12px; -fx-pref-width: 260px; -fx-prompt-text-fill: #94A3B8;");
            } else {
                tf.setStyle(FONT
                        + "-fx-background-color: #F1F5F9; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; " +
                        "-fx-border-radius: 8px; -fx-padding: 8px 14px; -fx-font-size: 12px; -fx-pref-width: 260px; -fx-prompt-text-fill: #94A3B8;");
            }
        });
        return tf;
    }

    private TextField createFormField(String text, boolean editable) {
        TextField tf = new TextField(text);
        tf.setEditable(editable);
        String baseBg = editable ? "#F8FAFC" : "#F1F5F9";
        tf.setStyle(
                FONT + "-fx-background-color: " + baseBg + "; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; " +
                        "-fx-border-radius: 6px; -fx-padding: 8px 12px; -fx-font-size: 12px;");
        return tf;
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
