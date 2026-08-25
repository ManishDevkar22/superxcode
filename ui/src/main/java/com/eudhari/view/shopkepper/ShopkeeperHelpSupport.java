package com.eudhari.view.shopkepper;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.NotificationController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.CustomerController;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.UserModel;
import com.eudhari.model.shopkeppermodel.CustomerModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopkeeperHelpSupport {

    private final BorderPane root;
    private final VBox submittedTicketsContainer = new VBox(10);

    public ShopkeeperHelpSupport() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #F8F9FE; -fx-font-family: 'Segoe UI', sans-serif;");

        VBox mainContent = new VBox(22);
        mainContent.setPadding(new Insets(24));

        // 1. Header Row
        Label headerTitle = new Label("🎧 Shopkeeper Help & Support Desk");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label subHeader = new Label("Need assistance with settlements, customer disputes, or app features? Submit your complaint to Admin or directly to a specific Customer.");
        subHeader.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        VBox headerBox = new VBox(4, headerTitle, subHeader);

        // 2. Complaint Writing Form Card
        VBox formCard = new VBox(16);
        formCard.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 22; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        Label formTitle = new Label("Create a New Support Ticket / Complaint");
        formTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        UserModel currentSkUser = ProfileController.getInstance().getCurrentUserProfile();
        String currentSkUid = currentSkUser != null && currentSkUser.getUid() != null ? currentSkUser.getUid() : "";
        String currentSkName = currentSkUser != null && currentSkUser.getShopName() != null && !currentSkUser.getShopName().isBlank() ? currentSkUser.getShopName() : "Shopkeeper";

        // Recipient Selection (Admin vs Specific Customer)
        Label recipientLabel = new Label("Send Complaint To:");
        recipientLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        ComboBox<String> recipientCombo = new ComboBox<>();
        recipientCombo.setPrefHeight(40);
        recipientCombo.setMaxWidth(Double.MAX_VALUE);
        recipientCombo.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; " +
                "-fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");

        recipientCombo.getItems().add("Admin (Platform Administrator)");

        // Populate connected customers for this shopkeeper
        Map<String, String> customerIdToUidMap = new HashMap<>();
        if (!currentSkUid.isBlank()) {
            List<ConnectionRequestModel> approvedReqs = ConnectionRequestController.getInstance().getApprovedCustomersForShopkeeper(currentSkUid);
            for (ConnectionRequestModel req : approvedReqs) {
                String cName = req.getCustomerName() != null && !req.getCustomerName().isBlank() ? req.getCustomerName() : "Customer";
                String itemStr = "Customer: " + cName + " [" + req.getCustomerId() + "]";
                recipientCombo.getItems().add(itemStr);
                customerIdToUidMap.put(itemStr, req.getCustomerId());
            }
        }
        
        // Also add active customers from store if any missing
        for (CustomerModel cm : CustomerController.getInstance().getActiveCustomers()) {
            String itemStr = "Customer: " + cm.getName() + " [" + cm.getId() + "]";
            if (!recipientCombo.getItems().contains(itemStr)) {
                recipientCombo.getItems().add(itemStr);
                customerIdToUidMap.put(itemStr, cm.getUid() != null && !cm.getUid().isBlank() ? cm.getUid() : cm.getId());
            }
        }

        recipientCombo.getSelectionModel().selectFirst();

        // Subject TextField
        Label subjTitleLabel = new Label("Complaint Subject:");
        subjTitleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject (e.g. Settlement delay, Payment dispute)");
        subjectField.setPrefHeight(40);
        subjectField.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-prompt-text-fill: #94A3B8; " +
                "-fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 12; -fx-font-size: 13px;");

        // Message TextArea
        Label msgLabel = new Label("Complaint / Message Details:");
        msgLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        TextArea complaintTextArea = new TextArea();
        complaintTextArea.setPromptText("Describe your complaint or inquiry in detail...");
        complaintTextArea.setPrefRowCount(4);
        complaintTextArea.setWrapText(true);
        complaintTextArea.setStyle("-fx-control-inner-background: #F8FAFC; -fx-text-fill: #0F172A; " +
                "-fx-prompt-text-fill: #94A3B8; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");

        Label feedbackBanner = new Label();
        feedbackBanner.setVisible(false);

        Runnable refreshSkComplaints = () -> {
            submittedTicketsContainer.getChildren().clear();
            List<com.eudhari.model.ComplaintModel> list = com.eudhari.controller.ComplaintController.getInstance().getComplaintsForUser(currentSkUid);
            if (list != null && !list.isEmpty()) {
                for (com.eudhari.model.ComplaintModel c : list) {
                    addFirestoreTicketToHistory(c);
                }
            } else {
                Label emptyLbl = new Label("No complaints or support tickets submitted yet.");
                emptyLbl.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
                submittedTicketsContainer.getChildren().add(emptyLbl);
            }
        };

        Button submitBtn = new Button("Send Complaint ➔");
        submitBtn.setStyle("-fx-background-color: #3A57E8; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 11 28; -fx-background-radius: 8; -fx-font-size: 14px; -fx-cursor: hand;");

        submitBtn.setOnAction(e -> {
            String selectedTarget = recipientCombo.getValue();
            String subj = subjectField.getText().trim();
            String msg = complaintTextArea.getText().trim();

            if (subj.isEmpty() || msg.isEmpty()) {
                feedbackBanner.setText("⚠️ Subject and details are required.");
                feedbackBanner.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackBanner.setVisible(true);
                return;
            }

            boolean isToAdmin = selectedTarget != null && selectedTarget.startsWith("Admin");
            String fullSubject = isToAdmin ? subj : "[Target: " + selectedTarget + "] " + subj;

            com.eudhari.controller.ComplaintController.getInstance().createComplaint(
                    currentSkUid, "SHOPKEEPER", currentSkName, fullSubject, msg
            );

            if (!isToAdmin && selectedTarget != null && customerIdToUidMap.containsKey(selectedTarget)) {
                String targetCustId = customerIdToUidMap.get(selectedTarget);
                NotificationController.getInstance().sendNotification(
                        targetCustId,
                        "customer",
                        currentSkUid,
                        "shopkeeper",
                        "COMPLAINT",
                        "Complaint / Inquiry from " + currentSkName,
                        "Subject: " + subj + "\nMessage: " + msg,
                        ""
                );
                feedbackBanner.setText("✓ Complaint sent directly to " + selectedTarget + " & logged in system!");
            } else {
                feedbackBanner.setText("✓ Complaint submitted to Admin Console! Status: OPEN");
            }

            feedbackBanner.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 13px;");
            feedbackBanner.setVisible(true);

            subjectField.clear();
            complaintTextArea.clear();
            refreshSkComplaints.run();
        });

        HBox btnRow = new HBox(16, submitBtn, feedbackBanner);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        VBox formBox = new VBox(12, recipientLabel, recipientCombo, subjTitleLabel, subjectField, msgLabel, complaintTextArea, btnRow);
        formCard.getChildren().addAll(formTitle, formBox);

        // 3. Submitted Tickets History Card
        VBox historyCard = new VBox(14);
        historyCard.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);");

        Label historyTitle = new Label("Support History & Responses");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        refreshSkComplaints.run();

        historyCard.getChildren().addAll(historyTitle, submittedTicketsContainer);

        mainContent.getChildren().addAll(headerBox, formCard, historyCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F8F9FE; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
    }

    public BorderPane getView() {
        return root;
    }

    private void addFirestoreTicketToHistory(com.eudhari.model.ComplaintModel c) {
        VBox ticketBox = new VBox(6);
        ticketBox.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14;");

        HBox top = new HBox(10);
        Label catLabel = new Label("Subject: " + c.getSubject());
        catLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3A57E8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(c.getStatus());
        if ("RESOLVED".equalsIgnoreCase(c.getStatus())) {
            statusBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        } else if ("IN_PROGRESS".equalsIgnoreCase(c.getStatus())) {
            statusBadge.setStyle("-fx-background-color: #F3E8FF; -fx-text-fill: #7C3AED; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        } else {
            statusBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        }

        String dtStr = c.getCreatedAt() != null && c.getCreatedAt().length() >= 10 ? c.getCreatedAt().substring(0, 10) : "Recent";
        Label dateLabel = new Label(dtStr);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        top.getChildren().addAll(catLabel, spacer, dateLabel, statusBadge);

        Label msgText = new Label(c.getDescription());
        msgText.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F172A;");
        msgText.setWrapText(true);

        ticketBox.getChildren().addAll(top, msgText);

        if (c.getAdminResponse() != null && !c.getAdminResponse().isBlank()) {
            Label replyBox = new Label("💬 Response: " + c.getAdminResponse());
            replyBox.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #3A57E8; -fx-font-size: 12px; " +
                    "-fx-padding: 8 10; -fx-background-radius: 6; -fx-border-color: #C7D2FE; -fx-border-radius: 6;");
            replyBox.setWrapText(true);
            ticketBox.getChildren().add(replyBox);
        }

        submittedTicketsContainer.getChildren().add(0, ticketBox);
    }
}
