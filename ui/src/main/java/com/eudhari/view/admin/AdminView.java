package com.eudhari.view.admin;

import com.eudhari.view.login.Loginpage;
import com.eudhari.controller.AdminController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminView {
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private StackPane mainContent;
    private ListView<String> sidebarListView;
    private Label topPageTitle;
    private Label topPageBreadcrumb;
    private AdminController controller;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String SIDEBAR_BG = "#0B192C";
    private static final String PRIMARY_COLOR = "#3A57E8";
    private static final String PRIMARY_HOVER = "#2D44C2";

    public static final String NAV_DASHBOARD = "🏠  Dashboard";
    public static final String NAV_SHOPKEEPERS = "👤  Shopkeepers";
    public static final String NAV_SHOPS = "🏪  Shops";
    public static final String NAV_CUSTOMERS = "👥  Customers";
    public static final String NAV_ANALYSIS = "📊  Transactions / Reports";
    public static final String NAV_COMPLAINTS = "🔔  Notifications / Complaints";
    public static final String NAV_PROFILE = "👤  Profile";
    public static final String NAV_LOGOUT = "🚪  Logout";

    public AdminView() {
        this.controller = new AdminController();
    }

    public AdminView(AdminController controller) {
        this.controller = controller;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;
        this.root = new BorderPane();
        this.mainContent = new StackPane();

        // 1. Build Constant Top Bar
        root.setTop(buildTopBar());

        // 2. Build Constant Sidebar (ListView)
        root.setLeft(buildSidebar());

        // 3. Set Dynamic Center Container
        root.setCenter(mainContent);

        // 4. Default Initial Navigation: Dashboard
        navigateTo(NAV_DASHBOARD);

        // 5. Create Single Scene and attach to Single Stage
        scene = new Scene(root, 1280, 800);
        stage.setTitle("Smart eUdhari - Fintech Admin Console");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
    }

    public Parent getView(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.mainContent = new StackPane();

        root.setTop(buildTopBar());
        root.setLeft(buildSidebar());
        root.setCenter(mainContent);

        navigateTo(NAV_DASHBOARD);
        return root;
    }

    // CONSTANT TOP BAR
    
    private HBox buildTopBar() {
        HBox topBar = new HBox(18);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setStyle("-fx-background-color: #1d3b8f; -fx-border-color: transparent transparent #E2E8F0 transparent; " +
                "-fx-border-width: 0 0 1 0; -fx-pref-height: 64px;");

        // Role Badge
        Label roleBadge = new Label("ADMIN");
        roleBadge.setStyle(FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-padding: 4px 10px; -fx-background-radius: 6px; -fx-border-color: #C7D2FE; -fx-border-radius: 6px;");

        // Page title & breadcrumb on top bar
        VBox titleBox = new VBox(2);
        topPageTitle = new Label("Admin Dashboard");
        topPageTitle.setStyle(FONT + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #f1f5ff;");

        topPageBreadcrumb = new Label("Smart eUdhari  /  Dashboard");
        topPageBreadcrumb.setStyle(FONT + "-fx-font-size: 11px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(topPageTitle, topPageBreadcrumb);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle(FONT + "-fx-background-color: #F8FAFC; -fx-background-radius: 50%; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 50%; -fx-min-width: 36px; -fx-min-height: 36px; -fx-font-size: 13px; -fx-cursor: hand;");
        notifBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No new unread platform notifications.", ButtonType.OK);
            alert.showAndWait();
        });

        // Admin Avatar Badge & Details
        com.eudhari.model.UserModel currentAdmin = com.eudhari.config.UserSession.getInstance().getCurrentUser();
        String displayAdminName = currentAdmin != null && currentAdmin.getName() != null && !currentAdmin.getName().isBlank() ? currentAdmin.getName() : "Admin Console";
        String displayAdminRole = currentAdmin != null && currentAdmin.getRole() != null && !currentAdmin.getRole().isBlank() ? currentAdmin.getRole() : "System Administrator";
        String adminInitials = displayAdminName.length() >= 2 ? displayAdminName.substring(0, 2).toUpperCase() : "AD";

        Label avatar = new Label(adminInitials);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(36, 36);
        avatar.setMinSize(36, 36);
        avatar.setStyle(FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-background-radius: 50%; -fx-border-color: #C7D2FE; -fx-border-radius: 50%;");

        VBox adminInfo = new VBox(1);
        Label adminName = new Label(displayAdminName);
        adminName.setStyle(FONT + "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e2e6f0;");
        Label adminRole = new Label(displayAdminRole);
        adminRole.setStyle(FONT + "-fx-font-size: 10px; -fx-text-fill: #64748B;");
        adminInfo.getChildren().addAll(adminName, adminRole);

        HBox profileBox = new HBox(8, avatar, adminInfo);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setStyle("-fx-cursor: hand;");
        profileBox.setOnMouseClicked(e -> {
            sidebarListView.getSelectionModel().select(NAV_PROFILE);
            navigateTo(NAV_PROFILE);
        });

        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox("#3A57E8", "-fx-background-color: #F8FAFC; -fx-padding: 6 12; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        topBar.getChildren().addAll(roleBadge, titleBox, spacer, clockWidget, notifBtn, profileBox);
        return topBar;
    }

    // CONSTANT SIDEBAR (LISTVIEW)
    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(20, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + "; " +
                "-fx-border-color: transparent #122f58 transparent transparent; -fx-border-width: 0 1 0 0;");

        // Brand Header
        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 14, 8));

        Label brandIcon = new Label("🏛");
        brandIcon.setAlignment(Pos.CENTER);
        brandIcon.setPrefSize(38, 38);
        brandIcon.setStyle(FONT + "-fx-background-color: rgba(58, 87, 232, 0.2); -fx-text-fill: #60A5FA; " +
                "-fx-font-size: 18px; -fx-background-radius: 8px;");

        VBox brandText = new VBox(1);
        Label brandTitle = new Label("Smart eUdhari");
        brandTitle.setStyle(FONT + "-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label brandSubtitle = new Label("Fintech Admin");
        brandSubtitle.setStyle(FONT + "-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        brandText.getChildren().addAll(brandTitle, brandSubtitle);

        brandBox.getChildren().addAll(brandIcon, brandText);
        sidebar.getChildren().add(brandBox);

        // Sidebar Navigation ListView
        ObservableList<String> navItems = FXCollections.observableArrayList(
                NAV_DASHBOARD,
                NAV_SHOPKEEPERS,
                NAV_SHOPS,
                NAV_CUSTOMERS,
                NAV_COMPLAINTS,
                NAV_PROFILE,
                NAV_LOGOUT
        );

        sidebarListView = new ListView<>(navItems);
        sidebarListView.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        VBox.setVgrow(sidebarListView, Priority.ALWAYS);

        sidebarListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    boolean isLogout = item.equals(NAV_LOGOUT);

                    if (isSelected()) {
                        setStyle(FONT + "-fx-background-color: #3A57E8; -fx-text-fill: #FFFFFF; " +
                                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else if (isLogout) {
                        setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #F87171; " +
                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else {
                        setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    }

                    setOnMouseEntered(e -> {
                        if (!isSelected()) {
                            if (isLogout) {
                                setStyle(FONT + "-fx-background-color: #3F1D1D; -fx-text-fill: #EF4444; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle(FONT + "-fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            if (isLogout) {
                                setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #F87171; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle(FONT + "-fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });
                }
            }
        });

        // ListView Navigation Listener
        sidebarListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                navigateTo(newVal);
            }
        });

        // Pre-select Dashboard
        sidebarListView.getSelectionModel().select(0);
        sidebar.getChildren().add(sidebarListView);

        // Support Pill Button
        Button supportBtn = new Button("🎧  Support");
        supportBtn.setMaxWidth(Double.MAX_VALUE);
        supportBtn.setStyle(FONT + "-fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; " +
                "-fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
        supportBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Smart eUdhari Support Desk\n24/7 Helpline: 1800-123-UDHARI\nEmail: support@eudhari.com", ButtonType.OK);
            alert.showAndWait();
        });

        sidebar.getChildren().add(supportBtn);
        return sidebar;
    }

    // COMPLETE NAVIGATION IMPLEMENTATION USING RUNNABLES
    // ONLY mainContent.getChildren().setAll(...) CHANGES
    public void navigateTo(String page) {
        if (page == null) return;

        // Keep sidebar selection synced
        if (sidebarListView != null && !page.equals(sidebarListView.getSelectionModel().getSelectedItem())) {
            sidebarListView.getSelectionModel().select(page);
        }

        Runnable goToShops = () -> navigateTo(NAV_SHOPS);
        Runnable goToCustomers = () -> navigateTo(NAV_CUSTOMERS);

        switch (page) {
            case NAV_DASHBOARD:
                mainContent.getChildren().setAll(new DashboardView(controller, goToShops, goToCustomers, null, null).getView());
                topPageTitle.setText("Admin Dashboard");
                topPageBreadcrumb.setText("Smart eUdhari  /  Dashboard");
                break;

            case NAV_SHOPKEEPERS:
                mainContent.getChildren().setAll(new ShopManagementView(controller, null).getView());
                topPageTitle.setText("Shopkeeper Management");
                topPageBreadcrumb.setText("Smart eUdhari  /  Shopkeepers");
                break;

            case NAV_SHOPS:
                mainContent.getChildren().setAll(new ShopsManagementView(controller, null).getView());
                topPageTitle.setText("Shops Management");
                topPageBreadcrumb.setText("Smart eUdhari  /  Shops");
                break;

            case NAV_CUSTOMERS:
                mainContent.getChildren().setAll(new CustomerManagementView(controller, null).getView());
                topPageTitle.setText("Customer Management");
                topPageBreadcrumb.setText("Smart eUdhari  /  Customers");
                break;

            case NAV_COMPLAINTS:
                mainContent.getChildren().setAll(new AdminComplaintsView(controller, null).getView());
                topPageTitle.setText("Notifications & Complaints Desk");
                topPageBreadcrumb.setText("Smart eUdhari  /  Complaints");
                break;

            case NAV_PROFILE:
                mainContent.getChildren().setAll(new AdminProfileView(controller, null).getView());
                topPageTitle.setText("Admin Profile & Settings");
                topPageBreadcrumb.setText("Smart eUdhari  /  Admin Profile");
                break;

            case NAV_LOGOUT:
                handleLogout();
                break;

            default:
                break;
        }
    }

    
    // LOGOUT FLOW (CONFIRMATION & CLEAN EXIT)
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout from Smart eUdhari Admin Console");
        alert.setContentText("Are you sure you want to logout?");

        ButtonType yesBtn = new ButtonType("Yes, Logout", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesBtn, cancelBtn);

        alert.showAndWait().ifPresent(res -> {
            if (res == yesBtn) {
                if (stage != null) {
                    try {
                        new Loginpage().show(stage);
                    } catch (Exception e) {
                        stage.close();
                    }
                }
            } else {
                // Stay on current page and reset selection
                sidebarListView.getSelectionModel().select(0);
            }
        });
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
}
