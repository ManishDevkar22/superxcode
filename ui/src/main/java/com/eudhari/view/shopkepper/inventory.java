package com.eudhari.view.shopkepper;

import com.eudhari.controller.shopkeppercontroller.*;
import com.eudhari.model.shopkeppermodel.*;
// import com.eudhari.model.ProductModel;
// import com.eudhari.model.ProductStore;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class inventory {

    public static Parent create(dashboard nav) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + Theme.BG_DARK + ";");

        ProductController controller = ProductController.getInstance();
        ProductStore store = nav.getProductStore();

        Button notification = new Button("🔔 Notifications");
        notification.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-weight:bold; -fx-background-radius:18; -fx-padding:8 14; -fx-border-color:"
                + Theme.SKY_BLUE + "; -fx-border-radius:18; -fx-cursor:hand;");
        notification.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        VBox userText = new VBox(2, new Label("Admin User"), new Label("Store Manager"));
        userText.getChildren().get(0).setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + "; -fx-font-weight:bold;");
        userText.getChildren().get(1).setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px;");

        Label userCircle = new Label("AU");
        userCircle.setStyle("-fx-background-color:" + Theme.SKY_BLUE_DARK
                + "; -fx-text-fill:white; -fx-background-radius:20; -fx-padding:10 12; -fx-cursor:hand; -fx-font-weight:bold;");
        userCircle.setOnMouseClicked(e -> nav.navigateTo(dashboard.PROFILE));

        HBox topGap = new HBox();
        HBox.setHgrow(topGap, Priority.ALWAYS);
        HBox top = new HBox(20, topGap, notification, userText, userCircle);
        top.setPadding(new Insets(16, 30, 14, 30));
        top.setAlignment(Pos.CENTER_RIGHT);
        Theme.applyHeaderStyle(top);
        root.setTop(top);

        String cardStyle = Theme.STYLE_CARD;

        // Statistic Cards
        Label totalIcon = new Label("Box");
        totalIcon.setStyle("-fx-background-color:" + Theme.SKY_BLUE_DARK
                + "; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:11;");
        Label totalProductsVal = new Label("0");
        totalProductsVal.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        totalProductsVal.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label totalProductsLbl = new Label("TOTAL\nPRODUCTS");
        totalProductsLbl
                .setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px; -fx-font-weight:bold;");
        Label activeInStoreLbl = new Label("Active in store");
        activeInStoreLbl.setStyle("-fx-text-fill:#4ade80; -fx-font-size:12px;");

        VBox totalProductsBox = new VBox(8, new HBox(15, totalIcon, totalProductsLbl), totalProductsVal,
                activeInStoreLbl);
        totalProductsBox.setPrefSize(220, 155);
        totalProductsBox.setPadding(new Insets(20));
        totalProductsBox.setStyle(cardStyle);

        Label stockIcon = new Label("Grid");
        stockIcon.setStyle(
                "-fx-background-color:#0d9488; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:11;");
        Label totalStockVal = new Label("0");
        totalStockVal.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        totalStockVal.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label totalWeightLbl = new Label("TOTAL WEIGHT\nSTOCK (KG)");
        totalWeightLbl
                .setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px; -fx-font-weight:bold;");
        Label limitLbl = new Label("Max 500 kg limit");
        limitLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");

        VBox totalStockBox = new VBox(8, new HBox(15, stockIcon, totalWeightLbl), totalStockVal, limitLbl);
        totalStockBox.setPrefSize(220, 155);
        totalStockBox.setPadding(new Insets(20));
        totalStockBox.setStyle(cardStyle);

        Label lowIcon = new Label("!");
        lowIcon.setStyle(
                "-fx-background-color:#451a03; -fx-text-fill:#fbbf24; -fx-background-radius:8; -fx-padding:11 15; -fx-font-weight:bold;");
        Label lowStockVal = new Label("0");
        lowStockVal.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        lowStockVal.setStyle("-fx-text-fill:#fbbf24;");

        Label lowStockLbl = new Label("LOW STOCK\nITEMS");
        lowStockLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px; -fx-font-weight:bold;");
        Label lowSubLbl = new Label("< 10 units remaining");
        lowSubLbl.setStyle("-fx-text-fill:#fbbf24; -fx-font-size:12px;");

        VBox lowStockBox = new VBox(8, new HBox(15, lowIcon, lowStockLbl), lowStockVal, lowSubLbl);
        lowStockBox.setPrefSize(220, 155);
        lowStockBox.setPadding(new Insets(20));
        lowStockBox.setStyle(cardStyle);

        Label outIcon = new Label("X");
        outIcon.setStyle(
                "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-background-radius:8; -fx-padding:11 14; -fx-font-weight:bold;");
        Label outStockVal = new Label("0");
        outStockVal.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        outStockVal.setTextFill(Color.web("#f87171"));

        Label outStockLbl = new Label("OUT OF\nSTOCK");
        outStockLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:11px; -fx-font-weight:bold;");
        Label outSubLbl = new Label("Restock required");
        outSubLbl.setStyle("-fx-text-fill:#f87171; -fx-font-size:12px;");

        VBox outStockBox = new VBox(8, new HBox(15, outIcon, outStockLbl), outStockVal, outSubLbl);
        outStockBox.setPrefSize(220, 155);
        outStockBox.setPadding(new Insets(20));
        outStockBox.setStyle(cardStyle);

        HBox statistics = new HBox(20, totalProductsBox, totalStockBox, lowStockBox, outStockBox);

        // Filter Controls
        TextField productSearch = new TextField();
        productSearch.setPromptText("🔍 Search product...");
        productSearch.setPrefWidth(240);
        Theme.styleTextField(productSearch);

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().add("Category: All");
        for (String c : controller.getAvailableCategories()) {
            if (!categoryCombo.getItems().contains(c)) {
                categoryCombo.getItems().add(c);
            }
        }
        controller.getAvailableCategories().addListener((javafx.collections.ListChangeListener<String>) c -> {
            String current = categoryCombo.getValue();
            categoryCombo.getItems().clear();
            categoryCombo.getItems().add("Category: All");
            for (String customCat : controller.getAvailableCategories()) {
                if (!categoryCombo.getItems().contains(customCat)) {
                    categoryCombo.getItems().add(customCat);
                }
            }
            if (current != null && categoryCombo.getItems().contains(current)) {
                categoryCombo.setValue(current);
            } else {
                categoryCombo.getSelectionModel().selectFirst();
            }
        });
        categoryCombo.getSelectionModel().selectFirst();
        Theme.styleComboBox(categoryCombo);

        ToggleButton activeBtn = new ToggleButton("Active Products");
        activeBtn.setSelected(true);
        activeBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        ToggleButton deletedBtn = new ToggleButton("Deleted / Bin");
        deletedBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);

        ToggleGroup viewGroup = new ToggleGroup();
        activeBtn.setToggleGroup(viewGroup);
        deletedBtn.setToggleGroup(viewGroup);

        Button addStockBtn = new Button("+ Add Stock");
        addStockBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        Button addProductBtn = new Button("+ Add New Product");
        addProductBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        HBox filterGap = new HBox();
        HBox.setHgrow(filterGap, Priority.ALWAYS);
        HBox filters = new HBox(14, productSearch, categoryCombo, activeBtn, deletedBtn, filterGap, addStockBtn,
                addProductBtn);
        filters.setAlignment(Pos.CENTER_LEFT);

        // TableView connected to ProductStore
        TableView<ProductModel> productTable = new TableView<>();
        productTable.setPrefHeight(320);
        productTable.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:14;");

        TableColumn<ProductModel, String> nameCol = new TableColumn<>("PRODUCT");
        nameCol.setPrefWidth(220);
        nameCol.setCellValueFactory(
                d -> new ReadOnlyStringWrapper(d.getValue().getName() + "\nID: " + d.getValue().getId()));

        TableColumn<ProductModel, String> categoryCol = new TableColumn<>("CATEGORY");
        categoryCol.setPrefWidth(140);
        categoryCol.setCellValueFactory(d -> d.getValue().categoryProperty());

        TableColumn<ProductModel, String> stockCol = new TableColumn<>("STOCK QUANTITY");
        stockCol.setPrefWidth(150);
        stockCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFormattedStock()));

        TableColumn<ProductModel, String> priceCol = new TableColumn<>("PRICE");
        priceCol.setPrefWidth(140);
        priceCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFormattedPrice()));

        TableColumn<ProductModel, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setPrefWidth(130);
        statusCol.setCellValueFactory(d -> {
            if (d.getValue().isDeleted())
                return new ReadOnlyStringWrapper("Deleted");
            if (d.getValue().getStock() <= 0)
                return new ReadOnlyStringWrapper("Out of Stock");
            if (d.getValue().getStock() < 10)
                return new ReadOnlyStringWrapper("Low Stock");
            return new ReadOnlyStringWrapper("In Stock");
        });

        TableColumn<ProductModel, Void> actionCol = new TableColumn<>("ACTIONS");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final Button restoreBtn = new Button("Restore");

            {
                editBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + ";-fx-text-fill:" + Theme.SKY_BLUE
                        + ";-fx-font-weight:bold;-fx-cursor:hand;");
                delBtn.setStyle(
                        "-fx-background-color:#3f1414;-fx-text-fill:#f87171;-fx-font-weight:bold;-fx-cursor:hand;");
                restoreBtn.setStyle(
                        "-fx-background-color:#14382c;-fx-text-fill:#4ade80;-fx-font-weight:bold;-fx-cursor:hand;");

                editBtn.setOnAction(e -> {
                    ProductModel item = getTableView().getItems().get(getIndex());
                    showEditDialog(controller, item);
                });
                delBtn.setOnAction(e -> {
                    ProductModel item = getTableView().getItems().get(getIndex());
                    controller.deleteProduct(item);
                });
                restoreBtn.setOnAction(e -> {
                    ProductModel item = getTableView().getItems().get(getIndex());
                    controller.restoreProduct(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProductModel model = getTableView().getItems().get(getIndex());
                    if (model.isDeleted()) {
                        setGraphic(new HBox(8, restoreBtn));
                    } else {
                        setGraphic(new HBox(8, editBtn, delBtn));
                    }
                }
            }
        });

        productTable.getColumns().addAll(nameCol, categoryCol, stockCol, priceCol, statusCol, actionCol);

        FilteredList<ProductModel> tableData = new FilteredList<>(controller.getAllProducts(), p -> !p.isDeleted());
        productTable.setItems(tableData);

        // Filter and view listener
        Runnable updateTableFilter = () -> {
            boolean showDeleted = deletedBtn.isSelected();
            String cat = categoryCombo.getValue();
            String query = productSearch.getText() != null ? productSearch.getText().toLowerCase() : "";

            tableData.setPredicate(p -> {
                if (showDeleted != p.isDeleted())
                    return false;
                if (cat != null && !cat.contains("All") && !p.getCategory().equalsIgnoreCase(cat))
                    return false;
                if (!query.isBlank() && !p.getName().toLowerCase().contains(query))
                    return false;
                return true;
            });
        };

        activeBtn.setOnAction(e -> {
            activeBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            deletedBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
            updateTableFilter.run();
        });
        deletedBtn.setOnAction(e -> {
            deletedBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
            activeBtn.setStyle(Theme.STYLE_BUTTON_SECONDARY);
            updateTableFilter.run();
        });
        categoryCombo.setOnAction(e -> updateTableFilter.run());
        productSearch.textProperty().addListener((obs, oldVal, newVal) -> updateTableFilter.run());

        // Dynamic Stats & Meter Refresh
        Label percentLabel = new Label("0%\nUSED");
        percentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        percentLabel.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        percentLabel.setAlignment(Pos.CENTER);

        Label capacityDetail = new Label("Total Weight: 0 / 500 kg");
        capacityDetail.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        capacityDetail.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        ProgressBar progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(220);

        Runnable refreshStats = () -> {
            int activeCount = controller.getActiveProducts().size();
            double totalKg = controller.getTotalStockKg();
            double pct = controller.getStorageUsagePercentage();

            long lowCount = controller.getActiveProducts().stream().filter(p -> p.getStock() > 0 && p.getStock() < 10)
                    .count();
            long outCount = controller.getActiveProducts().stream().filter(p -> p.getStock() <= 0).count();

            totalProductsVal.setText(String.valueOf(activeCount));
            totalStockVal.setText(String.format("%.1f", totalKg));
            lowStockVal.setText(String.valueOf(lowCount));
            outStockVal.setText(String.valueOf(outCount));

            percentLabel.setText(String.format("%.0f%%\nUSED", pct));
            capacityDetail.setText(String.format("Total Weight: %.1f / 500 kg", totalKg));
            progressBar.setProgress(pct / 100.0);
            if (pct > 80) {
                progressBar.setStyle("-fx-accent:#f87171;");
            } else {
                progressBar.setStyle("-fx-accent:" + Theme.SKY_BLUE + ";");
            }
        };

        controller.getAllProducts().addListener((ListChangeListener<ProductModel>) c -> {
            updateTableFilter.run();
            refreshStats.run();
        });

        refreshStats.run();

        // Storage Capacity Card
        Label capacityTitle = new Label("Storage Capacity");
        capacityTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        capacityTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        StackPane meter = new StackPane();
        Circle outside = new Circle(75, Color.web(Theme.BORDER_DARK));
        Circle inside = new Circle(60, Color.web(Theme.BG_CARD));
        meter.getChildren().addAll(outside, inside, percentLabel);

        Label capacityNote = new Label(
                "Maximum storage capacity = 500 kg.\nStock changes dynamically update progress.");
        capacityNote.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-background-radius:9; -fx-padding:10; -fx-font-size:11px;");

        VBox capacityCard = new VBox(12, capacityTitle, capacityDetail, progressBar, meter, capacityNote);
        capacityCard.setPadding(new Insets(22));
        capacityCard.setPrefWidth(280);
        capacityCard.setAlignment(Pos.TOP_CENTER);
        capacityCard.setStyle(cardStyle);

        // Recent Updates Panel
        Label updateTitle = new Label("Recent Stock Activity");
        updateTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        updateTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label u1 = new Label("• +50 kg Premium Basmati Rice received (Supplier A) - Today 10:30 AM");
        u1.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label u2 = new Label("• -5 kg Sugar sold in Bill #1004 - Today 11:15 AM");
        u2.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label u3 = new Label("• +24 pkts Milk added to dairy shelf - Today 08:00 AM");
        u3.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label u4 = new Label("• Storage level updated dynamically across Product & Inventory views");
        u4.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        VBox updates = new VBox(14, updateTitle, u1, u2, u3, u4);
        updates.setPadding(new Insets(22));
        updates.setStyle(cardStyle);
        HBox.setHgrow(updates, Priority.ALWAYS);

        HBox lower = new HBox(22, updates, capacityCard);

        addProductBtn.setOnAction(e -> showAddProductDialog(controller));
        addStockBtn.setOnAction(e -> showAddStockDialog(controller));

        VBox page = new VBox(22, statistics, filters, productTable, lower);
        page.setPadding(new Insets(26, 30, 30, 30));

        ScrollPane scroll = new ScrollPane(page);
        Theme.applyScrollDarkStyle(scroll);
        root.setCenter(scroll);

        return root;
    }

    private static void showAddProductDialog(ProductController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        ButtonType save = new ButtonType("Add Product", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField();
        ComboBox<String> category = new ComboBox<>();
        for (String c : controller.getAvailableCategories()) {
            if (!category.getItems().contains(c)) {
                category.getItems().add(c);
            }
        }
        category.getItems().add("Other");
        category.getSelectionModel().selectFirst();

        TextField newCategoryInput = new TextField();
        newCategoryInput.setPromptText("Enter new category name...");
        newCategoryInput.setVisible(false);
        newCategoryInput.setManaged(false);

        category.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isOther = "Other".equalsIgnoreCase(newVal);
            newCategoryInput.setVisible(isOther);
            newCategoryInput.setManaged(isOther);
        });

        VBox categoryBox = new VBox(6, category, newCategoryInput);

        TextField price = new TextField();
        ComboBox<String> unit = new ComboBox<>();
        unit.getItems().addAll("kg", "pkt", "bottle", "pack", "L");
        unit.getSelectionModel().selectFirst();
        TextField stock = new TextField();
        TextField image = new TextField();

        ComboBox<ProductImageOption> imageCombo = new ComboBox<>();
        imageCombo.getItems().addAll(ProductImageOption.getPresets());
        imageCombo.getSelectionModel().selectFirst();
        Theme.styleComboBox(imageCombo);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(16);
        form.setPadding(new Insets(22));
        form.setPrefHeight(490);
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Category:"), categoryBox);
        form.addRow(2, new Label("Price:"), price);
        form.addRow(3, new Label("Unit:"), unit);
        form.addRow(4, new Label("Stock Qty:"), stock);
        form.addRow(5, new Label("Image Code:"), image);
        form.addRow(6, new Label("Product Image:"), imageCombo);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefHeight(510);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !name.getText().isBlank()) {
                try {
                    double p = Double.parseDouble(price.getText().trim());
                    double s = Double.parseDouble(stock.getText().trim());
                    String img = image.getText().isBlank()
                            ? name.getText().substring(0, Math.min(4, name.getText().length())).toUpperCase()
                            : image.getText().trim();
                    ProductImageOption sel = imageCombo.getValue();
                    String imgUrl = sel != null ? sel.getUrl() : "";

                    String selectedCat = category.getValue();
                    if ("Other".equalsIgnoreCase(selectedCat)) {
                        String typedCat = newCategoryInput.getText().trim();
                        if (!typedCat.isEmpty()) {
                            selectedCat = typedCat;
                            controller.addCustomCategory(typedCat);
                        }
                    }

                    controller.addProduct(name.getText().trim(), selectedCat, p, unit.getValue(), s, img,
                            imgUrl);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private static void showAddStockDialog(ProductController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Stock Quantity");
        ButtonType save = new ButtonType("Update Stock", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        ComboBox<ProductModel> productCombo = new ComboBox<>(controller.getActiveProducts());
        productCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProductModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : item.getName() + " (Current Stock: " + item.getFormattedStock() + ")");
            }
        });
        productCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : item.getName() + " (Current Stock: " + item.getFormattedStock() + ")");
            }
        });
        if (!controller.getActiveProducts().isEmpty())
            productCombo.getSelectionModel().selectFirst();

        TextField addQtyField = new TextField();
        addQtyField.setPromptText("Quantity to add (e.g. 10)");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.addRow(0, new Label("Select Product:"), productCombo);
        form.addRow(1, new Label("Quantity to Add:"), addQtyField);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && productCombo.getValue() != null) {
                try {
                    double added = Double.parseDouble(addQtyField.getText().trim());
                    ProductModel p = productCombo.getValue();
                    controller.addStock(p, added);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private static void showEditDialog(ProductController controller, ProductModel p) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product - " + p.getName());
        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField(p.getName());
        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("Grocery", "Dairy", "Bakery", "Other");
        category.setValue(p.getCategory());
        TextField price = new TextField(String.valueOf(p.getPrice()));
        ComboBox<String> unit = new ComboBox<>();
        unit.getItems().addAll("kg", "pkt", "bottle", "pack", "L");
        unit.setValue(p.getUnit());
        TextField stock = new TextField(String.valueOf(p.getStock()));
        TextField image = new TextField(p.getImage());

        ComboBox<ProductImageOption> imageCombo = new ComboBox<>();
        imageCombo.getItems().addAll(ProductImageOption.getPresets());
        Theme.styleComboBox(imageCombo);

        String currentUrl = p.getImageUrl();
        ProductImageOption selectedOption = null;
        if (currentUrl != null && !currentUrl.isBlank()) {
            for (ProductImageOption opt : imageCombo.getItems()) {
                if (currentUrl.equalsIgnoreCase(opt.getUrl())) {
                    selectedOption = opt;
                    break;
                }
            }
            if (selectedOption == null) {
                selectedOption = new ProductImageOption("Custom URL (" + currentUrl + ")", currentUrl);
                imageCombo.getItems().add(selectedOption);
            }
            imageCombo.setValue(selectedOption);
        } else {
            imageCombo.getSelectionModel().selectFirst();
        }

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Category:"), category);
        form.addRow(2, new Label("Price:"), price);
        form.addRow(3, new Label("Unit:"), unit);
        form.addRow(4, new Label("Stock Qty:"), stock);
        form.addRow(5, new Label("Image Code:"), image);
        form.addRow(6, new Label("Product Image:"), imageCombo);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == save && !name.getText().isBlank()) {
                try {
                    double pr = Double.parseDouble(price.getText().trim());
                    double st = Double.parseDouble(stock.getText().trim());
                    ProductImageOption sel = imageCombo.getValue();
                    String imgUrl = sel != null ? sel.getUrl() : "";
                    controller.updateProduct(p, name.getText().trim(), category.getValue(), pr, unit.getValue(), st,
                            image.getText().trim(), imgUrl);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
