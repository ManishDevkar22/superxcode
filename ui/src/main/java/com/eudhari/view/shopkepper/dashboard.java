package com.eudhari.view.shopkepper;

import com.eudhari.view.login.Loginpage;

import com.eudhari.model.shopkeppermodel.*;
// import com.eudhari.model.ProductStore;
// import com.eudhari.model.TransactionStore;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class dashboard {
    public static final String DASHBOARD = "Dashboard";
    public static final String PRODUCTS = "Products";
    public static final String ORDERS = "Orders / Requests";
    public static final String NOTIFICATIONS = "Notifications";
    public static final String BILLING = "Billing";
    public static final String CUSTOMERS = "Customers";
    public static final String UDHARI = "Udhari";
    public static final String REPORTS = "Sales & Profit / Reports";
    public static final String INVENTORY = "Inventory";
    public static final String HELP_SUPPORT = "Help & Support";
    public static final String PROFILE = "Profile";
    public static final String LOGOUT = "Logout";

    private static final List<String> MENU = List.of(DASHBOARD, PRODUCTS, NOTIFICATIONS, BILLING,
            CUSTOMERS, UDHARI, REPORTS, INVENTORY, HELP_SUPPORT, PROFILE, LOGOUT);

    private Stage stage;
    private BorderPane mainRoot;
    private HBox staticTopBar;
    private VBox staticSidebar;
    private ListView<String> navListView;
    private String currentActivePage = DASHBOARD;
    private Label skNameLabel;
    private Label skAvatarLabel;
    private Label activePageTitleLabel;

    private final Map<String, Integer> basket = new LinkedHashMap<>();
    private final List<String> purchaseHistory = new ArrayList<>();
    private final ProductStore productStore = ProductStore.getInstance();
    private final CustomerStore customerStore = CustomerStore.getInstance();
    private final TransactionStore transactionStore = TransactionStore.getInstance();

    private com.eudhari.model.OrderModel selectedOrderForBilling;

    public void setSelectedOrderForBilling(com.eudhari.model.OrderModel order) {
        this.selectedOrderForBilling = order;
    }

    public com.eudhari.model.OrderModel getSelectedOrderForBilling() {
        return selectedOrderForBilling;
    }

    public void clearSelectedOrderForBilling() {
        this.selectedOrderForBilling = null;
    }

    public void navigateToBillingWithOrder(com.eudhari.model.OrderModel order) {
        this.selectedOrderForBilling = order;
        navigateTo(BILLING);
    }

    public void show(Stage primaryStage) {
        stage = primaryStage;
        stage.setMinWidth(1050);
        stage.setMinHeight(650);

        mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");

        staticTopBar = buildStaticTopBar();
        mainRoot.setTop(staticTopBar);

        staticSidebar = buildStaticSidebar();
        mainRoot.setLeft(staticSidebar);

        Scene scene = new Scene(mainRoot, 1280, 800);
        stage.setScene(scene);

        navigateTo(DASHBOARD);
    }

    private HBox buildStaticTopBar() {
        HBox topBar = new HBox(18);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setStyle("-fx-background-color: " + Theme.BG_HEADER + "; -fx-border-color: transparent transparent "
                + Theme.BORDER_DARK + " transparent; " +
                "-fx-border-width: 0 0 1 0; -fx-pref-height: 64px; -fx-min-height: 64px; -fx-max-height: 64px;");

        // Top-Left: Current Role Badge & Dynamic Page Name
        Label roleBadge = new Label("SHOPKEEPER");
        roleBadge.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-font-weight: bold; "
                        +
                        "-fx-font-size: 11px; -fx-padding: 6px 14px; -fx-background-radius: 20px; -fx-border-color: #A7F3D0; -fx-border-radius: 20px;");

        activePageTitleLabel = new Label(currentActivePage != null ? currentActivePage : DASHBOARD);
        activePageTitleLabel.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #eeeff1ff;");

        HBox topLeftBox = new HBox(12, roleBadge, activePageTitleLabel);
        topLeftBox.setAlignment(Pos.CENTER_LEFT);

        HBox spacer = spacer();

        // Top-Right: Date & Time, Notifications Icon, User/Profile Controls
        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox("#3A57E8",
                "-fx-background-color: #F8FAFC; -fx-padding: 6 12; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #F8FAFC; -fx-background-radius: 50%; -fx-border-color: #E2E8F0; "
                        +
                        "-fx-border-radius: 50%; -fx-min-width: 38px; -fx-min-height: 38px; -fx-font-size: 14px; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> navigateTo(NOTIFICATIONS));

        com.eudhari.model.UserModel currentUser = com.eudhari.controller.ProfileController.getInstance()
                .getCurrentUserProfile();
        String displaySkName = currentUser != null && currentUser.getName() != null && !currentUser.getName().isBlank()
                ? currentUser.getName()
                : "Shopkeeper";
        String skInit = displaySkName.length() >= 2 ? displaySkName.substring(0, 2).toUpperCase() : "SK";

        skAvatarLabel = new Label(skInit);
        skAvatarLabel.setAlignment(Pos.CENTER);
        skAvatarLabel.setPrefSize(36, 36);
        skAvatarLabel.setMinSize(36, 36);
        skAvatarLabel.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #EEF2FF; -fx-text-fill: #556ed3ff; -fx-font-weight: bold; "
                        +
                        "-fx-font-size: 13px; -fx-background-radius: 50%; -fx-border-color: #C7D2FE; -fx-border-radius: 50%;");

        VBox shopkeeperInfo = new VBox(1);
        skNameLabel = new Label(displaySkName);
        skNameLabel.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e9ecf1ff;");
        Label skRoleLbl = new Label("Merchant Partner");
        skRoleLbl.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 10px; -fx-text-fill: #f3f7fcff;");
        shopkeeperInfo.getChildren().addAll(skNameLabel, skRoleLbl);

        HBox profileBox = new HBox(8, skAvatarLabel, shopkeeperInfo);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setStyle("-fx-cursor: hand;");
        profileBox.setOnMouseClicked(e -> navigateTo(PROFILE));

        topBar.getChildren().addAll(topLeftBox, spacer, clockWidget, notifBtn, profileBox);
        return topBar;
    }

    private void updateTopBarProfile() {
        if (skNameLabel != null && skAvatarLabel != null) {
            com.eudhari.model.UserModel currentUser = com.eudhari.controller.ProfileController.getInstance()
                    .getCurrentUserProfile();
            String displaySkName = currentUser != null && currentUser.getName() != null
                    && !currentUser.getName().isBlank() ? currentUser.getName() : "Shopkeeper";
            String skInit = displaySkName.length() >= 2 ? displaySkName.substring(0, 2).toUpperCase() : "SK";
            skNameLabel.setText(displaySkName);
            skAvatarLabel.setText(skInit);
        }
    }

    public ProductStore getProductStore() {
        return productStore;
    }

    public CustomerStore getCustomerStore() {
        return customerStore;
    }

    public TransactionStore getTransactionStore() {
        return transactionStore;
    }

    private VBox buildStaticSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(20, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + Theme.BG_SIDEBAR + "; " +
                "-fx-border-color: transparent #1E293B transparent transparent; -fx-border-width: 0 1 0 0;");

        // Brand Header
        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 14, 8));

        Label brandIcon = new Label("🏪");
        brandIcon.setAlignment(Pos.CENTER);
        brandIcon.setPrefSize(38, 38);
        brandIcon.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: rgba(58, 87, 232, 0.2); -fx-text-fill: #60A5FA; "
                        +
                        "-fx-font-size: 18px; -fx-background-radius: 8px;");

        VBox brandText = new VBox(1);
        Label brandTitle = new Label("Smart eUdhari");
        brandTitle.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label brandSubtitle = new Label("Shopkeeper Console");
        brandSubtitle.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        brandText.getChildren().addAll(brandTitle, brandSubtitle);

        brandBox.getChildren().addAll(brandIcon, brandText);
        sidebar.getChildren().add(brandBox);

        navListView = new ListView<>(FXCollections.observableArrayList(MENU));
        navListView.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        VBox.setVgrow(navListView, Priority.ALWAYS);

        navListView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    boolean isLogout = item.equals(LOGOUT);
                    boolean isActive = currentActivePage != null && !currentActivePage.isEmpty()
                            && item.toLowerCase().contains(currentActivePage.toLowerCase().replaceAll(" / reports", "")
                                    .replaceAll(" / requests", "").trim());

                    if (isActive || isSelected()) {
                        setStyle(
                                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #3A57E8; -fx-text-fill: #FFFFFF; "
                                        +
                                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else if (isLogout) {
                        setStyle(
                                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #F87171; "
                                        +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else {
                        setStyle(
                                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #94A3B8; "
                                        +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    }

                    setOnMouseEntered(e -> {
                        if (!isActive && !isSelected()) {
                            if (isLogout) {
                                setStyle(
                                        "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #3F1D1D; -fx-text-fill: #EF4444; "
                                                +
                                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle(
                                        "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; "
                                                +
                                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isActive && !isSelected()) {
                            if (isLogout) {
                                setStyle(
                                        "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #F87171; "
                                                +
                                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle(
                                        "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #94A3B8; "
                                                +
                                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });
                }
            }
        });

        navListView.setOnMouseClicked(e -> {
            String selected = navListView.getSelectionModel().getSelectedItem();
            if (selected != null)
                navigateTo(selected);
        });

        sidebar.getChildren().add(navListView);

        // Support Pill Button
        Button supportBtn = new Button("🎧  Support");
        supportBtn.setMaxWidth(Double.MAX_VALUE);
        supportBtn.setStyle(
                "-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; "
                        +
                        "-fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
        supportBtn.setOnAction(e -> navigateTo(HELP_SUPPORT));

        sidebar.getChildren().add(supportBtn);
        return sidebar;
    }

    public void navigateTo(String pageName) {
        if (pageName == null)
            return;
        String cleanPage = pageName.trim();
        if (cleanPage.contains("Logout") || cleanPage.contains("Exit")) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText("Are you sure you want to log out?");
            alert.setContentText("You will be returned to the login screen.");
            alert.showAndWait().ifPresent(res -> {
                if (res == javafx.scene.control.ButtonType.OK && stage != null) {
                    try {
                        new Loginpage().show(stage);
                    } catch (Exception e) {
                        stage.close();
                    }
                }
            });
            return;
        }

        Parent pageRoot;
        if (cleanPage.contains("Billing") || cleanPage.contains("Bill")) {
            pageRoot = BillingPage.create(this);
            cleanPage = BILLING;
        } else if (cleanPage.contains("Product") || cleanPage.contains("Products")) {
            pageRoot = Product.create(this);
            cleanPage = PRODUCTS;
        } else if (cleanPage.contains("Customer") || cleanPage.contains("Customers")) {
            pageRoot = CustomerPage.create(this);
            cleanPage = CUSTOMERS;
        } else if (cleanPage.contains("Inventory")) {
            pageRoot = inventory.create(this);
            cleanPage = INVENTORY;
        } else if (cleanPage.contains("Udhari")) {
            pageRoot = UdhariPage.create(this);
            cleanPage = UDHARI;
        } else if (cleanPage.contains("Sales") || cleanPage.contains("Report") || cleanPage.contains("Reports")) {
            pageRoot = salesreport.create(this);
            cleanPage = REPORTS;
        } else if (cleanPage.contains("Profile")) {
            pageRoot = ProfilePage.create(this);
            cleanPage = PROFILE;
        } else if (cleanPage.contains("Help") || cleanPage.contains("Support")) {
            pageRoot = new ShopkeeperHelpSupport().getView();
            cleanPage = HELP_SUPPORT;
        } else if (cleanPage.contains("Notification") || cleanPage.contains("Notifications")
                || cleanPage.contains("Order")) {
            pageRoot = Notificationpage.create(this);
            cleanPage = NOTIFICATIONS;
        } else if (cleanPage.contains("Dashboard") || cleanPage.contains("Home")) {
            pageRoot = dashboardPage();
            cleanPage = DASHBOARD;
        } else {
            pageRoot = placeholderPage(cleanPage);
        }

        currentActivePage = cleanPage;
        if (navListView != null) {
            navListView.refresh();
        }
        if (activePageTitleLabel != null) {
            activePageTitleLabel.setText(cleanPage);
        }

        updateTopBarProfile();

        stage.setTitle("Smart Retail - " + cleanPage);

        if (pageRoot instanceof BorderPane bp) {
            bp.setLeft(null);
            bp.setTop(null);
        }

        if (mainRoot != null) {
            mainRoot.setCenter(pageRoot);
        } else if (stage.getScene() == null) {
            stage.setScene(new Scene(pageRoot, 1280, 800));
        } else {
            stage.getScene().setRoot(pageRoot);
        }
    }

    public void addToBasket(String name) {
        basket.merge(name, 1, Integer::sum);
    }

    public void changeQuantity(String name, int amount) {
        int quantity = basket.getOrDefault(name, 0) + amount;
        if (quantity <= 0)
            basket.remove(name);
        else
            basket.put(name, quantity);
    }

    public Map<String, Integer> getBasket() {
        return new LinkedHashMap<>(basket);
    }

    public List<String> getPurchaseHistory() {
        return List.copyOf(purchaseHistory);
    }

    public void saveCurrentBill() {
        if (!basket.isEmpty()) {
            purchaseHistory.add(0, "Bill #" + (1000 + purchaseHistory.size() + 1) + " — " + basketSummary());
        }
    }

    public String basketSummary() {
        if (basket.isEmpty())
            return "No products selected";
        return basket.entrySet().stream().map(e -> e.getKey() + " x" + e.getValue())
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    /** Shared shell used by pages wanting standard layout. */
    public BorderPane createLayout(String activePage, String title, Node content) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + Theme.BG_DARK + ";");

        ScrollPane scroll = new ScrollPane(content);
        Theme.applyScrollDarkStyle(scroll);
        root.setCenter(scroll);
        return root;
    }

    public VBox createSidebar(String activePage) {
        currentActivePage = activePage;
        if (navListView != null) {
            navListView.refresh();
        }
        if (staticSidebar == null) {
            staticSidebar = buildStaticSidebar();
        }
        return staticSidebar;
    }

    private Parent dashboardPage() {
        VBox body = new VBox(22);
        body.setPadding(new Insets(26, 30, 30, 30));

        com.eudhari.model.UserModel curUser = com.eudhari.controller.ProfileController.getInstance()
                .getCurrentUserProfile();
        String currentShopName = curUser != null && curUser.getShopName() != null && !curUser.getShopName().isBlank()
                ? curUser.getShopName()
                : (curUser != null && curUser.getName() != null ? curUser.getName() + "'s Store" : "Store");

        // Welcome Header
        Label welcomeTitle = new Label("Welcome back, " + currentShopName + "! 👋");
        welcomeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label welcomeSub = new Label(
                "Here is an overview of your shop status, inventory capacity, and pending customer requests.");
        welcomeSub.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:14px;");
        VBox welcomeBox = new VBox(5, welcomeTitle, welcomeSub);

        // 4 Key Metric Cards
        String cardStyle = Theme.STYLE_CARD;

        // Card 1: Active Products
        int activeCount = productStore.getActiveProducts().size();
        Label pIcon = new Label("📦");
        pIcon.setStyle("-fx-font-size:22px; -fx-background-color:" + Theme.SKY_BLUE_BG
                + "; -fx-padding:8 12; -fx-background-radius:10;");
        Label pTitle = new Label("TOTAL PRODUCTS");
        pTitle.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label pVal = new Label(String.valueOf(activeCount));
        pVal.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        pVal.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label pNote = new Label(activeCount + " Active items in store");
        pNote.setStyle("-fx-text-fill:#4ade80; -fx-font-size:12px;");
        VBox card1 = new VBox(10, new HBox(12, pIcon, pTitle), pVal, pNote);
        card1.setStyle(cardStyle);
        HBox.setHgrow(card1, Priority.ALWAYS);

        // Card 2: Storage Capacity (500 kg limit)
        double totalKg = productStore.getTotalStockKg();
        double pct = productStore.getStorageUsagePercentage();
        Label sIcon = new Label("⚖️");
        sIcon.setStyle("-fx-font-size:22px; -fx-background-color:#14382c; -fx-padding:8 12; -fx-background-radius:10;");
        Label sTitle = new Label("STORAGE CAPACITY");
        sTitle.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label sVal = new Label(String.format("%.1f / 500 kg", totalKg));
        sVal.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        sVal.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        ProgressBar capacityBar = new ProgressBar(pct / 100.0);
        capacityBar.setPrefWidth(220);
        capacityBar.setStyle(pct > 80 ? "-fx-accent:#f87171;" : "-fx-accent:" + Theme.SKY_BLUE + ";");

        Label sNote = new Label(String.format("%.1f%% capacity used", pct));
        sNote.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:12px;");
        VBox card2 = new VBox(8, new HBox(12, sIcon, sTitle), sVal, capacityBar, sNote);
        card2.setStyle(cardStyle);
        HBox.setHgrow(card2, Priority.ALWAYS);

        // Card 3: Pending Orders & Notifications
        com.eudhari.model.UserModel skUser = com.eudhari.controller.ProfileController.getInstance()
                .getCurrentUserProfile();
        String skUid = skUser != null && skUser.getUid() != null ? skUser.getUid() : "";
        int pendingConnCount = 0;
        int pendingOrderCount = 0;
        if (!skUid.isBlank()) {
            pendingConnCount = com.eudhari.controller.ConnectionRequestController.getInstance()
                    .getPendingRequestsForShopkeeper(skUid).size();
            List<com.eudhari.model.OrderModel> skOrders = com.eudhari.controller.OrderController.getInstance()
                    .getOrdersForShopkeeper(skUid);
            if (skOrders != null) {
                for (com.eudhari.model.OrderModel o : skOrders) {
                    if ("PENDING".equalsIgnoreCase(o.getStatus()))
                        pendingOrderCount++;
                }
            }
        }
        int totalPending = pendingConnCount + pendingOrderCount;

        Label nIcon = new Label("🔔");
        nIcon.setStyle("-fx-font-size:22px; -fx-background-color:" + Theme.WARM_BEIGE_BG
                + "; -fx-padding:8 12; -fx-background-radius:10;");
        Label nTitle = new Label("NOTIFICATIONS & ORDERS");
        nTitle.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label nVal = new Label(totalPending + " Pending");
        nVal.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nVal.setTextFill(Color.web(Theme.WARM_BROWN_TEXT));
        Button nLink = new Button("View Requests  ->");
        nLink.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.SKY_BLUE
                + "; -fx-font-weight:bold; -fx-cursor:hand;");
        nLink.setOnAction(e -> navigateTo(NOTIFICATIONS));
        VBox card3 = new VBox(8, new HBox(12, nIcon, nTitle), nVal, nLink);
        card3.setStyle(cardStyle);
        HBox.setHgrow(card3, Priority.ALWAYS);

        // Card 4: Sales Today
        double todaySales = com.eudhari.controller.shopkeppercontroller.SalesReportController.getInstance()
                .getTodaySales();
        Label salesIcon = new Label("💰");
        salesIcon.setStyle(
                "-fx-font-size:22px; -fx-background-color:#3b0764; -fx-padding:8 12; -fx-background-radius:10;");
        Label salesTitle = new Label("TODAY'S SALES");
        salesTitle.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");
        Label salesVal = new Label(String.format("₹%.2f", todaySales));
        salesVal.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        salesVal.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label salesNote = new Label("+12% compared to yesterday");
        salesNote.setStyle("-fx-text-fill:#4ade80; -fx-font-size:12px;");
        VBox card4 = new VBox(10, new HBox(12, salesIcon, salesTitle), salesVal, salesNote);
        card4.setStyle(cardStyle);
        HBox.setHgrow(card4, Priority.ALWAYS);

        HBox cardsRow = new HBox(18, card1, card2, card3, card4);

        // Quick Navigation Actions Section
        Label quickTitle = new Label("Quick Actions");
        quickTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        quickTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        HBox actionsRow = new HBox(14,
                action("🛍  Product Catalog", () -> navigateTo(PRODUCTS)),
                action("+ Add Product", () -> navigateTo(PRODUCTS)),
                action("🧾 Create Bill", () -> navigateTo(BILLING)),
                action("👥 Customers", () -> navigateTo(CUSTOMERS)),
                action("📦 Inventory & Capacity", () -> navigateTo(INVENTORY)),
                action("📊 Sales Reports", () -> navigateTo(REPORTS)));

        // Recent Orders & Storage Alert Banner
        Label bannerTitle = new Label("🔔 Active Customer Requests & Orders");
        bannerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        bannerTitle.setStyle("-fx-text-fill:" + Theme.TEXT_PRIMARY + ";");

        Label req1 = new Label("• Order #204: Manish Patil requested 5 kg Rice & 2 L Sunflower Oil");
        Label req2 = new Label("• Credit Alert: Aggarwal Store due payment reminder for Rs 2,500");
        Label req3 = new Label("• Inventory Notice: Cold storage space at 78% capacity");
        VBox reqList = new VBox(8, req1, req2, req3);
        reqList.setStyle("-fx-font-size:13px; -fx-text-fill:" + Theme.TEXT_SECONDARY + ";");

        Button viewAllNotifs = new Button("Open Notification Center");
        viewAllNotifs.setStyle("-fx-background-color:" + Theme.SKY_BLUE_DARK
                + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:10 18; -fx-cursor:hand;");
        viewAllNotifs.setOnAction(e -> navigateTo(NOTIFICATIONS));

        VBox notificationBanner = new VBox(14, bannerTitle, reqList, viewAllNotifs);
        notificationBanner.setPadding(new Insets(20));
        notificationBanner.setStyle(cardStyle);

        body.getChildren().addAll(welcomeBox, cardsRow, quickTitle, actionsRow, notificationBanner);
        return createLayout(DASHBOARD, DASHBOARD, body);
    }

    private Parent placeholderPage(String pageName) {
        VBox body = pageBody(pageName, "This section is active under the single-Stage navigation system.");
        return createLayout(pageName, pageName, body);
    }

    public VBox pageBody(String heading, String description) {
        Label h = new Label(heading);
        h.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + Theme.TEXT_PRIMARY + ";");
        Label d = new Label(description);
        d.setStyle("-fx-text-fill:" + Theme.TEXT_SECONDARY + "; -fx-font-size:14px;");
        VBox body = new VBox(16, h, d);
        body.setPadding(new Insets(28));
        return body;
    }

    public Button action(String text, Runnable action) {
        Button button = new Button(text);
        button.setCursor(Cursor.HAND);
        button.setStyle(
                "-fx-background-color:" + Theme.SKY_BLUE_DARK
                        + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:12 18; -fx-font-size:14px;");
        button.setOnAction(e -> action.run());
        return button;
    }

    public static HBox spacer() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
