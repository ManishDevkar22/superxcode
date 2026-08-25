package com.eudhari.view.admin;

import com.eudhari.controller.AdminController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminComplaintsView {

    private final BorderPane root;

    public static class ComplaintItem {
        String id;
        String senderName;
        String senderRole; // "Customer" or "Shopkeeper"
        String category;
        String date;
        String message;
        String status; // "NEW", "PENDING", "RESOLVED"
        String adminReply;

        public ComplaintItem(String id, String senderName, String senderRole, String category, String date, String message, String status, String adminReply) {
            this.id = id;
            this.senderName = senderName;
            this.senderRole = senderRole;
            this.category = category;
            this.date = date;
            this.message = message;
            this.status = status;
            this.adminReply = adminReply;
        }

        @Override
        public String toString() {
            return "[" + senderRole.toUpperCase() + "] " + senderName + " • " + category;
        }
    }

    private final ObservableList<com.eudhari.model.ComplaintModel> complaintList = FXCollections.observableArrayList();

    public AdminComplaintsView(AdminController controller, Runnable backAction) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #F8FAFC; -fx-font-family: 'Segoe UI', sans-serif;");

        Runnable refreshList = () -> {
            complaintList.clear();
            java.util.List<com.eudhari.model.ComplaintModel> list = com.eudhari.controller.ComplaintController.getInstance().getAllComplaints();
            if (list != null) {
                complaintList.addAll(list);
            }
        };

        refreshList.run();

        // Main Layout: Split (Left: Complaints List, Right: Complaint Detail & Admin Reply Box)

        HBox splitLayout = new HBox(20);
        splitLayout.setPadding(new Insets(20));
        splitLayout.setStyle("-fx-background-color : #c1e1ff");

        // LEFT SIDE: Complaints ListView Card
        VBox leftCard = new VBox(14);
        leftCard.setPrefWidth(380);
        leftCard.setMinWidth(350);
        leftCard.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");

        Label listTitle = new Label("📥 Received Complaints & Queries");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label listSub = new Label("Select a complaint below to review details and send a response.");
        listSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        listSub.setWrapText(true);

        ListView<com.eudhari.model.ComplaintModel> listView = new ListView<>(complaintList);
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        listView.setCellFactory(lv -> new ListCell<com.eudhari.model.ComplaintModel>() {
            @Override
            protected void updateItem(com.eudhari.model.ComplaintModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(4);
                    HBox top = new HBox(8);

                    String roleStr = item.getUserRole() != null ? item.getUserRole().toUpperCase() : "USER";
                    Label roleBadge = new Label(roleStr);
                    if ("CUSTOMER".equalsIgnoreCase(roleStr)) {
                        roleBadge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1D4ED8; -fx-font-size: 10px; " +
                                "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
                    } else {
                        roleBadge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-size: 10px; " +
                                "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
                    }

                    Label nameLbl = new Label(item.getName() != null ? item.getName() : "User");
                    nameLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);

                    String stStr = item.getStatus() != null ? item.getStatus() : "OPEN";
                    Label statusBadge = new Label(stStr);
                    if ("RESOLVED".equalsIgnoreCase(stStr)) {
                        statusBadge.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-font-size: 10px; " +
                                "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
                    } else if ("IN_PROGRESS".equalsIgnoreCase(stStr)) {
                        statusBadge.setStyle("-fx-background-color: #F3E8FF; -fx-text-fill: #7E22CE; -fx-font-size: 10px; " +
                                "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
                    } else {
                        statusBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-size: 10px; " +
                                "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
                    }

                    top.getChildren().addAll(roleBadge, nameLbl, sp, statusBadge);

                    String dtStr = item.getCreatedAt() != null && item.getCreatedAt().length() >= 10 ? item.getCreatedAt().substring(0, 10) : "";
                    Label catLbl = new Label(item.getSubject() + " • " + dtStr);
                    catLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

                    box.getChildren().addAll(top, catLbl);
                    box.setPadding(new Insets(6, 4, 6, 4));
                    setGraphic(box);
                }
            }
        });

        leftCard.getChildren().addAll(listTitle, listSub, listView);

        // RIGHT SIDE: Selected Complaint Details & Response Section Card

        VBox rightCard = new VBox(16);
        HBox.setHgrow(rightCard, Priority.ALWAYS);
        rightCard.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 22;");

        Label detailTitle = new Label("Complaint Details & Admin Action");
        detailTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        // Target Info Banner
        VBox targetBanner = new VBox(4);
        targetBanner.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: #CBD5E1; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 14;");

        Label targetNameLabel = new Label("Sender: Select a complaint from the left list");
        targetNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label targetMetaLabel = new Label("Role & Category: -");
        targetMetaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        targetBanner.getChildren().addAll(targetNameLabel, targetMetaLabel);

        // Full Complaint Message Card
        VBox msgCard = new VBox(6);
        msgCard.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 14;");
        Label msgHeading = new Label("Complaint Description:");
        msgHeading.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        Label complaintBodyLabel = new Label("No complaint selected.");
        complaintBodyLabel.setWrapText(true);
        complaintBodyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1E293B;");
        msgCard.getChildren().addAll(msgHeading, complaintBodyLabel);

        // Admin Response Area

        VBox responseBox = new VBox(10);

        Label recipientNoticeLabel = new Label("Replying to: -");
        recipientNoticeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

        HBox statusUpdateRow = new HBox(10);
        statusUpdateRow.setAlignment(Pos.CENTER_LEFT);
        Label statusSelectLabel = new Label("Update Status:");
        statusSelectLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("OPEN", "IN_PROGRESS", "RESOLVED"));
        statusCombo.setValue("RESOLVED");
        statusCombo.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6;");
        statusUpdateRow.getChildren().addAll(statusSelectLabel, statusCombo);

        TextArea responseTextArea = new TextArea();
        responseTextArea.setPromptText("Write your administrative response or resolution message here...");
        responseTextArea.setPrefRowCount(4);
        responseTextArea.setWrapText(true);
        responseTextArea.setStyle("-fx-control-inner-background: #FFFFFF; -fx-text-fill: #0F172A; " +
                "-fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");

        Label feedbackBanner = new Label();
        feedbackBanner.setVisible(false);

        Button sendResponseBtn = new Button("Send Response ➔");
        sendResponseBtn.setStyle("-fx-background-color: #3A57E8; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " +
                "-fx-padding: 10 24; -fx-background-radius: 8; -fx-font-size: 13px; -fx-cursor: hand;");

        HBox actionRow = new HBox(14, sendResponseBtn, feedbackBanner);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        responseBox.getChildren().addAll(recipientNoticeLabel, statusUpdateRow, responseTextArea, actionRow);

        rightCard.getChildren().addAll(detailTitle, targetBanner, msgCard, responseBox);

        splitLayout.getChildren().addAll(leftCard, rightCard);

        // Selection Listener
        
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedItem) -> {
            if (selectedItem != null) {
                targetNameLabel.setText("Sender: " + selectedItem.getName() + " (" + selectedItem.getUserRole() + ")");
                String dtStr = selectedItem.getCreatedAt() != null && selectedItem.getCreatedAt().length() >= 10 ? selectedItem.getCreatedAt().substring(0, 10) : "";
                targetMetaLabel.setText("Ticket ID: " + selectedItem.getComplaintId() + "  •  Subject: " + selectedItem.getSubject() + "  •  Date: " + dtStr);
                complaintBodyLabel.setText(selectedItem.getDescription());
                recipientNoticeLabel.setText("Replying to: " + selectedItem.getName() + " (" + selectedItem.getUserRole() + ")");
                statusCombo.setValue(selectedItem.getStatus() != null ? selectedItem.getStatus() : "RESOLVED");
                feedbackBanner.setVisible(false);

                if (selectedItem.getAdminResponse() != null && !selectedItem.getAdminResponse().isBlank()) {
                    responseTextArea.setText(selectedItem.getAdminResponse());
                } else {
                    responseTextArea.clear();
                }
            }
        });

        sendResponseBtn.setOnAction(e -> {
            com.eudhari.model.ComplaintModel selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                feedbackBanner.setText("⚠️ Please select a complaint from the list first.");
                feedbackBanner.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 12px;");
                feedbackBanner.setVisible(true);
                return;
            }

            String replyText = responseTextArea.getText().trim();
            String newStatus = statusCombo.getValue();
            if (replyText.isEmpty()) {
                feedbackBanner.setText("⚠️ Please enter a response message before sending.");
                feedbackBanner.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 12px;");
                feedbackBanner.setVisible(true);
                return;
            }

            com.eudhari.controller.ComplaintController.getInstance().updateComplaintByAdmin(
                    selectedItem.getComplaintId(), newStatus, replyText
            );

            feedbackBanner.setText("✓ Response saved to Firestore & Notification sent to " + selectedItem.getName() + "!");
            feedbackBanner.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-font-size: 12px;");
            feedbackBanner.setVisible(true);

            refreshList.run();
        });

        if (!complaintList.isEmpty()) {
            listView.getSelectionModel().select(0);
        }

        root.setCenter(splitLayout);
    }

    public BorderPane getView() {
        return root;
    }
}
