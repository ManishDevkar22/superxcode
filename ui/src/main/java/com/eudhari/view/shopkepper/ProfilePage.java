package com.eudhari.view.shopkepper;

import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ProfilePage {

    private UserModel user;
    private ShopModel shop;

    public static Parent create(dashboard nav) {
        ProfilePage instance = new ProfilePage();
        return instance.buildRoot(nav);
    }

    public Parent buildRoot(dashboard nav) {
        user = ProfileController.getInstance().getCurrentUserProfile();
        if (user != null && user.getUid() != null) {
            shop = ShopController.getInstance().getShopByOwnerId(user.getUid());
        }

        String displayName = (user != null && user.getName() != null && !user.getName().isBlank()) ? user.getName() : "Shopkeeper";
        String shopNameStr = (shop != null && shop.getShopName() != null && !shop.getShopName().isBlank()) 
                ? shop.getShopName() 
                : ((user != null && user.getShopName() != null && !user.getShopName().isBlank()) ? user.getShopName() : "My Shop");
        String emailStr = (user != null && user.getEmail() != null) ? user.getEmail() : "";
        String phoneStr = (user != null && user.getPhone() != null) ? user.getPhone() : "";
        String addressStr = (user != null && user.getAddress() != null) ? user.getAddress() : "";
        String roleStr = (user != null && user.getRole() != null) ? user.getRole() : "shopkeeper";
        String categoryStr = (shop != null && shop.getBusinessCategory() != null) ? shop.getBusinessCategory() 
                : ((user != null && user.getBusinessCategory() != null) ? user.getBusinessCategory() : "General");
        String gpayStr = (shop != null && shop.getGpayId() != null) ? shop.getGpayId() 
                : ((user != null && user.getGpayId() != null) ? user.getGpayId() : "");

        String initials = "SK";
        if (displayName.length() >= 2) {
            String[] parts = displayName.split(" ");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else {
                initials = displayName.substring(0, 2).toUpperCase();
            }
        }

        // ================= TOP BAR =================
        Label tbTitle = new Label("Profile");
        tbTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label tbSubtitle = new Label("Welcome back, " + shopNameStr + "! 👋");
        tbSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        VBox tbTitleBox = new VBox(2, tbTitle, tbSubtitle);

        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox(Theme.SKY_BLUE, "-fx-background-color: " + Theme.BG_CARD + "; -fx-padding: 6 12; -fx-background-radius: 15; -fx-border-color: " + Theme.BORDER_DARK + "; -fx-border-radius: 15;");

        Button tbNotifBtn = new Button("🔔");
        tbNotifBtn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-font-size:16px; -fx-cursor:hand;");
        tbNotifBtn.setOnAction(e -> nav.navigateTo(dashboard.NOTIFICATIONS));

        Button tbProfileBtn = new Button(initials + "  " + displayName + " ▾");
        tbProfileBtn.setStyle("-fx-background-color:" + Theme.BG_CARD + "; -fx-text-fill:" + Theme.TEXT_PRIMARY
                + "; -fx-border-color:" + Theme.BORDER_DARK
                + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:6 12; -fx-cursor:hand;");
        tbProfileBtn.setOnAction(e -> nav.navigateTo(dashboard.PROFILE));

        HBox tbTopRight = new HBox(15, clockWidget, tbNotifBtn, tbProfileBtn);
        tbTopRight.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(20, tbTitleBox, tbTopRight);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 24, 15, 24));
        Theme.applyHeaderStyle(topBar);
        HBox.setHgrow(tbTopRight, Priority.ALWAYS);

        // ================= CENTER CONTENT =================
        Label ccHeading = new Label("My Profile");
        ccHeading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Button ccEditBtn = new Button("✎  Edit Profile");
        ccEditBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);

        HBox ccHeadingRow = new HBox(ccHeading, ccEditBtn);
        ccHeadingRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(ccHeading, Priority.ALWAYS);

        // ---------- Personal / Shop Details Card ----------
        Label dcAvatar = new Label(initials);
        dcAvatar.setStyle(
                "-fx-background-color: " + Theme.SKY_BLUE_BG + "; -fx-text-fill: " + Theme.SKY_BLUE
                        + "; -fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 35; -fx-background-radius: 60; -fx-border-color: "
                        + Theme.SKY_BLUE + "; -fx-border-radius: 60;");

        Label dcName = new Label(displayName);
        dcName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        String createdAtStr = user != null && user.getCreatedAt() != null ? user.getCreatedAt() : "";
        Label dcSince = new Label("Role: " + roleStr.toUpperCase() + (createdAtStr.length() >= 10 ? " | Joined: " + createdAtStr.substring(0, 10) : ""));
        dcSince.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-size: 12px;");

        VBox dcAvatarBox = new VBox(8, dcAvatar, dcName, dcSince);
        dcAvatarBox.setAlignment(Pos.CENTER);

        Label dcPersonalHead = new Label("Personal Details");
        dcPersonalHead.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + Theme.SKY_BLUE + ";");
        VBox dcPersonalDetails = new VBox(10, dcPersonalHead);
        
        addDetailRow(dcPersonalDetails, "Full Name", displayName);
        addDetailRow(dcPersonalDetails, "Contact Number", phoneStr);
        addDetailRow(dcPersonalDetails, "Email", emailStr);
        addDetailRow(dcPersonalDetails, "Role", roleStr);
        if (user != null && user.getUid() != null) {
            addDetailRow(dcPersonalDetails, "User UID", user.getUid());
        }

        Label dcShopHead = new Label("Shop Details  ✅ VERIFIED");
        dcShopHead.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + Theme.SKY_BLUE + ";");
        VBox dcShopDetails = new VBox(10, dcShopHead);
        
        addDetailRow(dcShopDetails, "Shop Name", shopNameStr);
        addDetailRow(dcShopDetails, "Category", categoryStr);
        addDetailRow(dcShopDetails, "Address", addressStr);
        addDetailRow(dcShopDetails, "GPay ID", gpayStr);
        if (shop != null && shop.getShopId() != null) {
            addDetailRow(dcShopDetails, "Shop ID", shop.getShopId());
        }

        HBox dcRow = new HBox(40, dcAvatarBox, dcPersonalDetails, dcShopDetails);
        dcRow.setAlignment(Pos.TOP_LEFT);

        VBox detailsCard = new VBox(dcRow);
        detailsCard.setPadding(new Insets(24));
        detailsCard.setStyle(Theme.STYLE_CARD);

        VBox centerContent = new VBox(22, ccHeadingRow, detailsCard);
        centerContent.setPadding(new Insets(24));

        // EDIT BUTTON ACTION
        ccEditBtn.setOnAction(e -> {
            showEditDialog(nav);
        });

        // ================= ROOT LAYOUT =================
        BorderPane borderpane = new BorderPane();
        borderpane.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        borderpane.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        Theme.applyScrollDarkStyle(scrollPane);
        borderpane.setCenter(scrollPane);

        return borderpane;
    }

    private void addDetailRow(VBox container, String labelText, String valueText) {
        Label lbl = new Label(labelText + ":  ");
        lbl.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-size:13px;");
        Label val = new Label(valueText != null ? valueText : "");
        val.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-size:13px;");
        container.getChildren().add(new HBox(lbl, val));
    }

    private void showEditDialog(dashboard nav) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Shopkeeper Profile");

        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: " + Theme.BG_CARD + ";");

        Label title = new Label("Update Profile & Shop Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        TextField nameInput = createInputField(user != null ? user.getName() : "");
        TextField phoneInput = createInputField(user != null ? user.getPhone() : "");
        TextField addressInput = createInputField(user != null ? user.getAddress() : "");
        TextField shopNameInput = createInputField(shop != null ? shop.getShopName() : (user != null ? user.getShopName() : ""));
        TextField categoryInput = createInputField(shop != null ? shop.getBusinessCategory() : (user != null ? user.getBusinessCategory() : ""));
        TextField gpayInput = createInputField(shop != null ? shop.getGpayId() : (user != null ? user.getGpayId() : ""));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.add(createLabel("Full Name:"), 0, 0); grid.add(nameInput, 1, 0);
        grid.add(createLabel("Phone:"), 0, 1); grid.add(phoneInput, 1, 1);
        grid.add(createLabel("Shop Name:"), 0, 2); grid.add(shopNameInput, 1, 2);
        grid.add(createLabel("Category:"), 0, 3); grid.add(categoryInput, 1, 3);
        grid.add(createLabel("Address:"), 0, 4); grid.add(addressInput, 1, 4);
        grid.add(createLabel("GPay ID:"), 0, 5); grid.add(gpayInput, 1, 5);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle(Theme.STYLE_BUTTON_PRIMARY);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            if (user != null && user.getUid() != null) {
                ProfileController.getInstance().updateShopkeeperProfile(
                        user.getUid(),
                        nameInput.getText(),
                        phoneInput.getText(),
                        addressInput.getText(),
                        shopNameInput.getText(),
                        categoryInput.getText(),
                        gpayInput.getText()
                );
            }
            dialog.close();
            nav.navigateTo(dashboard.PROFILE);
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(12, saveBtn, cancelBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(title, grid, btnRow);
        Scene scene = new Scene(form, 480, 380);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        return l;
    }

    private TextField createInputField(String value) {
        TextField tf = new TextField(value != null ? value : "");
        tf.setPrefWidth(260);
        tf.setStyle("-fx-background-color: " + Theme.BG_DARK + "; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-border-color: " + Theme.BORDER_DARK + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10;");
        return tf;
    }
}
