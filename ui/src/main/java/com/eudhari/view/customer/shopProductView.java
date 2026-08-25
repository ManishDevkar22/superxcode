package com.eudhari.view.customer;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.OrderController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.ProductController;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.OrderItemModel;
import com.eudhari.model.OrderModel;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import com.eudhari.model.shopkeppermodel.ProductModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.*;


public class shopProductView {

    private final BorderPane root;
    private final FlowPane productGrid = new FlowPane();
    private final List<ProductCard> productCards = new ArrayList<>();
    private final Map<ProductModel, Integer> selectedCart = new LinkedHashMap<>();

    // Right-side dynamic UI components
    private final VBox cartItemsContainer = new VBox(8);
    private final Label cartItemCountBadge = new Label("0 Items");
    private final Label subtotalLabel = new Label("₹0.00");
    private final Label grandTotalLabel = new Label("₹0.00");
    private final Label availableCreditLabel = new Label("₹8,500.00");
    private final Button placeOrderBtn = new Button("Send Order to Shopkeeper ➔");

    // Customer Order Status Container
    private final VBox customerOrdersContainer = new VBox(10);

    private String selectedCategory = "All Items";
    private String searchQuery = "";

    // Active Connected Shop Mapping
    private final Map<String, ShopModel> connectedShopMap = new HashMap<>();
    private ShopModel activeShop = null;

    public shopProductView(String initialShopName, Runnable onBack) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc; -fx-font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, sans-serif;");

        // Top Header
        HBox headerBar = createHeader(initialShopName, onBack);
        root.setTop(headerBar);

        // Main Layout: Left (Product Catalog + Order History) & Right (Selected Products List + Total)
        HBox mainLayout = new HBox(24);
        mainLayout.setPadding(new Insets(20, 24, 24, 24));

        VBox leftCatalogSection = createProductCatalogSection();
        VBox rightCartSection = createRightSideCartSection();

        HBox.setHgrow(leftCatalogSection, Priority.ALWAYS);
        rightCartSection.setMinWidth(380);
        rightCartSection.setMaxWidth(400);

        mainLayout.getChildren().addAll(leftCatalogSection, rightCartSection);

        ScrollPane mainScrollPane = new ScrollPane(mainLayout);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setStyle("-fx-background-color: transparent; -fx-background: #c1e1ff; -fx-border-color: transparent;");

        root.setCenter(mainScrollPane);

        // Initial Data Load
        loadConnectedShopsAndProducts(initialShopName);
        refreshCustomerOrders();
    }

    public BorderPane getView() {
        return root;
    }

    // --- 1. Top Header ---
    private HBox createHeader(String initialShopName, Runnable onBack) {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 12, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-font-weight: bold; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        StackPane avatarPane = new StackPane();
        Rectangle avatarRect = new Rectangle(46, 46);
        avatarRect.setArcWidth(12);
        avatarRect.setArcHeight(12);
        avatarRect.setFill(Color.web("#e2e8f0"));
        Label avatarIcon = new Label("🏪");
        avatarIcon.setStyle("-fx-font-size: 22px;");
        avatarPane.getChildren().addAll(avatarRect, avatarIcon);

        // Connected Shop Selection Control (Dropdown)
        ComboBox<String> shopSelector = new ComboBox<>();
        shopSelector.setPromptText("Select Approved Shop...");
        shopSelector.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #0f172a; -fx-font-weight: bold; " +
                "-fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4 10; -fx-font-size: 13px;");

        VBox shopMeta = new VBox(2);
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("No Shop Selected");
        nameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label connectedBadge = new Label("✔ Connected");
        connectedBadge.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 10px; " +
                "-fx-font-weight: bold; -fx-padding: 2 8; -fx-background-radius: 10;");
        titleRow.getChildren().addAll(nameLabel, connectedBadge);

        Label subInfo = new Label("Select an approved shop to view products.");
        subInfo.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        shopMeta.getChildren().addAll(titleRow, subInfo);

        shopSelector.setOnAction(e -> {
            String selectedText = shopSelector.getValue();
            if (selectedText != null && connectedShopMap.containsKey(selectedText)) {
                ShopModel shop = connectedShopMap.get(selectedText);
                this.activeShop = shop;
                nameLabel.setText(shop.getShopName());
                subInfo.setText("📍 " + (shop.getAddress() != null && !shop.getAddress().isBlank() ? shop.getAddress() : "Local Area"));
                availableCreditLabel.setText("₹8,500.00");
                selectedCart.clear();
                loadProductsForActiveShop();
            }
        });

        VBox shopBoxWithSelector = new VBox(4);
        Label selTitle = new Label("Select Connected Shop:");
        selTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748b;");
        shopBoxWithSelector.getChildren().addAll(selTitle, shopSelector);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search in Shop
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #e2e8f0; -fx-border-radius: 20; " +
                "-fx-background-radius: 20; -fx-padding: 6 14; -fx-pref-width: 230;");
        Label searchIcon = new Label("🔍");
        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 13px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            this.searchQuery = newV.trim().toLowerCase();
            filterProducts();
        });
        searchBox.getChildren().addAll(searchIcon, searchField);

        header.getChildren().addAll(backBtn, avatarPane, shopMeta, shopBoxWithSelector, spacer, searchBox);
        return header;
    }

    // --- 2. Left Section: Product Catalog Grid & Order History ---
    private VBox createProductCatalogSection() {
        VBox section = new VBox(20);

        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Available Products");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox categoryButtons = new HBox(6);
        String[] categories = {"All Items", "Grocery", "Dairy", "Bakery", "Spices", "Oils"};
        for (String cat : categories) {
            Button catBtn = new Button(cat);
            updateCategoryBtnStyle(catBtn, cat.equals(selectedCategory));
            catBtn.setOnAction(e -> {
                this.selectedCategory = cat;
                for (javafx.scene.Node node : categoryButtons.getChildren()) {
                    if (node instanceof Button b) {
                        updateCategoryBtnStyle(b, b.getText().equals(selectedCategory));
                    }
                }
                filterProducts();
            });
            categoryButtons.getChildren().add(catBtn);
        }

        filterBar.getChildren().addAll(title, spacer, categoryButtons);

        productGrid.setHgap(14);
        productGrid.setVgap(14);
        productGrid.setPrefWrapLength(650);

        // Make Product Grid vertically scrollable
        ScrollPane productScrollPane = new ScrollPane(productGrid);
        productScrollPane.setFitToWidth(true);
        productScrollPane.setPrefViewportHeight(450);
        productScrollPane.setMinHeight(300);
        productScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        productScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        // --- CUSTOMER ORDER HISTORY / STATUS SECTION ---
        VBox orderHistoryBox = new VBox(12);
        orderHistoryBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-padding: 18;");
        
        Label historyTitle = new Label("📦 My Sent Order Requests & Status");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label historySub = new Label("Track live updates from shopkeepers on your order requests.");
        historySub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Button refreshOrdersBtn = new Button("🔄 Refresh Orders");
        refreshOrdersBtn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 10; -fx-cursor: hand;");
        refreshOrdersBtn.setOnAction(e -> refreshCustomerOrders());

        HBox historyHeader = new HBox(10, new VBox(2, historyTitle, historySub), new Region(), refreshOrdersBtn);
        HBox.setHgrow(historyHeader.getChildren().get(1), Priority.ALWAYS);
        historyHeader.setAlignment(Pos.CENTER_LEFT);

        ScrollPane ordersScroll = new ScrollPane(customerOrdersContainer);
        ordersScroll.setFitToWidth(true);
        ordersScroll.setPrefViewportHeight(280);
        ordersScroll.setMinHeight(160);
        ordersScroll.setMaxHeight(400);
        ordersScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        ordersScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        orderHistoryBox.getChildren().addAll(historyHeader, ordersScroll);

        section.getChildren().addAll(filterBar, productScrollPane, orderHistoryBox);
        return section;
    }

    private void updateCategoryBtnStyle(Button btn, boolean isActive) {
        if (isActive) {
            btn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 12; -fx-padding: 6 14; -fx-font-size: 12px; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #4338ca; -fx-font-weight: bold; " +
                    "-fx-background-radius: 12; -fx-padding: 6 14; -fx-font-size: 12px; -fx-cursor: hand;");
        }
    }

    // --- 3. Right Section: Live Selected Products List & Total Calculation ---
    private VBox createRightSideCartSection() {
        VBox rightSection = new VBox(16);

        // --- A. SELECTED PRODUCTS / CART CARD ---
        VBox cartCard = new VBox(14);
        cartCard.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 16; " +
                "-fx-background-radius: 16; -fx-padding: 18;");
        cartCard.setEffect(new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.04)));

        // Header of Cart Card
        HBox cartHeader = new HBox(8);
        cartHeader.setAlignment(Pos.CENTER_LEFT);
        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size: 18px;");
        Label cartTitle = new Label("Selected Products");
        cartTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        cartItemCountBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 10;");

        cartHeader.getChildren().addAll(cartIcon, cartTitle, sp, cartItemCountBadge);

        // Scrollable Items Container
        ScrollPane cartScroll = new ScrollPane(cartItemsContainer);
        cartScroll.setFitToWidth(true);
        cartScroll.setMaxHeight(260);
        cartScroll.setMinHeight(120);
        cartScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        // Bill Summary Section
        VBox billSummaryBox = new VBox(8);
        billSummaryBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-padding: 12;");

        HBox subtotalRow = createBillRow("Items Subtotal", subtotalLabel, false);
        HBox creditRow = createBillRow("Available Udhaari Limit", availableCreditLabel, false);
        availableCreditLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold; -fx-font-size: 12px;");

        Separator sep = new Separator();

        HBox totalRow = createBillRow("Grand Total", grandTotalLabel, true);
        grandTotalLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        billSummaryBox.getChildren().addAll(subtotalRow, creditRow, sep, totalRow);

        // Order Action Button (Send Order Request)
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);
        placeOrderBtn.setStyle("-fx-background-color: #4338ca; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14px; -fx-background-radius: 10; -fx-padding: 11 16; -fx-cursor: hand;");
        placeOrderBtn.setOnAction(e -> handleSendOrderRequest());

        cartCard.getChildren().addAll(cartHeader, cartScroll, billSummaryBox, placeOrderBtn);

        // --- B. UDHARI ACCOUNT CREDIT STATUS CARD ---
        VBox udhariStatusCard = new VBox(12);
        udhariStatusCard.setStyle("-fx-background-color: #f5f3ff; -fx-border-color: #ddd6fe; -fx-border-radius: 14; " +
                "-fx-background-radius: 14; -fx-padding: 14;");

        HBox uHead = new HBox(8);
        uHead.setAlignment(Pos.CENTER_LEFT);
        Label uIcon = new Label("💳");
        Label uTitle = new Label("Shop Credit Account");
        uTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4338ca;");
        uHead.getChildren().addAll(uIcon, uTitle);

        HBox statsCols = new HBox(10);
        statsCols.setAlignment(Pos.CENTER);
        VBox col1 = createMiniStat("TOTAL LIMIT", "₹10,000", "#1e293b");
        VBox col2 = createMiniStat("USED DUES", "₹1,500", "#dc2626");
        VBox col3 = createMiniStat("REMAINING", "₹8,500", "#16a34a");
        HBox.setHgrow(col1, Priority.ALWAYS);
        HBox.setHgrow(col2, Priority.ALWAYS);
        HBox.setHgrow(col3, Priority.ALWAYS);
        statsCols.getChildren().addAll(col1, col2, col3);

        udhariStatusCard.getChildren().addAll(uHead, statsCols);

        rightSection.getChildren().addAll(cartCard, udhariStatusCard);
        return rightSection;
    }

    private HBox createBillRow(String title, Label valueLabel, boolean isBold) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(isBold ? "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0f172a;" : "-fx-font-size: 12px; -fx-text-fill: #64748b;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(titleLabel, spacer, valueLabel);
        return row;
    }

    private VBox createMiniStat(String label, String value, String colorHex) {
        VBox col = new VBox(2);
        col.setAlignment(Pos.CENTER);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748b;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        col.getChildren().addAll(lbl, val);
        return col;
    }

    // --- 4. Product Card Component (with Steppers) ---
    private class ProductCard extends VBox {
        final ProductModel product;
        int quantity = 0;
        final Label qtyLabel = new Label("0");

        public ProductCard(ProductModel product) {
            this.product = product;
            this.setPrefWidth(190);
            this.setStyle("-fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-radius: 12; " +
                    "-fx-background-radius: 12; -fx-padding: 10;");
            this.setEffect(new DropShadow(4, 0, 2, Color.rgb(0, 0, 0, 0.03)));
            this.setSpacing(8);

            // Product Icon Box with Async Image Loading
            StackPane imageBox = new StackPane();
            imageBox.setPrefSize(170, 95);
            imageBox.setMinSize(170, 95);
            imageBox.setMaxSize(170, 95);
            imageBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
            imageBox.setAlignment(Pos.CENTER);

            Label defaultPlaceholder = new Label(
                    product.getImage() != null && !product.getImage().isBlank() ? product.getImage() : "📦");
            defaultPlaceholder.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4338ca;");
            StackPane placeholderPane = new StackPane(defaultPlaceholder);

            String imgUrl = product.getImageUrl();
            if ((imgUrl == null || imgUrl.isBlank()) && product.getImage() != null && product.getImage().startsWith("http")) {
                imgUrl = product.getImage();
            }

            if (imgUrl != null && !imgUrl.isBlank()) {
                try {
                    Image img = new Image(imgUrl, true);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(165);
                    imageView.setFitHeight(90);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);

                    imageBox.getChildren().setAll(imageView);

                    img.errorProperty().addListener((obs, oldVal, newVal) -> {
                        if (Boolean.TRUE.equals(newVal)) {
                            imageBox.getChildren().setAll(placeholderPane);
                        }
                    });
                } catch (Exception e) {
                    imageBox.getChildren().setAll(placeholderPane);
                }
            } else {
                imageBox.getChildren().setAll(placeholderPane);
            }

            // Product Info
            VBox info = new VBox(2);
            Label name = new Label(product.getName());
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            Label price = new Label(String.format("₹%.2f / %s", product.getPrice(), product.getUnit()));
            price.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
            info.getChildren().addAll(name, price);

            // Stepper Control [ –  0  + ]
            HBox stepper = new HBox();
            stepper.setAlignment(Pos.CENTER);
            stepper.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 3 6;");

            Button minusBtn = new Button("–");
            minusBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 2 8;");

            qtyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 0 8;");

            Button plusBtn = new Button("+");
            plusBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 2 8;");

            minusBtn.setOnAction(e -> {
                if (quantity > 0) {
                    quantity--;
                    updateQuantity(quantity);
                }
            });

            plusBtn.setOnAction(e -> {
                quantity++;
                updateQuantity(quantity);
            });

            Region s1 = new Region();
            Region s2 = new Region();
            HBox.setHgrow(s1, Priority.ALWAYS);
            HBox.setHgrow(s2, Priority.ALWAYS);

            stepper.getChildren().addAll(minusBtn, s1, qtyLabel, s2, plusBtn);
            this.getChildren().addAll(imageBox, info, stepper);
        }

        public void setQuantity(int qty) {
            this.quantity = qty;
            this.qtyLabel.setText(String.valueOf(qty));
        }

        private void updateQuantity(int newQty) {
            qtyLabel.setText(String.valueOf(newQty));
            if (newQty > 0) {
                selectedCart.put(product, newQty);
            } else {
                selectedCart.remove(product);
            }
            refreshRightSideCart();
        }
    }

    // --- 5. Real-Time Cart & Total Calculation Update ---
    private void refreshRightSideCart() {
        cartItemsContainer.getChildren().clear();

        if (selectedCart.isEmpty()) {
            VBox emptyBox = new VBox(6);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(25, 10, 25, 10));
            Label emptyIcon = new Label("🛍️");
            emptyIcon.setStyle("-fx-font-size: 24px;");
            Label emptyText = new Label("No products selected");
            emptyText.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");
            Label emptySub = new Label("Click + on any product to add");
            emptySub.setStyle("-fx-font-size: 11px; -fx-text-fill: #cbd5e1;");
            emptyBox.getChildren().addAll(emptyIcon, emptyText, emptySub);
            cartItemsContainer.getChildren().add(emptyBox);

            cartItemCountBadge.setText("0 Items");
            subtotalLabel.setText("₹0.00");
            grandTotalLabel.setText("₹0.00");
            return;
        }

        int totalItemCount = 0;
        double totalPrice = 0.0;

        for (Map.Entry<ProductModel, Integer> entry : selectedCart.entrySet()) {
            ProductModel p = entry.getKey();
            int qty = entry.getValue();
            double itemTotal = p.getPrice() * qty;

            totalItemCount += qty;
            totalPrice += itemTotal;

            // Single Item Row in Right Sidebar List
            HBox itemRow = new HBox(8);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8; " +
                    "-fx-background-radius: 8; -fx-padding: 8 10;");

            Label itemIcon = new Label("📦");
            itemIcon.setStyle("-fx-font-size: 16px;");

            VBox itemMeta = new VBox(1);
            Label nameLbl = new Label(p.getName());
            nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            Label calcLbl = new Label(qty + " " + p.getUnit() + " × ₹" + String.format("%.2f", p.getPrice()));
            calcLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            itemMeta.getChildren().addAll(nameLbl, calcLbl);

            Region rowSpacer = new Region();
            HBox.setHgrow(rowSpacer, Priority.ALWAYS);

            Label totalLbl = new Label(String.format("₹%.2f", itemTotal));
            totalLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; " +
                    "-fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 2 4;");
            removeBtn.setOnAction(e -> {
                selectedCart.remove(p);
                for (ProductCard card : productCards) {
                    if (card.product == p) {
                        card.setQuantity(0);
                    }
                }
                refreshRightSideCart();
            });

            itemRow.getChildren().addAll(itemIcon, itemMeta, rowSpacer, totalLbl, removeBtn);
            cartItemsContainer.getChildren().add(itemRow);
        }

        cartItemCountBadge.setText(totalItemCount + (totalItemCount == 1 ? " Item" : " Items"));
        subtotalLabel.setText(String.format("₹%.2f", totalPrice));
        grandTotalLabel.setText(String.format("₹%.2f", totalPrice));
    }

    // --- 6. Send Order Request Handler ---
    private void handleSendOrderRequest() {
        if (selectedCart.isEmpty()) {
            showAlert("No Products Selected", "Please select at least one product to place your order.");
            return;
        }
        if (activeShop == null || activeShop.getShopId() == null) {
            showAlert("No Connected Shop Selected", "Please select an approved connected shop before placing an order.");
            return;
        }

        UserModel loggedInCust = ProfileController.getInstance().getCurrentUserProfile();
        String customerId = loggedInCust != null && loggedInCust.getUid() != null ? loggedInCust.getUid() : "";
        String customerName = loggedInCust != null && loggedInCust.getName() != null ? loggedInCust.getName() : "Customer";

        List<OrderItemModel> itemsList = new ArrayList<>();
        double grandTotal = 0.0;

        for (Map.Entry<ProductModel, Integer> entry : selectedCart.entrySet()) {
            ProductModel p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = p.getPrice() * qty;
            grandTotal += subtotal;

            itemsList.add(new OrderItemModel(
                    p.getId(),
                    p.getName(),
                    qty,
                    p.getPrice(),
                    subtotal
            ));
        }

        OrderModel createdOrder = OrderController.getInstance().createOrder(
                customerId,
                customerName,
                activeShop.getShopId(),
                activeShop.getShopName(),
                activeShop.getOwnerId(),
                itemsList,
                grandTotal
        );

        if (createdOrder != null) {
            showAlert("Order Request Sent Successfully! 🎉",
                    "Your order (" + createdOrder.getOrderId() + ") totaling ₹" + String.format("%.2f", grandTotal) +
                            " has been sent to " + activeShop.getShopName() + ".\nStatus: PENDING");
            selectedCart.clear();
            for (ProductCard card : productCards) {
                card.setQuantity(0);
            }
            refreshRightSideCart();
            refreshCustomerOrders();
        } else {
            showAlert("Order Failed", "Failed to send order request. Please try again.");
        }
    }

    // --- 7. Refresh Customer Order Status List ---
    private void refreshCustomerOrders() {
        customerOrdersContainer.getChildren().clear();

        UserModel loggedInCust = ProfileController.getInstance().getCurrentUserProfile();
        String customerId = loggedInCust != null && loggedInCust.getUid() != null ? loggedInCust.getUid() : "";

        List<OrderModel> orders = OrderController.getInstance().getOrdersForCustomer(customerId);

        if (orders == null || orders.isEmpty()) {
            VBox emptyBox = new VBox(4);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(15));
            Text emptyTxt = new Text("No order requests found.");
            emptyTxt.setStyle("-fx-fill: #94a3b8; -fx-font-size: 13px;");
            emptyBox.getChildren().add(emptyTxt);
            customerOrdersContainer.getChildren().add(emptyBox);
            return;
        }

        for (OrderModel order : orders) {
            VBox card = createCustomerOrderCard(order);
            customerOrdersContainer.getChildren().add(card);
        }
    }

    private VBox createCustomerOrderCard(OrderModel order) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label orderIdLbl = new Label("Order #" + order.getOrderId());
        orderIdLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0f172a;");

        Label shopNameLbl = new Label("• " + order.getShopName());
        shopNameLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label statusBadge = new Label(order.getStatus());
        updateStatusBadgeStyle(statusBadge, order.getStatus());

        topRow.getChildren().addAll(orderIdLbl, shopNameLbl, sp, statusBadge);

        // Render items summary
        StringBuilder sb = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItemModel item : order.getItems()) {
                sb.append(item.getProductName()).append(" (x").append(item.getQuantity()).append("), ");
            }
        }
        String itemsStr = sb.length() > 2 ? sb.substring(0, sb.length() - 2) : "No items";

        Label itemsLbl = new Label("Items: " + itemsStr);
        itemsLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");

        HBox botRow = new HBox(10);
        botRow.setAlignment(Pos.CENTER_LEFT);

        Label totalLbl = new Label(String.format("Total: ₹%.2f", order.getTotalAmount()));
        totalLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2563eb;");

        Region botSp = new Region();
        HBox.setHgrow(botSp, Priority.ALWAYS);

        botRow.getChildren().addAll(totalLbl, botSp);

        if ("PENDING".equalsIgnoreCase(order.getStatus())) {
            Button cancelBtn = new Button("Cancel Order");
            cancelBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 6; -fx-padding: 4 10; -fx-cursor: hand;");
            cancelBtn.setOnAction(e -> {
                boolean success = OrderController.getInstance().cancelOrder(order.getOrderId());
                if (success) {
                    showAlert("Order Cancelled", "Your order " + order.getOrderId() + " has been cancelled.");
                    refreshCustomerOrders();
                } else {
                    showAlert("Action Failed", "Could not cancel order.");
                }
            });
            botRow.getChildren().add(cancelBtn);
        }

        card.getChildren().addAll(topRow, itemsLbl, botRow);
        return card;
    }

    private void updateStatusBadgeStyle(Label badge, String status) {
        String s = status != null ? status.toUpperCase() : "PENDING";
        switch (s) {
            case "ACCEPTED":
                badge.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10;");
                break;
            case "REJECTED":
                badge.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10;");
                break;
            case "CANCELLED":
                badge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10;");
                break;
            case "PENDING":
            default:
                badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 10;");
                break;
        }
    }

    // --- Data Loaders ---
    @SuppressWarnings("unchecked")
    private void loadConnectedShopsAndProducts(String preferredShopName) {
        UserModel loggedInCust = ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = loggedInCust != null && loggedInCust.getUid() != null ? loggedInCust.getUid() : "";

        List<ConnectionRequestModel> approvedReqs = ConnectionRequestController.getInstance().getApprovedConnectedShopsForCustomer(currentCustId);
        connectedShopMap.clear();

        ComboBox<String> selector = null;
        if (root.getTop() instanceof HBox header) {
            for (javafx.scene.Node n : header.getChildren()) {
                if (n instanceof VBox vbox && vbox.getChildren().size() > 1 && vbox.getChildren().get(1) instanceof ComboBox) {
                    selector = (ComboBox<String>) vbox.getChildren().get(1);
                    break;
                }
            }
        }

        if (selector != null) {
            selector.getItems().clear();
        }

        if (approvedReqs != null && !approvedReqs.isEmpty()) {
            for (ConnectionRequestModel req : approvedReqs) {
                ShopModel shop = ShopController.getInstance().getShopById(req.getShopId());
                if (shop != null) {
                    String displayName = shop.getShopName() + " (" + (shop.getAddress() != null && !shop.getAddress().isBlank() ? shop.getAddress() : "Local") + ")";
                    connectedShopMap.put(displayName, shop);
                    if (selector != null) {
                        selector.getItems().add(displayName);
                    }
                }
            }
        }

        if (selector != null && !selector.getItems().isEmpty()) {
            selector.setValue(selector.getItems().get(0));
            ShopModel defaultShop = connectedShopMap.get(selector.getItems().get(0));
            this.activeShop = defaultShop;
            loadProductsForActiveShop();
        } else {
            productGrid.getChildren().clear();
            VBox emptyNotice = new VBox(10);
            emptyNotice.setAlignment(Pos.CENTER);
            emptyNotice.setPadding(new Insets(30));
            Text msg1 = new Text("No Approved Connected Shops Available");
            msg1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #0f172a;");
            Text msg2 = new Text("Please connect with a shopkeeper from the Customer Dashboard to view and order products.");
            msg2.setStyle("-fx-font-size: 13px; -fx-fill: #64748b;");
            emptyNotice.getChildren().addAll(msg1, msg2);
            productGrid.getChildren().add(emptyNotice);
        }
    }

    private void loadProductsForActiveShop() {
        productCards.clear();
        if (activeShop != null && activeShop.getShopId() != null) {
            List<ProductModel> products = ProductController.getInstance().getProductsByShopId(activeShop.getShopId());
            if (products != null) {
                for (ProductModel p : products) {
                    productCards.add(new ProductCard(p));
                }
            }
        }
        filterProducts();
        refreshRightSideCart();
    }

    private void filterProducts() {
        productGrid.getChildren().clear();
        if (productCards.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setPadding(new Insets(20));
            emptyBox.setAlignment(Pos.CENTER);
            Text t = new Text(activeShop != null ? "No products added by " + activeShop.getShopName() + " yet." : "No products available.");
            t.setStyle("-fx-fill: #64748b; -fx-font-size: 14px; -fx-font-weight: bold;");
            emptyBox.getChildren().add(t);
            productGrid.getChildren().add(emptyBox);
            return;
        }

        for (ProductCard card : productCards) {
            boolean matchesCat = selectedCategory.equals("All Items") || card.product.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = searchQuery.isEmpty() || card.product.getName().toLowerCase().contains(searchQuery);
            if (matchesCat && matchesQuery) {
                productGrid.getChildren().add(card);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}