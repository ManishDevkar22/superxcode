package com.eudhari.view.shopkepper;

import com.eudhari.controller.shopkeppercontroller.*;
import com.eudhari.model.shopkeppermodel.*;
// import com.eudhari.model.ProductModel;
// import com.eudhari.model.ProductStore;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

public class Product {

    public static Parent create(dashboard nav) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + Theme.BG_DARK + ";");

        // Top Header
        VBox pageTitle = new VBox(3);
        Label productsTitle = new Label("Products");
        productsTitle.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label subTitle = new Label("Manage store products, stock levels, and pricing");
        subTitle.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");
        pageTitle.getChildren().addAll(productsTitle, subTitle);

        TextField search = new TextField();
        search.setPromptText("🔍 Search products by name...");
        search.setPrefWidth(300);
        Theme.styleTextField(search);

        Button bell = new Button("🔔 Notifications");
        bell.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-weight:bold; -fx-background-radius:18; -fx-padding:8 14; -fx-border-color:"
                + Theme.SKY_BLUE + "; -fx-border-radius:18; -fx-cursor:hand;");
        bell.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        com.eudhari.model.UserModel pCurUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String pShopName = pCurUser != null && pCurUser.getShopName() != null && !pCurUser.getShopName().isBlank() ? pCurUser.getShopName() : (pCurUser != null && pCurUser.getName() != null ? pCurUser.getName() + "'s Store" : "Store");
        String pInit = pShopName.length() >= 2 ? pShopName.substring(0, 2).toUpperCase() : "ST";
        Label owner = new Label(pInit + "   " + pShopName + "   v");
        owner.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-background-radius:20; -fx-padding:8 14; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:20; -fx-cursor:hand;");
        owner.setOnMouseClicked(e -> nav.navigateTo(dashboard.PROFILE));

        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox(Theme.SKY_BLUE, "-fx-background-color:" + Theme.BG_CARD + "; -fx-padding:6 12; -fx-background-radius:18; -fx-border-color:" + Theme.BORDER_DARK + "; -fx-border-radius:18;");

        HBox headerGap = new HBox();
        HBox.setHgrow(headerGap, Priority.ALWAYS);
        HBox header = new HBox(15, pageTitle, headerGap, clockWidget, bell, owner);
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        Theme.applyHeaderStyle(header);
        root.setTop(header);

        ProductController controller = ProductController.getInstance();
        ProductStore store = nav.getProductStore();

        // Category Filter Buttons
        Button allCat = new Button("All Categories");
        allCat.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        Button addProduct = new Button("+  Add Product");
        addProduct.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        Button viewDeletedBtn = new Button("🗑 Deleted Products");
        viewDeletedBtn.setStyle(
                "-fx-background-color:#3f1414; -fx-text-fill:#f87171; -fx-border-color:#b91c1c; -fx-border-radius:8; -fx-background-radius:8; -fx-font-weight:bold; -fx-padding:8 14; -fx-cursor:hand;");

        HBox dynamicCatBox = new HBox(12);

        HBox categoryGap = new HBox();
        HBox.setHgrow(categoryGap, Priority.ALWAYS);
        HBox categories = new HBox(12, allCat, dynamicCatBox, categoryGap, viewDeletedBtn, addProduct);
        categories.setAlignment(Pos.CENTER_LEFT);

        Label listTitle = new Label("Product Catalog");
        listTitle.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Sort: Name (A-Z)", "Sort: Price (Low-High)", "Sort: Stock Quantity");
        sortCombo.getSelectionModel().selectFirst();
        Theme.styleComboBox(sortCombo);

        HBox filterGap = new HBox();
        HBox.setHgrow(filterGap, Priority.ALWAYS);
        HBox filters = new HBox(16, listTitle, filterGap, sortCombo);
        filters.setAlignment(Pos.CENTER_LEFT);

        // Dynamic Product Grid Container
        FlowPane gridPane = new FlowPane();
        gridPane.setHgap(16);
        gridPane.setVgap(16);
        gridPane.setPadding(new Insets(10, 0, 10, 0));

        FilteredList<ProductModel> filteredProducts = new FilteredList<>(controller.getActiveProducts(), p -> true);

        Runnable refreshGrid = () -> {
            gridPane.getChildren().clear();
            for (ProductModel p : filteredProducts) {
                VBox card = createProductCard(nav, controller, p);
                gridPane.getChildren().add(card);
            }
            listTitle.setText("Product Catalog (" + filteredProducts.size() + " Items)");
        };

        Runnable rebuildCategoryButtons = () -> {
            dynamicCatBox.getChildren().clear();
            for (String catName : controller.getAvailableCategories()) {
                Button catBtn = new Button(catName);
                catBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-border-color:" + Theme.BORDER_DARK
                        + "; -fx-border-radius:8; -fx-background-radius:8 0 0 8; -fx-text-fill:" + Theme.TEXT_PRIMARY
                        + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:8 10; -fx-cursor:hand;");
                catBtn.setOnAction(ev -> {
                    filteredProducts.setPredicate(p -> catName.equalsIgnoreCase(p.getCategory()));
                    refreshGrid.run();
                });

                Button removeBtn = new Button("✖");
                removeBtn.setStyle("-fx-background-color:#3f1414; -fx-border-color:" + Theme.BORDER_DARK
                        + "; -fx-border-radius:8; -fx-background-radius:0 8 8 0; -fx-text-fill:#f87171; -fx-font-size:12px; -fx-font-weight:bold; -fx-padding:8 8; -fx-cursor:hand;");
                removeBtn.setOnAction(ev -> {
                    controller.removeCategory(catName);
                });

                HBox customTab = new HBox(0, catBtn, removeBtn);
                customTab.setAlignment(Pos.CENTER_LEFT);
                dynamicCatBox.getChildren().add(customTab);
            }
        };

        controller.getAvailableCategories().addListener((javafx.collections.ListChangeListener<String>) c -> rebuildCategoryButtons.run());
        rebuildCategoryButtons.run();

        // Category filter actions
        allCat.setOnAction(e -> {
            filteredProducts.setPredicate(p -> true);
            refreshGrid.run();
        });

        // Store change listener
        controller.getAllProducts().addListener((ListChangeListener<ProductModel>) c -> refreshGrid.run());

        refreshGrid.run();

        // Add Product button handler
        addProduct.setOnAction(e -> showAddProductDialog(controller));

        // View Deleted products handler
        viewDeletedBtn.setOnAction(e -> showDeletedProductsDialog(controller));

        VBox mainContent = new VBox(20, categories, filters, gridPane);
        mainContent.setPadding(new Insets(24, 26, 25, 26));

        // Center area Scrollable
        ScrollPane centerScroll = new ScrollPane(mainContent);
        Theme.applyScrollDarkStyle(centerScroll);
        root.setCenter(centerScroll);

        // Bottom Basket Bar
        Map<String, Integer> currentBasket = nav.getBasket();
        int totalItemsInBasket = currentBasket.values().stream().mapToInt(Integer::intValue).sum();
        Label basketTitle = new Label("Basket (" + currentBasket.size() + " Unique Items)");
        basketTitle.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        HBox basketItems = new HBox(12);
        if (currentBasket.isEmpty()) {
            Label emptyLbl = new Label("No items selected in basket. Click '+' on products above!");
            emptyLbl.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-padding:10;");
            basketItems.getChildren().add(emptyLbl);
        } else {
            for (Map.Entry<String, Integer> entry : currentBasket.entrySet()) {
                String bName = entry.getKey();
                int bQty = entry.getValue();
                Label basketItem = new Label(bName + "\nQty: " + bQty);
                basketItem.setPrefSize(180, 60);
                basketItem.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_PRIMARY
                        + "; -fx-background-radius:8; -fx-padding:8 12; -fx-border-color:" + Theme.BORDER_DARK
                        + "; -fx-border-radius:8;");
                basketItems.getChildren().add(basketItem);
            }
        }

        Label total = new Label("Total Items\n" + totalItemsInBasket + " units");
        total.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Button goBasket = new Button("Go to Basket  ->\nProceed to Billing");
        goBasket.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        goBasket.setOnAction(e -> nav.navigateTo(dashboard.BILLING));

        HBox bottomRow = new HBox(18, basketItems, total, goBasket);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        VBox basket = new VBox(10, basketTitle, bottomRow);
        basket.setPadding(new Insets(14, 28, 17, 28));
        basket.setStyle("-fx-background-color:" + Theme.BG_HEADER + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-width:1 0 0 0;");
        root.setBottom(basket);

        return root;
    }

    private static VBox createProductCard(dashboard nav, ProductController controller, ProductModel item) {
        String pName = item.getName();

        Label defaultPlaceholder = new Label(
                item.getImage() != null && !item.getImage().isBlank() ? item.getImage() : "PROD");
        defaultPlaceholder.setTextFill(Color.web(Theme.SKY_BLUE));
        defaultPlaceholder.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        StackPane placeholderBox = new StackPane(new Circle(36, Color.web(Theme.SKY_BLUE_BG)), defaultPlaceholder);

        StackPane imageBox = new StackPane(placeholderBox);
        imageBox.setPrefSize(180, 130);
        imageBox.setMinSize(180, 130);
        imageBox.setMaxSize(180, 130);
        imageBox.setStyle("-fx-background-color:" + Theme.BG_DARK + "; -fx-background-radius:10; -fx-border-color:"
                + Theme.BORDER_DARK + "; -fx-border-radius:10;");
        imageBox.setAlignment(Pos.CENTER);

        String url = item.getImageUrl();
        if (url != null && !url.isBlank()) {
            try {
                Image img = new Image(url, true);
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(170);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                imageBox.getChildren().setAll(imageView);

                img.errorProperty().addListener((obs, oldVal, newVal) -> {
                    if (Boolean.TRUE.equals(newVal)) {
                        imageBox.getChildren().setAll(placeholderBox);
                    }
                });
            } catch (Exception ignored) {
            }
        }

        Label name = new Label(pName);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        name.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        name.setWrapText(true);

        Label categoryTag = new Label(item.getCategory());
        categoryTag.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-size:11px; -fx-padding:2 8; -fx-background-radius:10;");

        Label price = new Label(item.getFormattedPrice());
        price.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        price.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label stock = new Label("Stock: " + item.getFormattedStock());
        stock.setTextFill(Color.web(item.getStock() < 10 ? "#f87171" : "#4ade80"));

        // Basket controls
        Button minus = new Button("-");
        minus.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-size:18px; -fx-cursor:hand;");

        int initialQty = nav.getBasket().getOrDefault(pName, 0);
        Label quantity = new Label(String.valueOf(initialQty));
        quantity.setStyle("-fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Button plus = new Button("+");
        plus.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-size:18px; -fx-cursor:hand;");

        minus.setOnAction(e -> {
            nav.changeQuantity(pName, -1);
            quantity.setText(String.valueOf(nav.getBasket().getOrDefault(pName, 0)));
            nav.navigateTo(dashboard.PRODUCTS);
        });
        plus.setOnAction(e -> {
            nav.addToBasket(pName);
            quantity.setText(String.valueOf(nav.getBasket().getOrDefault(pName, 0)));
            nav.navigateTo(dashboard.PRODUCTS);
        });

        HBox quantityBox = new HBox(12, minus, quantity, plus);
        quantityBox.setAlignment(Pos.CENTER);
        quantityBox.setStyle("-fx-background-color:" + Theme.BG_DARK + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:2 6;");

        VBox productCard = new VBox(8, imageBox, name, categoryTag, price, stock, quantityBox);
        productCard.setPadding(new Insets(14, 14, 12, 14));
        productCard.setPrefWidth(210);
        productCard.setAlignment(Pos.TOP_CENTER);
        productCard.setStyle(Theme.STYLE_CARD);
        return productCard;
    }

    private static void showAddProductDialog(ProductController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        ButtonType save = new ButtonType("Add Product", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField();
        name.setPromptText("Product Name (e.g. Basmati Rice)");
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
        price.setPromptText("Price (e.g. 65)");
        ComboBox<String> unit = new ComboBox<>();
        unit.getItems().addAll("kg", "pkt", "bottle", "pack", "L");
        unit.getSelectionModel().selectFirst();

        TextField stock = new TextField();
        stock.setPromptText("Available Stock (e.g. 30)");

        TextField image = new TextField();
        image.setPromptText("Short Image Code (e.g. RICE)");

        ComboBox<ProductImageOption> imageCombo = new ComboBox<>();
        imageCombo.getItems().addAll(ProductImageOption.getPresets());
        imageCombo.getSelectionModel().selectFirst();
        Theme.styleComboBox(imageCombo);

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(18);
        form.setPadding(new Insets(24));
        form.setPrefHeight(510);
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Category:"), categoryBox);
        form.addRow(2, new Label("Price:"), price);
        form.addRow(3, new Label("Unit:"), unit);
        form.addRow(4, new Label("Stock Qty:"), stock);
        form.addRow(5, new Label("Image Code:"), image);
        form.addRow(6, new Label("Product Image:"), imageCombo);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefHeight(530);

        dialog.showAndWait().ifPresent(button -> {
            if (button == save && !name.getText().isBlank()) {
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
                } catch (NumberFormatException ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR,
                            "Please enter valid numeric values for price and stock quantity.");
                    err.showAndWait();
                }
            }
        });
    }

    private static void showEditProductDialog(ProductController controller, ProductModel product) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product - " + product.getName());
        ButtonType save = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField(product.getName());
        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("Grocery", "Dairy", "Bakery", "Other");
        category.setValue(product.getCategory());

        TextField price = new TextField(String.valueOf(product.getPrice()));
        ComboBox<String> unit = new ComboBox<>();
        unit.getItems().addAll("kg", "pkt", "bottle", "pack", "L");
        unit.setValue(product.getUnit());

        TextField stock = new TextField(String.valueOf(product.getStock()));
        TextField image = new TextField(product.getImage());

        ComboBox<ProductImageOption> imageCombo = new ComboBox<>();
        imageCombo.getItems().addAll(ProductImageOption.getPresets());
        Theme.styleComboBox(imageCombo);

        String currentUrl = product.getImageUrl();
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
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(18));
        form.addRow(0, new Label("Name:"), name);
        form.addRow(1, new Label("Category:"), category);
        form.addRow(2, new Label("Price:"), price);
        form.addRow(3, new Label("Unit:"), unit);
        form.addRow(4, new Label("Stock Qty:"), stock);
        form.addRow(5, new Label("Image Code:"), image);
        form.addRow(6, new Label("Product Image:"), imageCombo);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(button -> {
            if (button == save && !name.getText().isBlank()) {
                try {
                    double p = Double.parseDouble(price.getText().trim());
                    double s = Double.parseDouble(stock.getText().trim());
                    ProductImageOption sel = imageCombo.getValue();
                    String imgUrl = sel != null ? sel.getUrl() : "";
                    controller.updateProduct(product, name.getText().trim(), category.getValue(), p, unit.getValue(), s,
                            image.getText().trim(), imgUrl);
                } catch (NumberFormatException ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR,
                            "Please enter valid numeric values for price and stock quantity.");
                    err.showAndWait();
                }
            }
        });
    }

    private static void showDeletedProductsDialog(ProductController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Deleted Products Bin");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        ListView<ProductModel> list = new ListView<>(controller.getDeletedProducts());
        list.setPrefSize(450, 300);

        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProductModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label name = new Label(item.getName() + " (" + item.getCategory() + ")");
                    name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    Label details = new Label(item.getFormattedPrice() + " | Stock: " + item.getFormattedStock());

                    VBox info = new VBox(3, name, details);

                    Button restoreBtn = new Button("↩ Restore");
                    restoreBtn.setStyle("-fx-background-color:" + Theme.SKY_BLUE_BG + "; -fx-text-fill:"
                            + Theme.SKY_BLUE + "; -fx-font-weight:bold; -fx-cursor:hand;");
                    restoreBtn.setOnAction(e -> controller.restoreProduct(item));

                    HBox row = new HBox(12, info, dashboard.spacer(), restoreBtn);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(8));
                    setGraphic(row);
                }
            }
        });

        VBox content = new VBox(10,
                new Label("The following items have been soft-deleted. Click Restore to make them active again:"),
                list);
        content.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
}
