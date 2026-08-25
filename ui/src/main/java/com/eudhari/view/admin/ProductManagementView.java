package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.shopkeppermodel.ProductModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ProductManagementView {
    private final AdminController controller;
    private final Runnable onBack;
    private final ScrollPane rootPane;
    private TableView<ProductModel> table;
    private FilteredList<ProductModel> filteredList;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String APP_BG = "#c1e1ff";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String PRIMARY_COLOR = "#3A57E8";
    private static final String PRIMARY_HOVER = "#2D44C2";
    private static final String DANGER_COLOR = "#DC2626";

    // Details panel components
    private VBox detailsCard;
    private VBox detailsPlaceholder;
    private VBox detailsContent;
    private TextField detailNameTf;
    private TextField detailPriceTf;
    private TextField detailStockTf;
    private TextField detailShopTf;
    private ComboBox<String> detailStatusCb;
    private ProductModel selectedProduct;

    public ProductManagementView(AdminController controller, Runnable onBack) {
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
        Label title = new Label("Product Management");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Manage catalog, pricing, and inventory.");
        subtitle.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button filterBtn = createSecondaryButton("⚙ Filter");
        Button addBtn = createPrimaryButton("+ Add Product");
        addBtn.setOnAction(e -> showAddProductDialog());

        HBox btnGroup = new HBox(10, filterBtn, addBtn);
        headerRow.getChildren().addAll(titleBox, spacer, btnGroup);
        mainContent.getChildren().add(headerRow);

        // Search Bar Card
        VBox searchCard = createCard();
        HBox searchRow = new HBox(16);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = createSearchField("Search products, categories, or shopkeepers...");
        searchField.setPrefWidth(350);

        ComboBox<String> catFilter = new ComboBox<>(
                FXCollections.observableArrayList("All Categories", "Groceries", "Pulses", "Beverages"));
        catFilter.setValue("All Categories");
        catFilter.setStyle(FONT
                + "-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-padding: 4px 10px; -fx-font-size: 12px;");

        Region sSpacer = new Region();
        HBox.setHgrow(sSpacer, Priority.ALWAYS);

        Button refreshBtn = createSecondaryButton("⟳ Refresh");
        refreshBtn.setOnAction(e -> {
            searchField.clear();
            catFilter.setValue("All Categories");
            updateFilter("", "All Categories");
        });

        searchRow.getChildren().addAll(searchField, catFilter, sSpacer, refreshBtn);
        searchCard.getChildren().add(searchRow);
        mainContent.getChildren().add(searchCard);

        // Main Layout: Left Table (~65%), Right Product Details Card (~35%)
        HBox contentSplit = new HBox(20);

        // Left Table Card
        VBox tableCard = createCard();
        HBox.setHgrow(tableCard, Priority.ALWAYS);

        table = new TableView<>();
        styleTableView(table);
        table.setPrefHeight(420);

        filteredList = controller.filterProducts("", "All Categories");

        TableColumn<ProductModel, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductId()));
        colId.setPrefWidth(75);

        TableColumn<ProductModel, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        colName.setPrefWidth(160);

        TableColumn<ProductModel, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
        colCat.setPrefWidth(100);

        TableColumn<ProductModel, String> colShop = new TableColumn<>("Shopkeeper");
        colShop.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShopkeeper()));
        colShop.setPrefWidth(160);

        TableColumn<ProductModel, String> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPriceAsString()));
        colPrice.setPrefWidth(90);

        TableColumn<ProductModel, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStock())));
        colStock.setPrefWidth(70);

        TableColumn<ProductModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        colStatus.setCellFactory(col -> new TableCell<ProductModel, String>() {
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
        colStatus.setPrefWidth(105);

        table.getColumns().addAll(colId, colName, colCat, colShop, colPrice, colStock, colStatus);
        table.setItems(filteredList);

        searchField.textProperty().addListener((obs, oldV, newV) -> updateFilter(newV, catFilter.getValue()));
        catFilter.valueProperty().addListener((obs, oldV, newV) -> updateFilter(searchField.getText(), newV));

        tableCard.getChildren().add(table);

        // Right Product Details Panel
        buildDetailsCard();

        contentSplit.getChildren().addAll(tableCard, detailsCard);
        mainContent.getChildren().add(contentSplit);

        // Selection listener for Details Panel
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                showProductDetails(newV);
            } else {
                hideProductDetails();
            }
        });

        ScrollPane sp = new ScrollPane(mainContent);
        sp.setFitToWidth(true);
        sp.setStyle(
                "-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");
        return sp;
    }

    private void updateFilter(String query, String category) {
        filteredList.setPredicate(p -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    p.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                    p.getShopkeeper().toLowerCase().contains(query.toLowerCase()) ||
                    p.getProductId().toLowerCase().contains(query.toLowerCase());
            boolean matchesCat = category == null || category.equals("All Categories")
                    || p.getCategory().equalsIgnoreCase(category);
            return matchesQuery && matchesCat;
        });
    }

    private void buildDetailsCard() {
        detailsCard = createCard();
        detailsCard.setPrefWidth(320);
        detailsCard.setMinWidth(300);

        Label cardTitle = new Label("Product Details");
        cardTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        // Placeholder State
        detailsPlaceholder = new VBox(14);
        detailsPlaceholder.setAlignment(Pos.CENTER);
        detailsPlaceholder.setPadding(new Insets(60, 20, 60, 20));

        Label emptyIcon = new Label("📄");
        emptyIcon.setStyle(FONT + "-fx-font-size: 36px; -fx-text-fill: #CBD5E1;");
        Label emptyText = new Label("Select a product to edit, or click 'Add Product' to create a new entry.");
        emptyText.setWrapText(true);
        emptyText.setAlignment(Pos.CENTER);
        emptyText.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-text-alignment: center;");
        detailsPlaceholder.getChildren().addAll(emptyIcon, emptyText);

        // Details Content State
        detailsContent = new VBox(12);
        detailsContent.setPadding(new Insets(10, 0, 0, 0));

        VBox nameGroup = new VBox(4, new Label("Product Name:") {
            {
                setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            }
        }, detailNameTf = createFormField("", true));
        VBox priceGroup = new VBox(4, new Label("Price:") {
            {
                setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            }
        }, detailPriceTf = createFormField("", true));
        VBox stockGroup = new VBox(4, new Label("Stock Quantity:") {
            {
                setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            }
        }, detailStockTf = createFormField("", true));
        VBox shopGroup = new VBox(4, new Label("Shopkeeper:") {
            {
                setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            }
        }, detailShopTf = createFormField("", true));

        detailStatusCb = new ComboBox<>(FXCollections.observableArrayList("Available", "Low Stock", "Out of Stock"));
        detailStatusCb.setMaxWidth(Double.MAX_VALUE);
        detailStatusCb.setStyle(FONT
                + "-fx-background-color: #F8FAFC; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-padding: 4px 10px; -fx-font-size: 12px;");
        VBox statusGroup = new VBox(4, new Label("Status:") {
            {
                setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            }
        }, detailStatusCb);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10, 0, 0, 0));

        Button deleteBtn = createDangerButton("Delete");
        deleteBtn.setOnAction(e -> {
            if (selectedProduct != null) {
                controller.deleteProduct(selectedProduct);
                hideProductDetails();
            }
        });

        Button saveBtn = createPrimaryButton("Save");
        saveBtn.setOnAction(e -> {
            if (selectedProduct != null) {
                selectedProduct.setProductName(detailNameTf.getText().trim());
                selectedProduct.setPrice(detailPriceTf.getText().trim());
                try {
                    selectedProduct.setStock(Integer.parseInt(detailStockTf.getText().trim()));
                } catch (Exception ignored) {
                }
                selectedProduct.setShopkeeper(detailShopTf.getText().trim());
                selectedProduct.setStatus(detailStatusCb.getValue());
                controller.saveOrUpdateProduct(selectedProduct, false);
                table.refresh();
            }
        });

        btnRow.getChildren().addAll(deleteBtn, saveBtn);
        detailsContent.getChildren().addAll(nameGroup, priceGroup, stockGroup, shopGroup, statusGroup, btnRow);
        detailsContent.setVisible(false);
        detailsContent.setManaged(false);

        detailsCard.getChildren().addAll(cardTitle, detailsPlaceholder, detailsContent);
    }

    private void showProductDetails(ProductModel p) {
        selectedProduct = p;
        detailNameTf.setText(p.getProductName());
        detailPriceTf.setText(p.getPriceAsString());
        detailStockTf.setText(String.valueOf(p.getStock()));
        detailShopTf.setText(p.getShopkeeper());
        detailStatusCb.setValue(p.getStatus());

        detailsPlaceholder.setVisible(false);
        detailsPlaceholder.setManaged(false);
        detailsContent.setVisible(true);
        detailsContent.setManaged(true);
    }

    private void hideProductDetails() {
        selectedProduct = null;
        detailsPlaceholder.setVisible(true);
        detailsPlaceholder.setManaged(true);
        detailsContent.setVisible(false);
        detailsContent.setManaged(false);
    }

    private void showAddProductDialog() {
        Dialog<ProductModel> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.setHeaderText("Add New Product to Inventory Catalog");

        ButtonType addBtnType = new ButtonType("Add Product", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField nameTf = createFormField("", true);
        TextField catTf = createFormField("Groceries", true);
        TextField shopTf = createFormField("", true);
        TextField priceTf = createFormField("₹0.00", true);
        TextField stockTf = createFormField("10", true);
        ComboBox<String> statusCb = new ComboBox<>(
                FXCollections.observableArrayList("Available", "Low Stock", "Out of Stock"));
        statusCb.setValue("Available");

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nameTf, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catTf, 1, 1);
        grid.add(new Label("Shopkeeper:"), 0, 2);
        grid.add(shopTf, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceTf, 1, 3);
        grid.add(new Label("Initial Stock:"), 0, 4);
        grid.add(stockTf, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCb, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addBtnType) {
                String id = "PR00" + (controller.getAllProducts().size() + 1);
                int stock = 0;
                try {
                    stock = Integer.parseInt(stockTf.getText().trim());
                } catch (Exception ignored) {
                }
                return new ProductModel(id, nameTf.getText().trim(), catTf.getText().trim(),
                        shopTf.getText().trim(), priceTf.getText().trim(), stock, statusCb.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            controller.saveOrUpdateProduct(p, true);
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

    private Button createDangerButton(String text) {
        Button btn = new Button(text);
        String normalStyle = FONT + "-fx-background-color: " + DANGER_COLOR + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;";
        String hoverStyle = FONT + "-fx-background-color: #B91C1C; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;";
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
