package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import com.eudhari.model.AdminModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminProfileView {
    private final AdminController controller;
    private final Runnable onBack;
    private final ScrollPane rootPane;

    // Styling constants
    private static final String FONT = "-fx-font-family: 'Segoe UI', sans-serif;";
    private static final String APP_BG = "#c1e1ff";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER_COLOR = "#E2E8F0";
    private static final String PRIMARY_COLOR = "#3A57E8";
    private static final String PRIMARY_HOVER = "#2D44C2";

    public AdminProfileView(AdminController controller, Runnable onBack) {
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
        Label title = new Label("Admin Profile & Settings");
        title.setStyle(FONT + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #15181fff;");
        Label subtitle = new Label("Manage system administrator credentials, security, and notification preferences.");
        subtitle.setStyle(FONT + "-fx-font-size: 13px; -fx-text-fill: #627a9cff;");
        titleBox.getChildren().addAll(title, subtitle);

        headerRow.getChildren().add(titleBox);
        mainContent.getChildren().add(headerRow);

        AdminModel admin = controller.getAdminProfile();

        // 2 Column Layout: Left (Profile Details & Settings), Right (Security /
        // Password)
        HBox columns = new HBox(20);

        // Left Column: Profile Card & Preferences Card
        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Profile Details Card
        VBox profileCard = createCard();
        HBox profHeader = new HBox(16);
        profHeader.setAlignment(Pos.CENTER_LEFT);

        String initialLetter = "A";
        if (admin.getName() != null && !admin.getName().isBlank()) {
            initialLetter = admin.getName().trim().substring(0, 1).toUpperCase();
        }
        Label avatarCircle = new Label(initialLetter);
        avatarCircle.setAlignment(Pos.CENTER);
        avatarCircle.setPrefSize(56, 56);
        avatarCircle.setStyle(FONT + "-fx-background-color: #EEF2FF; -fx-text-fill: " + PRIMARY_COLOR
                + "; -fx-font-size: 22px; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-border-color: #C7D2FE; -fx-border-radius: 50%;");

        VBox profInfo = new VBox(3);
        Label profName = new Label(admin.getName());
        profName.setStyle(FONT + "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label profRole = new Label(admin.getRole());
        profRole.setStyle(FONT + "-fx-font-size: 12px; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: 600;");
        profInfo.getChildren().addAll(profName, profRole);

        profHeader.getChildren().addAll(avatarCircle, profInfo);
        profileCard.getChildren().add(profHeader);

        VBox formFields = new VBox(12);
        formFields.setPadding(new Insets(16, 0, 0, 0));

        TextField nameField = createFormField(admin.getName(), true);
        TextField emailField = createFormField(admin.getEmail(), true);
        TextField phoneField = createFormField(admin.getPhone(), true);
        TextField roleField = createFormField(admin.getRole(), false);

        formFields.getChildren().addAll(
                new VBox(4, new Label("ADMIN NAME") {
                    {
                        setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
                    }
                }, nameField),
                new VBox(4, new Label("EMAIL ADDRESS") {
                    {
                        setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
                    }
                }, emailField),
                new VBox(4, new Label("PHONE NUMBER") {
                    {
                        setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
                    }
                }, phoneField),
                new VBox(4, new Label("SYSTEM ROLE") {
                    {
                        setStyle(FONT + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
                    }
                }, roleField));

        HBox profBtnRow = new HBox(10);
        profBtnRow.setAlignment(Pos.CENTER_RIGHT);
        profBtnRow.setPadding(new Insets(10, 0, 0, 0));

        Button saveProfileBtn = createPrimaryButton("Save Profile Changes");
        saveProfileBtn.setOnAction(e -> {
            admin.setName(nameField.getText().trim());
            admin.setEmail(emailField.getText().trim());
            admin.setPhone(phoneField.getText().trim());
            controller.updateAdminProfile(admin);
            profName.setText(admin.getName());
            if (admin.getName() != null && !admin.getName().isBlank()) {
                avatarCircle.setText(admin.getName().trim().substring(0, 1).toUpperCase());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Admin profile details updated successfully!",
                    ButtonType.OK);
            alert.showAndWait();
        });

        profBtnRow.getChildren().add(saveProfileBtn);
        profileCard.getChildren().addAll(formFields, profBtnRow);

        leftCol.getChildren().add(profileCard);
        mainContent.getChildren().add(leftCol);

        ScrollPane sp = new ScrollPane(mainContent);
        sp.setFitToWidth(true);
        sp.setStyle(
                "-fx-background-color: transparent; -fx-background: " + APP_BG + "; -fx-border-color: transparent;");
        return sp;
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

    private TextField createFormField(String text, boolean editable) {
        TextField tf = new TextField(text);
        tf.setEditable(editable);
        String baseBg = editable ? "#F8FAFC" : "#F1F5F9";
        tf.setStyle(
                FONT + "-fx-background-color: " + baseBg + "; -fx-background-radius: 6px; -fx-border-color: #CBD5E1; " +
                        "-fx-border-radius: 6px; -fx-padding: 8px 12px; -fx-font-size: 12px;");
        return tf;
    }
}
