package com.eudhari.view.customer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerHelpSupport {

    private final BorderPane root;
    private final VBox submittedTicketsContainer = new VBox(10);

    public CustomerHelpSupport(Runnable backAction) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #070d18; -fx-font-family: 'Segoe UI', sans-serif;");

        VBox mainContent = new VBox(22);
        mainContent.setPadding(new Insets(24));

        // 1. Top Header Row
        Label headerTitle = new Label("🎧 Customer Help & Support");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Button backBtn = new Button("← Back to Dashboard");
        // backBtn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-font-weight: bold; " +
        //         "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        // backBtn.setOnAction(e -> {
        //     if (backAction != null) backAction.run();
        // });

        HBox topRow = new HBox(headerTitle);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);
        topRow.setStyle("-fx-alignment: center-left;");

        // 2. Help Info Banner
        VBox banner = new VBox(6);
        banner.setStyle("-fx-background-color: linear-gradient(to right, #1e3a8a, #0284c7); " +
                "-fx-background-radius: 12; -fx-padding: 18;");
        Label bannerHead = new Label("Have an issue with a shop or payment?");
        bannerHead.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        Label bannerSub = new Label("Write your complaint or support request below. Our Admin team will review and resolve it promptly.");
        bannerSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #e0f2fe;");
        banner.getChildren().addAll(bannerHead, bannerSub);

        // 3. Complaint Writing Form Card
        VBox formCard = new VBox(16);
        formCard.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 22;");

        Label formTitle = new Label("Submit a Complaint / Support Query");
        formTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        com.eudhari.model.UserModel currentUser = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentUserId = currentUser != null && currentUser.getUid() != null ? currentUser.getUid() : "";
        String currentUserName = currentUser != null && currentUser.getName() != null ? currentUser.getName() : "Customer";

        // Subject TextField
        Label subjTitleLabel = new Label("Complaint Subject:");
        subjTitleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject (e.g. Payment issue, Order dispute)");
        subjectField.setPrefHeight(40);
        subjectField.setStyle("-fx-background-color: #131e33; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; " +
                "-fx-border-color: #1e293b; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 12; -fx-font-size: 13px;");

        // Complaint TextArea
        Label msgLabel = new Label("Complaint Details / Description:");
        msgLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");

        TextArea complaintTextArea = new TextArea();
        complaintTextArea.setPromptText("Type your complaint details here...");
        complaintTextArea.setPrefRowCount(5);
        complaintTextArea.setWrapText(true);
        complaintTextArea.setStyle("-fx-control-inner-background: #131e33; -fx-text-fill: white; " +
                "-fx-prompt-text-fill: #64748b; -fx-border-color: #1e293b; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");

        // Status Feedback Banner
        Label feedbackBanner = new Label();
        feedbackBanner.setVisible(false);

        Runnable refreshUserComplaints = () -> {
            submittedTicketsContainer.getChildren().clear();
            java.util.List<com.eudhari.model.ComplaintModel> list = com.eudhari.controller.ComplaintController.getInstance().getComplaintsForUser(currentUserId);
            if (list != null && !list.isEmpty()) {
                for (com.eudhari.model.ComplaintModel c : list) {
                    addFirestoreTicketToHistory(c);
                }
            } else {
                Label emptyLbl = new Label("No complaints or support tickets submitted yet.");
                emptyLbl.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");
                submittedTicketsContainer.getChildren().add(emptyLbl);
            }
        };

        // Submit Button
        Button submitBtn = new Button("Send Complaint to Admin ➔");
        submitBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 11 28; -fx-background-radius: 8; -fx-font-size: 14px; -fx-cursor: hand;");

        submitBtn.setOnAction(e -> {
            String subj = subjectField.getText().trim();
            String msg = complaintTextArea.getText().trim();
            if (subj.isEmpty() || msg.isEmpty()) {
                feedbackBanner.setText("⚠️ Subject and details are required.");
                feedbackBanner.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold; -fx-font-size: 13px;");
                feedbackBanner.setVisible(true);
                return;
            }

            com.eudhari.controller.ComplaintController.getInstance().createComplaint(
                    currentUserId, "CUSTOMER", currentUserName, subj, msg
            );

            feedbackBanner.setText("✓ Complaint stored in Firestore! Status: OPEN");
            feedbackBanner.setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold; -fx-font-size: 13px;");
            feedbackBanner.setVisible(true);

            subjectField.clear();
            complaintTextArea.clear();
            refreshUserComplaints.run();
        });

        HBox btnRow = new HBox(16, submitBtn, feedbackBanner);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        VBox formBox = new VBox(12, subjTitleLabel, subjectField, msgLabel, complaintTextArea, btnRow);
        formCard.getChildren().addAll(formTitle, formBox);

        // 4. Submitted Tickets History Card
        VBox historyCard = new VBox(14);
        historyCard.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 14; -fx-background-radius: 14; -fx-padding: 20;");

        Label historyTitle = new Label("My Support Tickets & Admin Responses");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        refreshUserComplaints.run();

        historyCard.getChildren().addAll(historyTitle, submittedTicketsContainer);

        mainContent.getChildren().addAll(topRow, banner, formCard, historyCard);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #c1e1ff; -fx-border-color: transparent;");

        root.setCenter(scrollPane);
    }

    public BorderPane getView() {
        return root;
    }

    private void addFirestoreTicketToHistory(com.eudhari.model.ComplaintModel c) {
        VBox ticketBox = new VBox(6);
        ticketBox.setStyle("-fx-background-color: #131e33; -fx-border-color: #1e293b; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14;");

        HBox top = new HBox(10);
        Label catLabel = new Label("Subject: " + c.getSubject());
        catLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(c.getStatus());
        if ("RESOLVED".equalsIgnoreCase(c.getStatus())) {
            statusBadge.setStyle("-fx-background-color: #064e3b; -fx-text-fill: #4ade80; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        } else if ("IN_PROGRESS".equalsIgnoreCase(c.getStatus())) {
            statusBadge.setStyle("-fx-background-color: #3b0764; -fx-text-fill: #c084fc; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        } else {
            statusBadge.setStyle("-fx-background-color: #451a03; -fx-text-fill: #fb923c; -fx-font-size: 11px; " +
                    "-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 6;");
        }

        String dtStr = c.getCreatedAt() != null && c.getCreatedAt().length() >= 10 ? c.getCreatedAt().substring(0, 10) : "Recent";
        Label dateLabel = new Label(dtStr);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        top.getChildren().addAll(catLabel, spacer, dateLabel, statusBadge);

        Label msgText = new Label(c.getDescription());
        msgText.setStyle("-fx-font-size: 13px; -fx-text-fill: #e2e8f0;");
        msgText.setWrapText(true);

        ticketBox.getChildren().addAll(top, msgText);

        if (c.getAdminResponse() != null && !c.getAdminResponse().isBlank()) {
            Label replyBox = new Label("💬 Admin Response: " + c.getAdminResponse());
            replyBox.setStyle("-fx-background-color: #0b1329; -fx-text-fill: #a78bfa; -fx-font-size: 12px; " +
                    "-fx-padding: 8 10; -fx-background-radius: 6; -fx-border-color: #1e293b; -fx-border-radius: 6;");
            replyBox.setWrapText(true);
            ticketBox.getChildren().add(replyBox);
        }

        submittedTicketsContainer.getChildren().add(0, ticketBox);
    }
}
