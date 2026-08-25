package com.eudhari.view.customer;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import com.eudhari.view.login.Loginpage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

public class Homepage {

    public void show(Stage eudhari) {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #F8F9FE; -fx-font-family: 'Segoe UI', sans-serif;");

        // --- TOP HEADER ---
        Label roleBadge = new Label("CUSTOMER");
        roleBadge.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; " +
                "-fx-font-size: 11px; -fx-padding: 4px 10px; -fx-background-radius: 6px; -fx-border-color: #FDE68A; -fx-border-radius: 6px;");

        VBox titleBox = new VBox(2);
        Label topPageTitle = new Label("Customer Portal");
        topPageTitle.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label topPageBreadcrumb = new Label("Smart eUdhari  /  Dashboard");
        topPageBreadcrumb.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 11px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(topPageTitle, topPageBreadcrumb);

        // TextField searchShops = new TextField();
        // searchShops.setPromptText("🔍  Search shops or products...");
        // searchShops.setPrefWidth(360);
        // searchShops.setPrefHeight(36);
        // searchShops.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #F1F5F9; -fx-text-fill: #0F172A; " +
        //         "-fx-prompt-text-fill: #94A3B8; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 6 12; -fx-font-size: 12px;");

        javafx.scene.layout.Region topSpacer = new javafx.scene.layout.Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #F8FAFC; -fx-background-radius: 50%; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 50%; -fx-min-width: 36px; -fx-min-height: 36px; -fx-font-size: 13px; -fx-cursor: hand;");

        UserModel activeCustUser = ProfileController.getInstance().getCurrentUserProfile();
        String displayCustName = activeCustUser != null && activeCustUser.getName() != null && !activeCustUser.getName().isBlank() ? activeCustUser.getName() : "Customer";
        String displayCustEmail = activeCustUser != null && activeCustUser.getEmail() != null && !activeCustUser.getEmail().isBlank() ? activeCustUser.getEmail() : "";
        String custInit = displayCustName.length() >= 2 ? displayCustName.substring(0, 2).toUpperCase() : "CU";

        Label avatar = new Label(custInit);
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(36, 36);
        avatar.setMinSize(36, 36);
        avatar.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-background-radius: 50%; -fx-border-color: #C7D2FE; -fx-border-radius: 50%;");

        VBox custInfo = new VBox(1);
        Label userName = new Label(displayCustName);
        userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label email = new Label("Customer Account");
        email.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 10px; -fx-text-fill: #64748B;");
        custInfo.getChildren().addAll(userName, email);

        HBox hb1 = new HBox(8, avatar, custInfo);
        hb1.setAlignment(Pos.CENTER_LEFT);
        hb1.setStyle("-fx-cursor: hand;");

        HBox clockWidget = com.eudhari.view.util.ClockWidget.createClockBox("#3A57E8", "-fx-background-color: #F8FAFC; -fx-padding: 6 12; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        HBox hb = new HBox(18, roleBadge, titleBox, topSpacer, clockWidget, notifBtn, hb1);
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.setStyle("-fx-padding: 12px 28px; -fx-background-color: #1122b8; -fx-border-color: transparent transparent #E2E8F0 transparent; " +
                "-fx-border-width: 0 0 1 0; -fx-pref-height: 64px;");
        bp.setTop(hb);

        // --- DASHBOARD CENTER CONTENT ---
        Label label = new Label("User Flow Overview");
        label.setStyle("-fx-font-size: 20px; -fx-text-fill: #0F172A; -fx-font-weight: bold;");

        ImageView connect = safeImageView("assets/images/Screenshot 2026-08-07 201003.png", 45, 45);
        connect.setClip(new Circle(22.5, 22.5, 22.5));
        Label title1 = new Label("Connect");
        title1.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label des1 = new Label(" Connect with your\n favorite local shops");
        des1.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        VBox vb2 = new VBox(connect, title1, des1);
        vb2.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Label lb1 = new Label("➜");
        lb1.setStyle(
                "-fx-font-size: 24px; -fx-alignment: center; -fx-padding: 20px 10px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");

        ImageView shopsoffers = safeImageView("assets/images/Screenshot 2026-08-07 201341.png", 45, 45);
        shopsoffers.setClip(new Circle(22.5, 22.5, 22.5));
        Label title2 = new Label("Shop & Offers");
        title2.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label des2 = new Label("  View products and\n   exclusive offers");
        des2.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        VBox vb3 = new VBox(shopsoffers, title2, des2);
        vb3.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Label lb2 = new Label("➜");
        lb2.setStyle(
                "-fx-font-size: 24px; -fx-alignment: center; -fx-padding: 20px 10px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");

        ImageView buyOffline = safeImageView("assets/images/Screenshot 2026-08-07 201539.png", 45, 45);
        buyOffline.setClip(new Circle(22.5, 22.5, 22.5));
        Label title3 = new Label("Buy Offline");
        title3.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label des3 = new Label("  Visit store and buy\n   product offline");
        des3.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        VBox vb4 = new VBox(buyOffline, title3, des3);
        vb4.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Label lb3 = new Label("➜");
        lb3.setStyle(
                "-fx-font-size: 24px; -fx-alignment: center; -fx-padding: 20px 10px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");

        ImageView getUdhari = safeImageView("assets/images/Screenshot 2026-08-07 201625.png", 45, 45);
        getUdhari.setClip(new Circle(22.5, 22.5, 22.5));
        Label title4 = new Label("Get Udhaari");
        title4.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label des4 = new Label("  Shopkeeper grant\n   credit limit");
        des4.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        VBox vb5 = new VBox(getUdhari, title4, des4);
        vb5.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        Label lb4 = new Label("➜");
        lb4.setStyle(
                "-fx-font-size: 24px; -fx-alignment: center; -fx-padding: 20px 10px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");

        ImageView payLater = safeImageView("assets/images/Screenshot 2026-08-07 201710.png", 45, 45);
        payLater.setClip(new Circle(22.5, 22.5, 22.5));
        Label title5 = new Label("Pay Later");
        title5.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label des5 = new Label("  Pay udhari online at\n   your convenience");
        des5.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        VBox vb6 = new VBox(payLater, title5, des5);
        vb6.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        HBox hb2 = new HBox(25, vb2, lb1, vb3, lb2, vb4, lb3, vb5, lb4, vb6);
        hb2.setStyle(
                "-fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-width: 1px; -fx-background-color: #FFFFFF; -fx-padding: 15px; -fx-alignment: center; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        Label lb5 = new Label("Nearby Connected Shops");
        lb5.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label lb6 = new Label("Trusted shops in your locality ready to serve you.");
        lb6.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        // --- SHOP CARD 1: Goroba Kirana ---
        ImageView gorobaShopImage = safeImageView("assets/images/Screenshot 2026-08-14 132836.png", 320, 130);
        Label gorobaName = new Label("Goroba Kirana");
        gorobaName.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        HBox gorobaTitle = new HBox(10, gorobaName);
        gorobaTitle.setStyle("-fx-alignment: center-left;");
        Label gorobaStatus = new Label("OPEN");
        gorobaStatus.setStyle(
                "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label gorobaLocation = new Label("⌖  Sector 14");
        gorobaLocation.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        Label gorobaTag1 = new Label("Daily Essentials");
        Label gorobaTag2 = new Label("GENERAL STORE");
        gorobaTag1.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        gorobaTag2.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        Button gorobaConnect = new Button("Connect");
        gorobaConnect.setMaxWidth(Double.MAX_VALUE);
        gorobaConnect.setStyle(
                "-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        VBox vb7 = new VBox(10, gorobaShopImage, gorobaTitle, gorobaStatus, gorobaLocation,
                new HBox(8, gorobaTag1, gorobaTag2), gorobaConnect);
        vb7.setPrefWidth(320);
        vb7.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        // --- SHOP CARD 2: Sai Kirana ---
        ImageView saiShopImage = safeImageView("assets/images/Screenshot 2026-08-14 132913.png", 320, 130);
        Label saiName = new Label("Sai Kirana");
        saiName.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        HBox saiTitle = new HBox(10, saiName);
        saiTitle.setStyle("-fx-alignment: center-left;");
        Label saiStatus = new Label("OPEN");
        saiStatus.setStyle(
                "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label saiLocation = new Label("⌖  DLF Phase 1");
        saiLocation.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        Label saiTag1 = new Label("Grocery");
        Label saiTag2 = new Label("GENERAL STORE");
        saiTag1.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        saiTag2.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        Button saiConnect = new Button("Connect");
        saiConnect.setMaxWidth(Double.MAX_VALUE);
        saiConnect.setStyle(
                "-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        VBox vb8 = new VBox(10, saiShopImage, saiTitle, saiStatus, saiLocation,
                new HBox(8, saiTag1, saiTag2), saiConnect);
        vb8.setPrefWidth(320);
        vb8.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        // --- SHOP CARD 3: Saishradha Kirana ---
        ImageView saishradhaShopImage = safeImageView("assets/images/Screenshot 2026-08-14 132930.png", 320, 130);
        Label saishradhaName = new Label("Saishradha Kirana");
        saishradhaName.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        HBox saishradhaTitle = new HBox(10, saishradhaName);
        saishradhaTitle.setStyle("-fx-alignment: center-left;");
        Label saishradhaStatus = new Label("CLOSING SOON");
        saishradhaStatus.setStyle(
                "-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-padding: 4px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label saishradhaLocation = new Label("⌖  Central Market");
        saishradhaLocation.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        Label saishradhaTag1 = new Label("Sweets");
        Label saishradhaTag2 = new Label("GENERAL STORE");
        saishradhaTag1.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        saishradhaTag2.setStyle(
                "-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        Button saishradhaConnect = new Button("Connect");
        saishradhaConnect.setMaxWidth(Double.MAX_VALUE);
        saishradhaConnect.setStyle(
                "-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        VBox vb9 = new VBox(10, saishradhaShopImage, saishradhaTitle, saishradhaStatus, saishradhaLocation,
                new HBox(8, saishradhaTag1, saishradhaTag2), saishradhaConnect);
        vb9.setPrefWidth(320);
        vb9.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        HBox shopCardsBox = new HBox(24);
        shopCardsBox.setStyle("-fx-padding: 10px 0;");

        List<ShopModel> fetchedShops = ShopController.getInstance().getAllShops();
        UserModel loggedInCust = ProfileController.getInstance().getCurrentUserProfile();
        String custUid = loggedInCust != null && loggedInCust.getUid() != null ? loggedInCust.getUid() : "";
        String custNameStr = loggedInCust != null && loggedInCust.getName() != null ? loggedInCust.getName() : "Customer";

        if (fetchedShops != null && !fetchedShops.isEmpty()) {
            for (ShopModel s : fetchedShops) {
                VBox card = createShopCard(s, custUid, custNameStr, null);
                shopCardsBox.getChildren().add(card);
            }
        } else {
            VBox emptyShopBox = new VBox(8);
            emptyShopBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-padding: 20;");
            Text noShopText = new Text("No active shops registered yet.");
            noShopText.setStyle("-fx-fill: #64748B; -fx-font-size: 14px; -fx-font-weight: bold;");
            emptyShopBox.getChildren().add(noShopText);
            shopCardsBox.getChildren().add(emptyShopBox);
        }

        ScrollPane shopsScrollPane = new ScrollPane(shopCardsBox);
        shopsScrollPane.setFitToHeight(true);
        shopsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        shopsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        shopsScrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F8F9FE; -fx-border-color: transparent; -fx-padding: 0;");

        HBox hb3 = new HBox(shopsScrollPane);
        hb3.setStyle("-fx-padding: 10px 0;");

        // --- REQUEST LIMIT & CREDIT TRUST ---
        Label requestTitle = new Label("Request Udhaari Limit");
        requestTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label requestDescription = new Label(
                "Select a connected shop to request a monthly credit limit for\nseamless shopping.");
        requestDescription.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        ComboBox<String> shopSelection = new ComboBox<>();
        shopSelection.getItems().addAll("Goroba Kirana", "Sai Kirana", "Saishradha Kirana");
        shopSelection.setPromptText("Select a shop...");
        shopSelection.setPrefWidth(250);
        shopSelection.setStyle(
                "-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Button requestLimit = new Button("Request Limit");
        requestLimit.setStyle(
                "-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;");
        VBox requestBox = new VBox(10, requestTitle, requestDescription, new HBox(12, shopSelection, requestLimit));
        requestBox.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");
        HBox.setHgrow(requestBox, Priority.ALWAYS);

        Label trustLabel = new Label("YOUR CREDIT TRUST");
        trustLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label trustScore = new Label("100");
        trustScore.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");
        Label trustMessage = new Label("Excellent! You are eligible for higher limits.");
        trustMessage.setWrapText(true);
        trustMessage.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 12px;");
        Label dividerLine = new Label("━━━━━━━━━━━━");
        dividerLine.setStyle("-fx-text-fill: #E2E8F0;");
        VBox trustBox = new VBox(8, trustLabel, trustScore, dividerLine, trustMessage);
        trustBox.setPrefWidth(250);
        trustBox.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        HBox hb4 = new HBox(20, requestBox, trustBox);
        hb4.setStyle("-fx-padding: 15px 0;");

        // --- DASHBOARD CONTAINER ---
        VBox vb1 = new VBox(14, label, hb2, lb5, lb6, hb3, hb4);
        vb1.setStyle("-fx-padding: 24px; -fx-background-color: #c1e1ff;");

        ScrollPane centerScrollPane = new ScrollPane(vb1);
        centerScrollPane.setFitToWidth(true);
        centerScrollPane
                .setStyle("-fx-background-color: transparent; -fx-background: #f6f7f4; -fx-border-color: transparent;");
        final Node dashboardCenter = centerScrollPane;
        bp.setCenter(dashboardCenter);

        // ===== SIDEBAR NAVIGATION (ADMIN STYLE NAVY) =====
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setPadding(new Insets(20, 14, 18, 14));
        sidebar.setStyle("-fx-background-color: #122f58; -fx-border-color: transparent #1E293B transparent transparent; -fx-border-width: 0 1 0 0;");

        // Brand Header
        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 8, 14, 8));

        Label brandIcon = new Label("🛍");
        brandIcon.setAlignment(Pos.CENTER);
        brandIcon.setPrefSize(38, 38);
        brandIcon.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: rgba(58, 87, 232, 0.2); -fx-text-fill: #60A5FA; " +
                "-fx-font-size: 18px; -fx-background-radius: 8px;");

        VBox brandText = new VBox(1);
        Label brandTitle = new Label("Smart eUdhari");
        brandTitle.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label brandSubtitle = new Label("Customer Portal");
        brandSubtitle.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        brandText.getChildren().addAll(brandTitle, brandSubtitle);

        brandBox.getChildren().addAll(brandIcon, brandText);
        sidebar.getChildren().add(brandBox);

        ListView<String> list = new ListView<>();
        list.getItems().addAll(
                "🏠  Dashboard",
                "🏪  Connected Shops",
                "🛍  Shop Products",
                "📒  My Udhari",
                "💳  Pay Udhari",
                "💰  Expenses",
                "📜  History",
                "🔔  Notifications",
                "🎧  Help & Support",
                "⚙  Profile",
                "🚪  Logout");
        list.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        VBox.setVgrow(list, Priority.ALWAYS);

        list.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    boolean isLogout = item.contains("Logout");

                    if (isSelected()) {
                        setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #3A57E8; -fx-text-fill: #FFFFFF; " +
                                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else if (isLogout) {
                        setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #F87171; " +
                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    } else {
                        setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
                                "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                    }

                    setOnMouseEntered(e -> {
                        if (!isSelected()) {
                            if (isLogout) {
                                setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #3F1D1D; -fx-text-fill: #EF4444; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            if (isLogout) {
                                setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #F87171; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            } else {
                                setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: transparent; -fx-text-fill: #94A3B8; " +
                                        "-fx-font-size: 13px; -fx-font-weight: 500; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
                            }
                        }
                    });
                }
            }
        });

        // --- Navigation Callbacks ---
        Runnable goToDashboard = () -> {
            bp.setCenter(dashboardCenter);
            topPageTitle.setText("Customer Portal");
            topPageBreadcrumb.setText("Smart eUdhari  /  Dashboard");
            list.getSelectionModel().select("🏠  Dashboard");
        };

        Runnable goToPayUdhari = () -> {
            PayUdhari payUdhari = new PayUdhari(goToDashboard);
            bp.setCenter(payUdhari.getView());
            topPageTitle.setText("Pay Udhari");
            topPageBreadcrumb.setText("Smart eUdhari  /  Pay Udhari");
            list.getSelectionModel().select("💳  Pay Udhari");
        };

        Runnable[] goToConnectedShops = new Runnable[1];
        Runnable[] goToNotifications = new Runnable[1];
        Runnable[] goToShopProducts = new Runnable[1];
        Runnable[] goToHistory = new Runnable[1];

        // Route to Shop Products view
        goToShopProducts[0] = () -> {
            shopProductView productView = new shopProductView("Goroba Kirana", goToDashboard);
            bp.setCenter(productView.getView());
            topPageTitle.setText("Shop Products");
            topPageBreadcrumb.setText("Smart eUdhari  /  Shop Products");
            list.getSelectionModel().select("🛍  Shop Products");
        };

        goToConnectedShops[0] = () -> {
            ConnectedShops connectedShops = new ConnectedShops(goToDashboard);
            bp.setCenter(connectedShops.getView());
            topPageTitle.setText("Connected Shops");
            topPageBreadcrumb.setText("Smart eUdhari  /  Connected Shops");
            list.getSelectionModel().select("🏪  Connected Shops");
        };

        gorobaConnect.setOnAction(e -> goToShopProducts[0].run());
        saiConnect.setOnAction(e -> goToShopProducts[0].run());
        saishradhaConnect.setOnAction(e -> goToShopProducts[0].run());

        goToNotifications[0] = () -> {
            Notifications notifications = new Notifications(
                    goToDashboard, goToPayUdhari, goToConnectedShops[0]);
            bp.setCenter(notifications.getView());
            topPageTitle.setText("Notifications");
            topPageBreadcrumb.setText("Smart eUdhari  /  Notifications");
            list.getSelectionModel().select("🔔  Notifications");
        };

        // --- Route to Profile View ---
        Runnable goToProfile = () -> {
            Node profileView = createProfileView(goToDashboard);
            bp.setCenter(profileView);
            topPageTitle.setText("Profile & Settings");
            topPageBreadcrumb.setText("Smart eUdhari  /  Profile");
            list.getSelectionModel().select("⚙  Profile");
        };

        // Route to History View
        goToHistory[0] = () -> {
            History history = new History();

            // Connect navigation from History back to Homepage flows
            history.setGoToDashboard(goToDashboard);
            history.setGoToConnectedShops(goToConnectedShops[0]);
            history.setGoToMyUdhari(() -> {
                Myudhari myudhari = new Myudhari(goToDashboard, goToPayUdhari);
                bp.setCenter(myudhari.getView());
                topPageTitle.setText("My Udhari");
                topPageBreadcrumb.setText("Smart eUdhari  /  My Udhari");
                list.getSelectionModel().select("📒  My Udhari");
            });
            history.setGoToPayUdhari(goToPayUdhari);
            history.setGoToNotifications(goToNotifications[0]);
            history.setGoToSettings(goToProfile);

            bp.setCenter(history.getView());
            topPageTitle.setText("Transaction History");
            topPageBreadcrumb.setText("Smart eUdhari  /  History");
            list.getSelectionModel().select("📜  History");
        };

        // Top profile banner click opens Profile
        hb1.setOnMouseClicked(e -> goToProfile.run());

        // Sidebar click routing
        list.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected == null) {
                return;
            }

            if (selected.contains("Dashboard")) {
                goToDashboard.run();
            } else if (selected.contains("Connected Shops")) {
                goToConnectedShops[0].run();
            } else if (selected.contains("Shop Products")) {
                goToShopProducts[0].run();
            } else if (selected.contains("My Udhari")) {
                Myudhari myudhari = new Myudhari(goToDashboard, goToPayUdhari);
                bp.setCenter(myudhari.getView());
                topPageTitle.setText("My Udhari");
                topPageBreadcrumb.setText("Smart eUdhari  /  My Udhari");
            } else if (selected.contains("Pay Udhari")) {
                goToPayUdhari.run();
            } else if (selected.contains("Expenses")) {
                CustomerExpenses expenses = new CustomerExpenses(goToDashboard);
                bp.setCenter(expenses.getView());
                topPageTitle.setText("Expenses Tracker");
                topPageBreadcrumb.setText("Smart eUdhari  /  Expenses");
            } else if (selected.contains("History")) {
                goToHistory[0].run();
            } else if (selected.contains("Notifications")) {
                goToNotifications[0].run();
            } else if (selected.contains("Help & Support")) {
                CustomerHelpSupport helpSupport = new CustomerHelpSupport(goToDashboard);
                bp.setCenter(helpSupport.getView());
                topPageTitle.setText("Help & Support");
                topPageBreadcrumb.setText("Smart eUdhari  /  Support");
            } else if (selected.contains("Profile")) {
                goToProfile.run();
            } else if (selected.contains("Logout")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Logout Confirmation");
                alert.setHeaderText("Are you sure you want to log out?");
                alert.setContentText("You will be returned to the login screen.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        Loginpage login = new Loginpage();
                        login.show(eudhari);
                    } catch (Exception e) {
                        eudhari.close();
                    }
                } else {
                    list.getSelectionModel().select("🏠  Dashboard");
                }
            }
        });

        notifBtn.setOnAction(e -> goToNotifications[0].run());

        sidebar.getChildren().add(list);

        // Support Pill Button
        Button supportBtn = new Button("🎧  Support");
        supportBtn.setMaxWidth(Double.MAX_VALUE);
        supportBtn.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; " +
                "-fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");
        supportBtn.setOnAction(e -> {
            CustomerHelpSupport helpSupport = new CustomerHelpSupport(goToDashboard);
            bp.setCenter(helpSupport.getView());
            topPageTitle.setText("Help & Support");
            topPageBreadcrumb.setText("Smart eUdhari  /  Support");
            list.getSelectionModel().select("🎧  Help & Support");
        });
        sidebar.getChildren().add(supportBtn);

        bp.setLeft(sidebar);

        // Pre-select Dashboard
        list.getSelectionModel().select(0);

        Scene user = new Scene(bp, 1280, 800);
        // Image img = loadImage("assets/images/me.jpg");
        // if (img != null) {
        // eudhari.getIcons().add(img);
        // }
        eudhari.setScene(user);
        eudhari.setTitle("Smart eUdhari - Customer Portal");
    }

    // =========================================================================
    // PROFILE VIEW GENERATOR
    // =========================================================================
    private Node createProfileView(Runnable goToDashboard) {
        // --- Header Section ---
        Label profileHeader = new Label("👤 User Profile & Settings");
        profileHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        HBox topRow = new HBox(profileHeader);
        HBox.setHgrow(profileHeader, Priority.ALWAYS);
        topRow.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 10 0;");

        UserModel user = ProfileController.getInstance().getCurrentUserProfile();
        String initialName = user != null && user.getName() != null && !user.getName().isBlank() ? user.getName() : "Customer";
        String initialEmail = user != null && user.getEmail() != null ? user.getEmail() : "";
        String initialPhone = user != null && user.getPhone() != null ? user.getPhone() : "";
        String initialAddress = user != null && user.getAddress() != null ? user.getAddress() : "";

        // --- Profile Banner Card ---
        ImageView bannerAvatar = safeImageView("assets/images/me.jpg", 80, 80);
        bannerAvatar.setClip(new Circle(40, 40, 40));

        Label pName = new Label(initialName);
        pName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label pEmail = new Label(initialEmail + (initialPhone.isBlank() ? "" : " • " + initialPhone));
        pEmail.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Label kycBadge = new Label("✔ KYC Verified");
        kycBadge.setStyle(
                "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        VBox bannerInfo = new VBox(6, pName, pEmail, kycBadge);
        bannerInfo.setStyle("-fx-alignment: center-left;");

        HBox bannerCard = new HBox(20, bannerAvatar, bannerInfo);
        bannerCard.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 20px; -fx-alignment: center-left; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        // --- Account Summary Stats Row ---
        VBox stat1 = createStatCard("CREDIT TRUST", "100", "#3A57E8");
        VBox stat2 = createStatCard("CONNECTED SHOPS", "3 Shops", "#16A34A");
        VBox stat3 = createStatCard("TOTAL UDHARI LIMIT", "₹15,000", "#7C3AED");
        VBox stat4 = createStatCard("ACTIVE DUES", "₹0.00", "#D97706");
        HBox statsRow = new HBox(15, stat1, stat2, stat3, stat4);

        // --- Personal Details Form ---
        Label formTitle = new Label("Personal & Contact Details");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        TextField nameField = createStyledInput(initialName);
        TextField emailField = createStyledInput(initialEmail);
        emailField.setEditable(false);
        TextField phoneField = createStyledInput(initialPhone);
        TextField addressField = createStyledInput(initialAddress);
        TextField aadhaarField = createStyledInput("XXXX-XXXX-4589");
        aadhaarField.setEditable(false);
        aadhaarField.setStyle(aadhaarField.getStyle() + "-fx-opacity: 0.7;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(14);
        formGrid.add(createFieldLabel("Full Name:"), 0, 0);
        formGrid.add(nameField, 0, 1);
        formGrid.add(createFieldLabel("Email Address:"), 1, 0);
        formGrid.add(emailField, 1, 1);
        formGrid.add(createFieldLabel("Phone Number:"), 0, 2);
        formGrid.add(phoneField, 0, 3);
        formGrid.add(createFieldLabel("Aadhaar KYC (Linked):"), 1, 2);
        formGrid.add(aadhaarField, 1, 3);
        formGrid.add(createFieldLabel("Residential Address:"), 0, 4, 2, 1);
        formGrid.add(addressField, 0, 5, 2, 1);

        Label saveStatus = new Label("");
        saveStatus.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");

        Button saveBtn = new Button("💾 Save Changes");
        saveBtn.setStyle(
                "-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            if (user != null && user.getUid() != null) {
                UserModel updated = ProfileController.getInstance().updateCustomerProfile(
                        user.getUid(),
                        nameField.getText(),
                        phoneField.getText(),
                        addressField.getText()
                );
                if (updated != null) {
                    pName.setText(updated.getName());
                    pEmail.setText(updated.getEmail() + (updated.getPhone().isBlank() ? "" : " • " + updated.getPhone()));
                    saveStatus.setText("✓ Profile updated in Firestore!");
                }
            } else {
                saveStatus.setText("✓ Profile updated!");
            }
        });

        HBox btnRow = new HBox(15, saveBtn, saveStatus);
        btnRow.setStyle("-fx-alignment: center-left; -fx-padding: 10 0 0 0;");

        VBox formCard = new VBox(15, formTitle, formGrid, btnRow);
        formCard.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 22px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        // --- Outer Container with Scroll ---
        VBox profileContent = new VBox(20, topRow, bannerCard, statsRow, formCard);
        profileContent.setStyle("-fx-padding: 24px; -fx-background-color: #F8F9FE;");

        ScrollPane profileScroll = new ScrollPane(profileContent);
        profileScroll.setFitToWidth(true);
        profileScroll
                .setStyle("-fx-background-color: transparent; -fx-background: #F8F9FE; -fx-border-color: transparent;");

        return profileScroll;
    }

    private VBox createStatCard(String label, String value, String colorHex) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        VBox box = new VBox(6, l, v);
        box.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private Label createFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        return lbl;
    }

    private TextField createStyledInput(String initialText) {
        TextField tf = new TextField(initialText);
        tf.setPrefHeight(40);
        tf.setPrefWidth(350);
        tf.setStyle(
                "-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 12; -fx-font-size: 13px;");
        return tf;
    }

    private Image loadImage(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank())
            return null;
        try {
            String cleanPath = resourcePath.replace("\\", "/");
            if (!cleanPath.startsWith("/")) {
                cleanPath = "/" + cleanPath;
            }
            cleanPath = cleanPath.replace("/assets/image/", "/assets/images/");
            java.io.InputStream is = getClass().getResourceAsStream(cleanPath);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private ImageView safeImageView(String path, double width, double height) {
        Image img = loadImage(path);
        ImageView iv = new ImageView();
        if (img != null) {
            iv.setImage(img);
        }
        if (width > 0)
            iv.setFitWidth(width);
        if (height > 0)
            iv.setFitHeight(height);
        return iv;
    }

    private VBox createShopCard(ShopModel shop, String customerId, String customerName, Runnable onViewShop) {
        ImageView shopImg = safeImageView("assets/images/Screenshot 2026-08-14 132836.png", 320, 130);
        Label sName = new Label(shop.getShopName() != null ? shop.getShopName() : "Shop");
        sName.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        HBox sTitle = new HBox(10, sName);
        sTitle.setStyle("-fx-alignment: center-left;");

        Label sStatus = new Label(shop.getStatus() != null ? shop.getStatus() : "ACTIVE");
        sStatus.setStyle(
                "-fx-background-color: #064e3b; -fx-text-fill: #4ade80; -fx-padding: 4px 8px; -fx-background-radius: 10px; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label sLocation = new Label("⌖  " + (shop.getAddress() != null && !shop.getAddress().isBlank() ? shop.getAddress() : "Local Area"));
        sLocation.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        Label tag1 = new Label(shop.getBusinessCategory() != null && !shop.getBusinessCategory().isBlank() ? shop.getBusinessCategory() : "Grocery");
        Label tag2 = new Label("GENERAL STORE");
        tag1.setStyle("-fx-background-color: #131e33; -fx-text-fill: #cbd5e1; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");
        tag2.setStyle("-fx-background-color: #131e33; -fx-text-fill: #cbd5e1; -fx-padding: 4px 8px; -fx-background-radius: 6px; -fx-font-size: 10px;");

        Button connectBtn = new Button("Connect");
        connectBtn.setMaxWidth(Double.MAX_VALUE);

        String currentReqStatus = ConnectionRequestController.getInstance().getRequestStatus(customerId, shop.getShopId());
        updateConnectButtonStyle(connectBtn, currentReqStatus);

        connectBtn.setOnAction(e -> {
            String updatedStatus = ConnectionRequestController.getInstance().getRequestStatus(customerId, shop.getShopId());
            if ("APPROVED".equalsIgnoreCase(updatedStatus)) {
                if (onViewShop != null) {
                    onViewShop.run();
                }
            } else if ("PENDING".equalsIgnoreCase(updatedStatus)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Connection request is pending approval from shopkeeper.", ButtonType.OK);
                alert.showAndWait();
            } else {
                ConnectionRequestModel req = ConnectionRequestController.getInstance().sendConnectionRequest(
                        customerId,
                        customerName,
                        shop.getShopId(),
                        shop.getShopName(),
                        shop.getOwnerId()
                );
                if (req != null) {
                    updateConnectButtonStyle(connectBtn, req.getStatus());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Connection request sent to " + shop.getShopName() + "!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        VBox card = new VBox(10, shopImg, sTitle, sStatus, sLocation, new HBox(8, tag1, tag2), connectBtn);
        card.setPrefWidth(320);
        card.setMinWidth(320);
        card.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-padding: 14px;");
        return card;
    }

    private void updateConnectButtonStyle(Button connectBtn, String status) {
        if ("APPROVED".equalsIgnoreCase(status)) {
            connectBtn.setText("CONNECTED  ✓");
            connectBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        } else if ("PENDING".equalsIgnoreCase(status)) {
            connectBtn.setText("PENDING  ⌛");
            connectBtn.setStyle("-fx-background-color: #d97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            connectBtn.setText("REJECTED  ❌");
            connectBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        } else {
            connectBtn.setText("Connect  →");
            connectBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px; -fx-cursor: hand;");
        }
    }
}