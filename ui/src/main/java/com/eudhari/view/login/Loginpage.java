package com.eudhari.view.login;

import com.eudhari.controller.AuthController;
import com.eudhari.controller.AuthResult;
import com.eudhari.view.customer.Homepage;
import com.eudhari.view.shopkepper.dashboard;
import com.eudhari.view.admin.AdminView;

import javax.swing.Icon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Loginpage extends Application {

        public static Stage loginStage;
        private Scene loginScene;
        private String selectedRole = "Shopkeeper";
        private final AuthController authController = new AuthController();

        private void navigateToRole(String role) {
                try {
                        if ("Admin".equalsIgnoreCase(role)) {
                                new AdminView().show(loginStage);
                        } else if ("Shopkeeper".equalsIgnoreCase(role)) {
                                new dashboard().show(loginStage);
                        } else if ("Customer".equalsIgnoreCase(role)) {
                                new Homepage().show(loginStage);
                        }
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        public void show(Stage myStage) {
                try {
                        start(myStage);
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        @Override
        public void start(Stage myStage) throws Exception {

                loginStage = myStage;

                // ROOT
                BorderPane root = new BorderPane();
                root.setStyle("-fx-background-color: #06152b;");

                // LEFT SIDE - RETAIL IMAGE / BRANDING
                StackPane leftSide = new StackPane();
                leftSide.setPrefWidth(700);
                leftSide.setMinWidth(380);

                java.net.URL bgUrl = getClass().getResource("/assets/images/ecd3a983-49c0-415b-9478-c415d2e11519.png");
                if (bgUrl == null) {
                        bgUrl = getClass().getResource("/assets/images/me.jpg");
                }
                if (bgUrl == null) {
                        bgUrl = getClass().getResource("/assets/image/ecd3a983-49c0-415b-9478-c415d2e11519.png");
                }

                if (bgUrl != null) {
                        Image retailImage = new Image(bgUrl.toExternalForm());
                        ImageView retailImageView = new ImageView(retailImage);
                        retailImageView.setPreserveRatio(false);
                        retailImageView.fitWidthProperty().bind(leftSide.widthProperty());
                        retailImageView.fitHeightProperty().bind(leftSide.heightProperty());
                        leftSide.getChildren().add(retailImageView);
                }

                // Dark overlay to make text readable
                Region imageOverlay = new Region();
                imageOverlay.setStyle(
                                "-fx-background-color: linear-gradient(" +
                                                "to right, rgba(0,0,0,0.55), rgba(0,0,0,0.20));");
                imageOverlay.prefWidthProperty().bind(leftSide.widthProperty());
                imageOverlay.prefHeightProperty().bind(leftSide.heightProperty());

                // LEFT BRANDING CONTENT
                VBox brandContent = new VBox(18);
                brandContent.setAlignment(Pos.CENTER_LEFT);
                brandContent.setMaxWidth(470);
                brandContent.setPadding(new Insets(40, 40, 40, 30));
                brandContent.setTranslateX(110);

                Label brandTitle = new Label("Smart Retail\nManagement System");
                brandTitle.setFont(Font.font("Arial", FontWeight.BOLD, 43));
                brandTitle.setTextFill(Color.WHITE);
                brandTitle.setWrapText(true);
                brandTitle.setTextAlignment(TextAlignment.LEFT);
                brandTitle.setMaxWidth(440);

                // Blue underline
                Region blueLine = new Region();
                blueLine.setPrefSize(110, 6);
                blueLine.setMaxSize(110, 6);
                blueLine.setStyle(
                                "-fx-background-color: linear-gradient(to right, #2196f3, #5bc0ff);" +
                                                "-fx-background-radius: 8;");

                Label brandSubtitle = new Label("Manage. Monitor. Grow.");
                brandSubtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 19));
                brandSubtitle.setTextFill(Color.WHITE);
                brandSubtitle.setPadding(new Insets(0, 0, 8, 0));

                VBox features = new VBox(14);
                features.getChildren().addAll(
                                createFeature("◉", "Real-time Insights"),
                                createFeature("▣", "Inventory Control"),
                                createFeature("♙", "Sales Tracking"));

                brandContent.getChildren().addAll(
                                brandTitle,
                                blueLine,
                                brandSubtitle,
                                features);

                StackPane.setAlignment(brandContent, Pos.CENTER_RIGHT);
                StackPane.setMargin(brandContent, new Insets(0, 48, 0, 0));
                leftSide.getChildren().addAll(
                                imageOverlay,
                                brandContent);

                // RIGHT SIDE - LOGIN PANEL
                StackPane rightSide = new StackPane();
                rightSide.setStyle("-fx-background-color: #06152b;");
                rightSide.setPadding(new Insets(45));

                VBox loginCard = new VBox(18);
                loginCard.setMaxWidth(565);
                loginCard.setMaxHeight(700);
                loginCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                loginCard.setAlignment(Pos.TOP_CENTER);
                loginCard.setPadding(new Insets(35, 40, 30, 40));

                loginCard.setStyle(
                                "-fx-background-color: linear-gradient(to bottom, #172b47, #07192e);" +
                                                "-fx-background-radius: 28;" +
                                                "-fx-border-color: #6c86a6;" +
                                                "-fx-border-width: 1;" +
                                                "-fx-border-radius: 28;");

                DropShadow cardShadow = new DropShadow();
                cardShadow.setColor(Color.rgb(0, 0, 0, 0.45));
                cardShadow.setRadius(30);
                cardShadow.setOffsetY(12);
                loginCard.setEffect(cardShadow);

                
                // CART LOGO
                
                StackPane logoBox = new StackPane();
                logoBox.setPrefSize(72, 72);
                logoBox.setMaxSize(72, 72);
                logoBox.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #1976f3, #0b55bd);" +
                                                "-fx-background-radius: 16;" +
                                                "-fx-border-color: #3289ff;" +
                                                "-fx-border-radius: 16;");

                java.io.InputStream logoStream = getClass()
                                .getResourceAsStream("/assets/icons/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
                if (logoStream == null) {
                        logoStream = getClass().getResourceAsStream(
                                        "/assets/images/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
                }
                if (logoStream == null) {
                        logoStream = getClass().getResourceAsStream(
                                        "/assets/icon/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
                }
                if (logoStream != null) {
                        Image logoImage = new Image(logoStream);
                        ImageView eUdhariIcon = new ImageView(logoImage);
                        eUdhariIcon.setFitWidth(80);
                        eUdhariIcon.setFitHeight(80);
                        eUdhariIcon.setPreserveRatio(true);
                        logoBox.getChildren().add(eUdhariIcon);
                }

                // WELCOME HEADER
                Label titleLabel = new Label("Welcome Back!");
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 34));
                titleLabel.setTextFill(Color.WHITE);

                // Blue "Back!" effect using TextFlow
                Text welcome = new Text("Welcome ");
                welcome.setFill(Color.WHITE);
                welcome.setFont(Font.font("Arial", FontWeight.BOLD, 34));

                Text back = new Text("Back!");
                back.setFill(Color.web("#2589ff"));
                back.setFont(Font.font("Arial", FontWeight.BOLD, 34));

                javafx.scene.text.TextFlow welcomeFlow = new javafx.scene.text.TextFlow(welcome, back);
                welcomeFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                Label subtitleLabel = new Label("Login to continue to your account");
                subtitleLabel.setFont(Font.font("Arial", 15));
                subtitleLabel.setTextFill(Color.web("#b4bfd0"));

                VBox header = new VBox(8, welcomeFlow, subtitleLabel);
                header.setAlignment(Pos.CENTER);

                // USERNAME
                TextField usernameField = new TextField();
                usernameField.setPromptText("Email");
                usernameField.setPrefHeight(55);
                usernameField.setStyle(inputStyle());

                // PASSWORD
                PasswordField passwordField = new PasswordField();
                passwordField.setPromptText("Password");
                passwordField.setPrefHeight(55);
                passwordField.setStyle(inputStyle());

                // REMEMBER / FORGOT PASSWORD
                CheckBox rememberMe = new CheckBox("Remember me");
                rememberMe.setTextFill(Color.web("#c5cfdd"));
                rememberMe.setStyle("-fx-font-size: 14px;");

                // Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
                // forgotPassword.setTextFill(Color.web("#2589ff"));
                // forgotPassword.setStyle(
                // "-fx-font-size: 14px;" +
                // "-fx-border-color: transparent;" +
                // "-fx-padding: 0;");

                Region optionSpacer = new Region();
                HBox.setHgrow(optionSpacer, Priority.ALWAYS);

                HBox optionsRow = new HBox(
                                10,
                                rememberMe,
                                optionSpacer);
                optionsRow.setAlignment(Pos.CENTER_LEFT);

                // LOGIN BUTTON
                Button loginBtn = new Button("Login  →");
                loginBtn.setMaxWidth(Double.MAX_VALUE);
                loginBtn.setPrefHeight(56);
                loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                loginBtn.setTextFill(Color.WHITE);
                loginBtn.setStyle(
                                "-fx-background-color: linear-gradient(to right, #247cf0, #2589ff);" +
                                                "-fx-background-radius: 10;" +
                                                "-fx-cursor: hand;");

                loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                                "-fx-background-color: #1268d8;" +
                                                "-fx-background-radius: 10;" +
                                                "-fx-cursor: hand;"));

                loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                                "-fx-background-color: linear-gradient(to right, #247cf0, #2589ff);" +
                                                "-fx-background-radius: 10;" +
                                                "-fx-cursor: hand;"));

                // OR DIVIDER
                Separator leftSeparator = new Separator();
                Separator rightSeparator = new Separator();

                HBox.setHgrow(leftSeparator, Priority.ALWAYS);
                HBox.setHgrow(rightSeparator, Priority.ALWAYS);

                Label orLabel = new Label("OR");
                orLabel.setTextFill(Color.web("#aeb9c9"));
                orLabel.setFont(Font.font("Arial", 13));

                HBox divider = new HBox(12, leftSeparator, orLabel, rightSeparator);
                divider.setAlignment(Pos.CENTER);

                // ROLE LOGIN BUTTONS
                Button adminBtn = createRoleButton(
                                "◈", "Admin", "System Administrator", "#2589ff");

                Button shopkeeperBtn = createRoleButton(
                                "●", "Shopkeeper", "Manage Store & Inventory", "#16b88a");

                Button customerBtn = createRoleButton(
                                "▣", "Customer", "Billing & Purchase", "#ff9418");

                adminBtn.setOnAction(e -> {
                        selectedRole = "Admin";
                        highlightRoleButton(adminBtn, "#2589ff", shopkeeperBtn, customerBtn);
                });
                shopkeeperBtn.setOnAction(e -> {
                        selectedRole = "Shopkeeper";
                        highlightRoleButton(shopkeeperBtn, "#16b88a", adminBtn, customerBtn);
                });
                customerBtn.setOnAction(e -> {
                        selectedRole = "Customer";
                        highlightRoleButton(customerBtn, "#ff9418", adminBtn, shopkeeperBtn);
                });

                loginBtn.setOnAction(e -> {
                        String email = usernameField.getText() != null ? usernameField.getText().trim() : "";
                        String password = passwordField.getText() != null ? passwordField.getText() : "";

                        if (email.isEmpty() || password.isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Login Error");
                                alert.setHeaderText(null);
                                alert.setContentText("Please enter both email and password.");
                                alert.showAndWait();
                                return;
                        }

                        loginBtn.setDisable(true);
                        loginBtn.setText("Logging in...");

                        new Thread(() -> {
                                AuthResult result = authController.loginUser(email, password, selectedRole);
                                Platform.runLater(() -> {
                                        loginBtn.setDisable(false);
                                        loginBtn.setText("Login  →");
                                        if (result.isSuccess()) {
                                                String role = result.getUser() != null ? result.getUser().getRole()
                                                                : selectedRole;
                                                navigateToRole(role);
                                        } else {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Login Failed");
                                                alert.setHeaderText(null);
                                                alert.setContentText(result.getMessage());
                                                alert.showAndWait();
                                        }
                                });
                        }).start();
                });

                // SIGN UP BUTTON
                
                Button createAccountTab = new Button("Create Account");
                createAccountTab.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: #2589ff;" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-border-color: transparent;" +
                                                "-fx-cursor: hand;");

                createAccountTab.setOnAction(event -> {
                        System.out.println("Go to Sign Up Page");

                        Runnable callBackAction = this::backtologinpage;

                        Signuppage signupPage = new Signuppage(callBackAction);

                        loginStage.setScene(signupPage.getScene());
                        loginStage.setTitle("Smart eUdhari - Sign Up");
                });

                
                // ADD ALL LOGIN CONTENT
                loginCard.getChildren().addAll(
                                logoBox,
                                header,
                                usernameField,
                                passwordField,
                                optionsRow,
                                loginBtn,
                                divider,
                                adminBtn,
                                shopkeeperBtn,
                                customerBtn,
                                createAccountTab);

                rightSide.getChildren().add(loginCard);
                StackPane.setAlignment(loginCard, Pos.CENTER);

                
                // FINAL BORDERPANE LAYOUT
                root.setLeft(leftSide);
                root.setCenter(rightSide);

                // 55% image + 45% login panel
                leftSide.prefWidthProperty().bind(root.widthProperty().multiply(0.55));
                rightSide.prefWidthProperty().bind(root.widthProperty().multiply(0.45));

                // SCENE / STAGE
                double sceneWidth = 1200;
                double sceneHeight = 720;

                Scene scene = new Scene(root, sceneWidth, sceneHeight);
                loginScene = scene;

                myStage.setMinWidth(1800);
                myStage.setMinHeight(1000);
                myStage.setWidth(sceneWidth);
                myStage.setHeight(sceneHeight);
                myStage.setTitle("Smart Retail - Login");
                myStage.setScene(scene);
                myStage.centerOnScreen();
                myStage.show();
        }

        // FEATURE ITEM
        private HBox createFeature(String icon, String text) {

                Label iconLabel = new Label(icon);
                iconLabel.setPrefSize(36, 36);
                iconLabel.setAlignment(Pos.CENTER);
                iconLabel.setTextFill(Color.WHITE);
                iconLabel.setStyle(
                                "-fx-background-color: #2388f7;" +
                                                "-fx-background-radius: 8;" +
                                                "-fx-font-size: 16px;");

                Label textLabel = new Label(text);
                textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                textLabel.setTextFill(Color.WHITE);

                HBox box = new HBox(14, iconLabel, textLabel);
                box.setAlignment(Pos.CENTER_LEFT);

                return box;
        }

        // ROLE BUTTON
        private Button createRoleButton(
                        String icon,
                        String title,
                        String subtitle,
                        String iconColor) {

                Label iconLabel = new Label(icon);
                iconLabel.setPrefSize(40, 40);
                iconLabel.setMinSize(40, 40);
                iconLabel.setAlignment(Pos.CENTER);
                iconLabel.setTextFill(Color.WHITE);
                iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                iconLabel.setStyle(
                                "-fx-background-color: " + iconColor + ";" +
                                                "-fx-background-radius: 8;");

                Label titleLabel = new Label(title);
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                titleLabel.setTextFill(Color.WHITE);

                Label subtitleLabel = new Label(subtitle);
                subtitleLabel.setFont(Font.font("Arial", 12));
                subtitleLabel.setTextFill(Color.web("#aab7c8"));

                VBox textBox = new VBox(2, titleLabel, subtitleLabel);

                Label arrow = new Label("→");
                arrow.setFont(Font.font("Arial", 25));
                arrow.setTextFill(Color.web("#b9c4d3"));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox content = new HBox(
                                14,
                                iconLabel,
                                textBox,
                                spacer,
                                arrow);
                content.setAlignment(Pos.CENTER_LEFT);

                Button button = new Button();
                button.setGraphic(content);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setPrefHeight(68);

                button.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.055);" +
                                                "-fx-border-color: rgba(150,170,200,0.35);" +
                                                "-fx-border-radius: 10;" +
                                                "-fx-background-radius: 10;" +
                                                "-fx-padding: 8 14;" +
                                                "-fx-cursor: hand;");

                return button;
        }
        
        // INPUT FIELD STYLE
        private String inputStyle() {

                return "-fx-background-color: rgba(255,255,255,0.08);" +
                                "-fx-text-fill: white;" +
                                "-fx-prompt-text-fill: #aeb9c9;" +
                                "-fx-border-color: rgba(160,180,205,0.35);" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 0 16;" +
                                "-fx-font-size: 15px;";
        }

        private void highlightRoleButton(Button selectedBtn, String hexColor, Button other1, Button other2) {
                String activeStyle = "-fx-background-color: " + hexColor + "33;" +
                                "-fx-border-color: " + hexColor + ";" +
                                "-fx-border-width: 2px;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 8 14;" +
                                "-fx-cursor: hand;";

                DropShadow glow = new DropShadow();
                glow.setColor(Color.web(hexColor, 0.8));
                glow.setRadius(20);
                glow.setSpread(0.25);

                selectedBtn.setStyle(activeStyle);
                selectedBtn.setEffect(glow);

                String defaultStyle = "-fx-background-color: rgba(255,255,255,0.055);" +
                                "-fx-border-color: rgba(150,170,200,0.35);" +
                                "-fx-border-width: 1px;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 8 14;" +
                                "-fx-cursor: hand;";

                other1.setStyle(defaultStyle);
                other1.setEffect(null);
                other2.setStyle(defaultStyle);
                other2.setEffect(null);
        }

        // RETURN TO LOGIN PAGE
        
        public void backtologinpage() {
                loginStage.setScene(loginScene);
        }

}