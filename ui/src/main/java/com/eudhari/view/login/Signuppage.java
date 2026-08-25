package com.eudhari.view.login;
import com.eudhari.controller.AuthController;
import com.eudhari.controller.AuthResult;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Signuppage{

    private final BorderPane root;
    private final Scene scene;
    private final Runnable callBackAction;
    private final AuthController authController = new AuthController();


    // COLORS

    private static final String BACKGROUND = "#0B1326";
    private static final String SURFACE = "#171F33";
    private static final String SURFACE_LOW = "#131B2E";
    private static final String SURFACE_HIGH = "#222A3D";
    private static final String PRIMARY = "#A6C8FF";
    private static final String PRIMARY_CONTAINER = "#3192FC";
    private static final String SECONDARY = "#4EDEA3";
    private static final String TEXT = "#DAE2FD";
    private static final String TEXT_SECONDARY = "#C0C7D5";
    private static final String OUTLINE = "#404753";

    // FORM FIELDS

    private TextField fullNameField;
    private TextField mobileField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label selectedAccountLabel;

    // Shopkeeper Details
    private VBox shopkeeperDetailsBox;
    private TextField ownerNameField;
    private TextField shopNameField;
    private TextField shopAddressField;
    private TextField gpayIdField;
    private ComboBox<String> businessCategoryCombo;
    private TextField otherCategoryField;
    private VBox otherCategoryContainer;
    private Label storeImageLabel;
    private File selectedImageFile;

    private boolean customerSelected = true;

    // CONSTRUCTOR

    public Signuppage(Runnable callBackAction) {

        this.callBackAction = callBackAction;

        root = new BorderPane();
        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        // Create complete UI
        createUI();

        scene = new Scene(root, 1200, 720);

        // Make scene responsive
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveLayout();
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveLayout();
        });
    }

    // CREATE UI

    private void createUI() {

        HBox mainContainer = new HBox();

        mainContainer.setMaxWidth(1200);
        mainContainer.setMaxHeight(720);

        mainContainer.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + OUTLINE + ";" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1;"
        );

        // LEFT PANEL
        VBox leftPanel = createLeftPanel();

        // RIGHT PANEL
        VBox rightPanel = createRightPanel();

        HBox.setHgrow(leftPanel, Priority.SOMETIMES);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        mainContainer.getChildren().addAll(
                leftPanel,
                rightPanel
        );

        StackPane wrapper = new StackPane(mainContainer);

        wrapper.setPadding(new Insets(24));

        wrapper.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        root.setCenter(wrapper);
        }
        
    // LEFT PANEL
    

    private VBox createLeftPanel() {

        VBox leftPanel = new VBox();

        leftPanel.setPadding(new Insets(40));
        leftPanel.setSpacing(30);

        leftPanel.setPrefWidth(460);

        leftPanel.setStyle(
                "-fx-background-color: " + SURFACE_LOW + ";"
        );

        // BRANDING

        HBox branding = new HBox(10);
        branding.setAlignment(Pos.CENTER_LEFT);



        StackPane logoBox = new StackPane();
        logoBox.setPrefSize(72, 72);
        logoBox.setMaxSize(72, 72);
        logoBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1976f3, #0b55bd);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #3289ff;" +
                "-fx-border-radius: 16;"
        );
        java.io.InputStream logoStream = getClass().getResourceAsStream("/assets/icons/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
        if (logoStream == null) {
            logoStream = getClass().getResourceAsStream("/assets/images/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
        }
        if (logoStream == null) {
            logoStream = getClass().getResourceAsStream("/assets/icon/WhatsApp Image 2026-07-30 at 12.15.48 AM.jpeg");
        }
        if (logoStream != null) {
            Image logoImage = new Image(logoStream);
            ImageView eUdhariIcon = new ImageView(logoImage);
            eUdhariIcon.setFitWidth(80);
            eUdhariIcon.setFitHeight(80);
            eUdhariIcon.setPreserveRatio(true);
            logoBox.getChildren().add(eUdhariIcon);
        }
        
        Label brandName = new Label("Smart eUdhari");

        brandName.setFont(Font.font("Hanken Grotesk", FontWeight.BOLD, 22));
        brandName.setTextFill(Color.web(PRIMARY));

        branding.getChildren().addAll(
                logoBox,

                brandName
        );
        

        // MESSAGE

        VBox messageBox = new VBox(8);

        Label title = new Label("Create Your Account");

        title.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                32
        ));

        title.setTextFill(Color.web(TEXT));

        title.setWrapText(true);

        Label description = new Label(
                "Join Smart eUdhari and manage your Udhaar digitally."
        );

        description.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.NORMAL,
                16
        ));

        description.setTextFill(Color.web(TEXT_SECONDARY));

        description.setWrapText(true);

        messageBox.getChildren().addAll(
                title,
                description
        );

        // ACCOUNT TYPE

        VBox accountTypeBox = new VBox(15);

        Label accountTitle = new Label("Select Account Type");

        accountTitle.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                14
        ));

        accountTitle.setTextFill(Color.web(TEXT));

        HBox roleButtons = new HBox(15);

        Button customerBtn = createRoleButton(
                "👤",
                "Customer",
                true
        );

        Button shopkeeperBtn = createRoleButton(
                "🏪",
                "Shopkeeper",
                false
        );

        customerBtn.setOnAction(e -> {

            customerSelected = true;

            updateRoleButton(
                    customerBtn,
                    true,
                    "👤"
            );

            updateRoleButton(
                    shopkeeperBtn,
                    false,
                    "🏪"
            );

            updateSelectedLabel();
            updateShopkeeperDetailsVisibility();
        });

        shopkeeperBtn.setOnAction(e -> {

            customerSelected = false;

            updateRoleButton(
                    customerBtn,
                    false,
                    "👤"
            );

            updateRoleButton(
                    shopkeeperBtn,
                    true,
                    "🏪"
            );

            updateSelectedLabel();
            updateShopkeeperDetailsVisibility();
        });

        HBox.setHgrow(customerBtn, Priority.ALWAYS);
        HBox.setHgrow(shopkeeperBtn, Priority.ALWAYS);

        roleButtons.getChildren().addAll(
                customerBtn,
                shopkeeperBtn
        );

        selectedAccountLabel = new Label();
        selectedAccountLabel.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                12
        ));
        selectedAccountLabel.setTextFill(Color.web(PRIMARY));
        selectedAccountLabel.setId("selectedLabel");

        updateSelectedLabel();

        accountTypeBox.getChildren().addAll(
                accountTitle,
                roleButtons,
                selectedAccountLabel
        );

        // QR ICON

        Region spacer = new Region();

        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label qrIcon = createIcon("qr_code_scanner");

        qrIcon.setStyle(
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-opacity: 0.15;" +
                "-fx-font-size: 110px;"
        );

        leftPanel.getChildren().addAll(
                branding,
                messageBox,
                accountTypeBox,
                spacer,
                qrIcon
        );

        return leftPanel;
    }

    // RIGHT PANEL

    private VBox createRightPanel() {

        VBox rightPanel = new VBox();

        rightPanel.setPadding(new Insets(40));

        rightPanel.setAlignment(Pos.CENTER);

        rightPanel.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        VBox formContainer = new VBox(20);

        formContainer.setMaxWidth(500);

        // FORM HEADER

        VBox header = new VBox(5);

        Label title = new Label("Create Your Account");

        title.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                24
        ));

        title.setTextFill(Color.web(TEXT));

        Label subtitle = new Label(
                "Enter your details to create your account."
        );

        subtitle.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.NORMAL,
                14
        ));

        subtitle.setTextFill(Color.web(TEXT_SECONDARY));

        header.getChildren().addAll(
                title,
                subtitle
        );

        // FORM

        VBox form = new VBox(16);

        // Full Name
        fullNameField = createTextField(
                "Full Name",
                "Enter your full name",
                "badge"
        );

        // Mobile Number
        mobileField = createTextField(
                "Mobile Number",
                "Enter your mobile number",
                "call"
        );

        // Email
        emailField = createTextField(
                "Email Address",
                "Enter your email",
                "mail"
        );

        // Password
        passwordField = createPasswordField(
                "Password",
                "Enter your password",
                "lock"
        );

        // Confirm Password
        confirmPasswordField = createPasswordField(
                "Confirm Password",
                "Re-enter your password",
                "lock_reset"
        );

        
        // SHOPKEEPER DETAILS (Conditional)

        shopkeeperDetailsBox = createShopkeeperDetailsBox();
        shopkeeperDetailsBox.setVisible(false);
        shopkeeperDetailsBox.setManaged(false);

        // TERMS

        CheckBox termsCheckBox = new CheckBox();

        termsCheckBox.setSelected(false);

        termsCheckBox.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        Label termsText = new Label(
                "I agree to the Terms & Conditions and Privacy Policy."
        );

        termsText.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.NORMAL,
                13
        ));

        termsText.setTextFill(Color.web(TEXT_SECONDARY));

        termsText.setWrapText(true);

        HBox termsBox = new HBox(10);

        termsBox.setAlignment(Pos.TOP_LEFT);

        termsBox.getChildren().addAll(
                termsCheckBox,
                termsText
        );

        // CREATE ACCOUNT BUTTON

        Button createAccountBtn = new Button(
                "Create Account   →"
        );

        createAccountBtn.setMaxWidth(Double.MAX_VALUE);
        createAccountBtn.setPrefHeight(48);

        createAccountBtn.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                14
        ));

        createAccountBtn.setTextFill(Color.web("#00315F"));

        createAccountBtn.setStyle(
                "-fx-background-color: " + PRIMARY_CONTAINER + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        createAccountBtn.setOnMouseEntered(e -> {

            createAccountBtn.setStyle(
                    "-fx-background-color: #5AA8FF;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        });

        createAccountBtn.setOnMouseExited(e -> {

            createAccountBtn.setStyle(
                    "-fx-background-color: " + PRIMARY_CONTAINER + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        });

        createAccountBtn.setOnAction(e -> {

            if (!validateForm(termsCheckBox)) {
                return;
            }

            String name = fullNameField.getText() != null ? fullNameField.getText().trim() : "";
            String mobile = mobileField.getText() != null ? mobileField.getText().trim() : "";
            String email = emailField.getText() != null ? emailField.getText().trim() : "";
            String password = passwordField.getText();

            String role = customerSelected ? "customer" : "shopkeeper";

            Map<String, Object> extraFields = new HashMap<>();
            if (!customerSelected) {
                if (ownerNameField != null && ownerNameField.getText() != null)
                    extraFields.put("ownerName", ownerNameField.getText().trim());
                if (shopNameField != null && shopNameField.getText() != null)
                    extraFields.put("shopName", shopNameField.getText().trim());
                if (shopAddressField != null && shopAddressField.getText() != null)
                    extraFields.put("shopAddress", shopAddressField.getText().trim());
                if (gpayIdField != null && gpayIdField.getText() != null)
                    extraFields.put("gpayId", gpayIdField.getText().trim());
                if (businessCategoryCombo != null && businessCategoryCombo.getValue() != null) {
                    String category = businessCategoryCombo.getValue();
                    if ("Other".equals(category) && otherCategoryField != null && otherCategoryField.getText() != null) {
                        category = otherCategoryField.getText().trim();
                    }
                    extraFields.put("businessCategory", category);
                }
                if (selectedImageFile != null) {
                    extraFields.put("storeImagePath", selectedImageFile.getAbsolutePath());
                }
            }

            createAccountBtn.setDisable(true);
            createAccountBtn.setText("Creating Account...");

            new Thread(() -> {
                AuthResult result = authController.registerUser(email, password, name, mobile, role, extraFields);
                Platform.runLater(() -> {
                    createAccountBtn.setDisable(false);
                    createAccountBtn.setText("Create Account   →");
                    if (result.isSuccess()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Account Created");
                        alert.setHeaderText(null);
                        alert.setContentText("Account created successfully!");
                        alert.showAndWait();

                        Stage currentStage = (Stage) root.getScene().getWindow();
                        if (currentStage != null) {
                            try {
                                if ("shopkeeper".equalsIgnoreCase(role)) {
                                    new com.eudhari.view.shopkepper.dashboard().show(currentStage);
                                } else {
                                    new com.eudhari.view.customer.Homepage().show(currentStage);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Registration Failed");
                        alert.setHeaderText(null);
                        alert.setContentText(result.getMessage());
                        alert.showAndWait();
                    }
                });
            }).start();
        });


        form.getChildren().addAll(
                fullNameField,
                mobileField,
                emailField,
                passwordField,
                confirmPasswordField,
                shopkeeperDetailsBox,
                termsBox,
                createAccountBtn
        );

        // LOGIN FOOTER

        Separator separator = new Separator();

        separator.setStyle(
                "-fx-background-color: " + OUTLINE + ";"
        );

        HBox loginBox = new HBox(5);

        loginBox.setAlignment(Pos.CENTER);

        Label alreadyLabel = new Label(
                "Already have an account?"
        );

        alreadyLabel.setTextFill(
                Color.web(TEXT_SECONDARY)
        );

        alreadyLabel.setFont(
                Font.font("Hanken Grotesk", 14)
        );

        Button loginButton = new Button("Login");

        loginButton.setBackground(
                Background.EMPTY
        );

        loginButton.setBorder(
                Border.EMPTY
        );

        loginButton.setFont(
                Font.font(
                        "Hanken Grotesk",
                        FontWeight.BOLD,
                        14
                )
        );

        loginButton.setTextFill(
                Color.web(PRIMARY)
        );

        loginButton.setCursor(
                javafx.scene.Cursor.HAND
        );

        loginButton.setOnAction(event -> {
            if (callBackAction != null) {
                callBackAction.run();
            }
        });

        loginBox.getChildren().addAll(
                alreadyLabel,
                loginButton
        );

        formContainer.getChildren().addAll(
                header,
                form,
                separator,
                loginBox
        );

        // SCROLL PANE FOR DETAILS

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                "-fx-background: " + BACKGROUND + ";" +
                "-fx-control-inner-background: " + BACKGROUND + ";" +
                "-fx-padding: 0;"
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rightPanel.getChildren().add(
                scrollPane
        );

        return rightPanel;
    }

    // TEXT FIELD

    private TextField createTextField(
            String labelText,
            String prompt,
            String icon
    ) {

        VBox container = new VBox(5);

        Label label = new Label(labelText);

        label.setFont(
                Font.font(
                        "Hanken Grotesk",
                        FontWeight.BOLD,
                        12
                )
        );

        label.setTextFill(
                Color.web(TEXT)
        );

        TextField field = new TextField();

        // IMPORTANT:
        // No pre-filled data
        field.clear();

        // Prompt text
        field.setPromptText(prompt);

        field.setPrefHeight(42);

        field.setMaxWidth(Double.MAX_VALUE);

        field.setFont(
                Font.font(
                        "Hanken Grotesk",
                        14
                )
        );

        field.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: #7F8798;" +
                "-fx-border-color: " + OUTLINE + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 12 0 12;"
        );

        container.getChildren().addAll(
                label,
                field
        );

        return field;
    }

    // PASSWORD FIELD

    private PasswordField createPasswordField(
            String labelText,
            String prompt,
            String icon
    ) {

        VBox container = new VBox(5);

        Label label = new Label(labelText);

        label.setFont(
                Font.font(
                        "Hanken Grotesk",
                        FontWeight.BOLD,
                        12
                )
        );

        label.setTextFill(
                Color.web(TEXT)
        );

        PasswordField field = new PasswordField();

        // Remove pre-filled password
        field.clear();

        // Prompt
        field.setPromptText(prompt);

        field.setPrefHeight(42);

        field.setMaxWidth(Double.MAX_VALUE);

        field.setFont(
                Font.font(
                        "Hanken Grotesk",
                        14
                )
        );

        field.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: #7F8798;" +
                "-fx-border-color: " + OUTLINE + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        container.getChildren().addAll(
                label,
                field
        );

        return field;
    }

    // SHOPKEEPER DETAILS

    private VBox createShopkeeperDetailsBox() {

        VBox detailsBox = new VBox(16);

        // SHOPKEEPER DETAILS TITLE

        Label detailsTitle = new Label("Shopkeeper Details");

        detailsTitle.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                14
        ));

        detailsTitle.setTextFill(Color.web(TEXT));

        // OWNER NAME

        ownerNameField = createTextField(
                "Owner Name",
                "Enter owner name",
                "person"
        );

        // SHOP NAME

        shopNameField = createTextField(
                "Shop Name",
                "Enter shop name",
                "storefront"
        );

        // SHOP ADDRESS

        shopAddressField = createTextField(
                "Shop Address",
                "Enter shop address",
                "location_on"
        );

        // GPAY ID

        gpayIdField = createTextField(
                "GPay ID",
                "Enter GPay ID (e.g. owner@okaxis)",
                "account_balance_wallet"
        );

        // BUSINESS CATEGORY

        VBox categoryContainer = new VBox(5);

        Label categoryLabel = new Label("Business Category / Type of Shop");

        categoryLabel.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                12
        ));

        categoryLabel.setTextFill(Color.web(TEXT));

        businessCategoryCombo = new ComboBox<>();

        businessCategoryCombo.getItems().addAll(
                "Grocery Store",
                "Clothing Shop",
                "Electronics & Mobiles",
                "Pharmacy / Medical",
                "Bakery & Sweets",
                "Hardware & Electrical",
                "Books & Stationery",
                "Other"
        );

        businessCategoryCombo.setPrefHeight(42);

        businessCategoryCombo.setMaxWidth(Double.MAX_VALUE);

        businessCategoryCombo.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-border-color: " + OUTLINE + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        categoryContainer.getChildren().addAll(
                categoryLabel,
                businessCategoryCombo
        );

        // Custom Category Field for "Other" option
        otherCategoryField = createTextField(
                "Specify Business Category / Type",
                "Enter your shop / business category",
                "storefront"
        );

        otherCategoryContainer = new VBox(otherCategoryField);
        otherCategoryContainer.setVisible(false);
        otherCategoryContainer.setManaged(false);

        businessCategoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isOther = "Other".equals(newVal);
            otherCategoryContainer.setVisible(isOther);
            otherCategoryContainer.setManaged(isOther);
        });

 // STORE IMAGE

        VBox imageContainer = new VBox(8);

        Label imageTitle = new Label("Store Image");

        imageTitle.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                14
        ));

        imageTitle.setTextFill(Color.web(TEXT));

        HBox imageBox = new HBox(10);

        imageBox.setAlignment(Pos.CENTER_LEFT);

        Button chooseImageBtn = new Button("Choose Store Image");

        chooseImageBtn.setPrefHeight(42);

        chooseImageBtn.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.BOLD,
                12
        ));

        chooseImageBtn.setTextFill(Color.web(PRIMARY));

        chooseImageBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + PRIMARY + ";" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-cursor: hand;"
        );

        chooseImageBtn.setOnMouseEntered(e -> {
            chooseImageBtn.setStyle(
                    "-fx-background-color: rgba(166,200,255,0.10);" +
                    "-fx-border-color: " + PRIMARY + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 8 16 8 16;" +
                    "-fx-cursor: hand;"
            );
        });

        chooseImageBtn.setOnMouseExited(e -> {
            chooseImageBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: " + PRIMARY + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 8 16 8 16;" +
                    "-fx-cursor: hand;"
            );
        });

        chooseImageBtn.setOnAction(e -> {
            handleImageSelection();
        });

        storeImageLabel = new Label("No image selected");

        storeImageLabel.setFont(Font.font(
                "Hanken Grotesk",
                FontWeight.NORMAL,
                12
        ));

        storeImageLabel.setTextFill(Color.web(TEXT_SECONDARY));

        imageBox.getChildren().addAll(
                chooseImageBtn,
                storeImageLabel
        );

        imageContainer.getChildren().addAll(
                imageTitle,
                imageBox
        );

        detailsBox.getChildren().addAll(
                detailsTitle,
                ownerNameField,
                shopNameField,
                shopAddressField,
                gpayIdField,
                categoryContainer,
                otherCategoryContainer,
                imageContainer
        );

        return detailsBox;
    }

    // ROLE BUTTON

    private Button createRoleButton(
            String icon,
            String text,
            boolean selected
    ) {

        VBox content = new VBox(7);

        content.setAlignment(Pos.CENTER);

        Label iconLabel = createIcon(icon);

        iconLabel.setStyle(
                "-fx-text-fill: " +
                        (selected ? PRIMARY : TEXT_SECONDARY) +
                        ";" +
                "-fx-font-size: 30px;"
        );

        Label textLabel = new Label(text);

        textLabel.setFont(
                Font.font(
                        "Hanken Grotesk",
                        FontWeight.BOLD,
                        13
                )
        );

        textLabel.setTextFill(
                Color.web(
                        selected
                                ? PRIMARY
                                : TEXT_SECONDARY
                )
        );

        content.getChildren().addAll(
                iconLabel,
                textLabel
        );

        Button button = new Button();

        button.setGraphic(content);

        button.setPrefHeight(110);

        button.setMaxWidth(Double.MAX_VALUE);

        updateRoleButton(
                button,
                selected,
                icon
        );

        return button;
    }

    // UPDATE ROLE BUTTON

    private void updateRoleButton(
            Button button,
            boolean selected,
            String icon
    ) {

        if (selected) {

            button.setStyle(
                    "-fx-background-color: rgba(166,200,255,0.10);" +
                    "-fx-border-color: " + PRIMARY + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: " + SURFACE + ";" +
                    "-fx-border-color: " + OUTLINE + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        }
    }

    // ICON

    private Label createIcon(String iconName) {

        Label icon = new Label();

        
        String symbol;

        switch (iconName) {

            case "account_balance_wallet":
                symbol = "▣";
                break;

            case "person":
                symbol = "●";
                break;

            case "storefront":
                symbol = "▤";
                break;

            case "qr_code_scanner":
                symbol = "▦";
                break;

            case "badge":
                symbol = "◆";
                break;

            case "call":
                symbol = "☎";
                break;

            case "mail":
                symbol = "✉";
                break;

            case "lock":
                symbol = "🔒";
                break;

            case "lock_reset":
                symbol = "🔐";
                break;

            default:
                symbol = "●";
        }

        icon.setText(symbol);

        icon.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        icon.setTextFill(
                Color.web(TEXT_SECONDARY)
        );

        return icon;
    }

    // UPDATE SELECTED LABEL

    private void updateSelectedLabel() {

        if (selectedAccountLabel == null) {
            return;
        }

        String text = customerSelected
                ? "✓  Customer Selected"
                : "✓  Shopkeeper Selected";

        selectedAccountLabel.setText(text);
        selectedAccountLabel.setTextFill(Color.web(PRIMARY));

        System.out.println(
                customerSelected
                        ? "Customer Selected"
                        : "Shopkeeper Selected"
        );
    }

    // UPDATE SHOPKEEPER DETAILS VISIBILITY

    private void updateShopkeeperDetailsVisibility() {
        if (shopkeeperDetailsBox != null) {
            shopkeeperDetailsBox.setVisible(!customerSelected);
            shopkeeperDetailsBox.setManaged(!customerSelected);
        }
    }

    // HANDLE IMAGE SELECTION

    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Store Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            if (storeImageLabel != null) {
                storeImageLabel.setText(file.getName());
            }
            System.out.println("Selected image: " + file.getAbsolutePath());
        }
    }

    // FORM VALIDATION

    private boolean validateForm(
            CheckBox termsCheckBox
    ) {

        if (fullNameField.getText().trim().isEmpty()) {

            showAlert(
                    "Validation Error",
                    "Please enter your full name."
            );

            fullNameField.requestFocus();

            return false;
        }

        if (mobileField.getText().trim().isEmpty()) {

            showAlert(
                    "Validation Error",
                    "Please enter your mobile number."
            );

            mobileField.requestFocus();

            return false;
        }

        if (emailField.getText().trim().isEmpty()) {

            showAlert(
                    "Validation Error",
                    "Please enter your email address."
            );

            emailField.requestFocus();

            return false;
        }

        if (passwordField.getText().isEmpty()) {

            showAlert(
                    "Validation Error",
                    "Please enter a password."
            );

            passwordField.requestFocus();

            return false;
        }

        if (!passwordField.getText().equals(
                confirmPasswordField.getText()
        )) {

            showAlert(
                    "Validation Error",
                    "Passwords do not match."
            );

            confirmPasswordField.requestFocus();

            return false;
        }

        if (!termsCheckBox.isSelected()) {

            showAlert(
                    "Validation Error",
                    "Please accept the Terms & Conditions."
            );

            return false;
        }

        return true;
    }

    // ALERT

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert = new Alert(
                Alert.AlertType.WARNING
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // RESPONSIVE LAYOUT

    private void updateResponsiveLayout() {

        if (scene == null) {
            return;
        }

        // JavaFX automatically handles most resizing
        // through HBox and VBox.
    }

    // GET ROOT

    public BorderPane getRoot() {
        return root;
    }

    // GET SCENE

    public Scene getScene() {
        return scene;
    }

    
    // SHOW SIGNUP

    public void show(Stage stage) {

        stage.setTitle(
                "Smart eUdhari - Create Account"
        );

        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(600);

        stage.show();
    }
}