package com.eudhari.view.customer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import java.util.List;
import javafx.scene.text.Text;

/**
 * Reusable "My Udhari" view. No Stage/Scene of its own.
 * Its own left navigation sidebar was removed (duplicated Dashboard's).
 * backAction -> returns to Dashboard center.
 * payAction -> switches Dashboard center to PayUdhari (wired to "Pay Now").
 */
public class Myudhari {

    private final BorderPane root;

    public Myudhari(Runnable backAction, Runnable payAction) {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #F8FAFC;");

        // --- TOP BAR (with Back button) ---
        // Text t1 = new Text("My Udhaari");
        // t1.setFont(Font.font("Arial", 22));
        // t1.setStyle("-fx-font-weight: bold;");
        // t1.setFill(Color.web("#1E293B"));

        // Label wallet = new Label("\uD83D\uDCB3");
        // wallet.setFont(Font.font(20));
        // Label notification = new Label("\uD83D\uDD14");
        // notification.setFont(Font.font(20));

        // Button backButton = new Button("\u2190 Back to Dashboard");
        // backButton.setStyle("-fx-background-color: #1E3A8A; -fx-text-fill: white;
        // -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;
        // -fx-cursor: hand;");
        // backButton.setOnAction(e -> backAction.run());

        // Label userName = new Label("Omkar Sonawane");
        // userName.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:
        // #1E293B;");
        // Label statusLabel = new Label("Gold Member");
        // statusLabel.setStyle("-fx-font-size:11px; -fx-text-fill:gray;");
        // VBox userDetails = new VBox(userName, statusLabel);
        // Circle userAvatar = new Circle(20, Color.web("#CBD5E1"));
        // HBox profileBox = new HBox(10, userAvatar, userDetails);
        // profileBox.setAlignment(Pos.CENTER_LEFT);

        // Region topSpacer = new Region();
        // HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // HBox topBar = new HBox(20, topSpacer );//wallet, notification, backButton);
        // topBar.setStyle("-fx-padding: 15px 25px; -fx-background-color: white;
        // -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        // topBar.setAlignment(Pos.CENTER_LEFT);
        // bp.setTop(topBar);

        // --- CENTER CONTENT ---
        Label dashTitle = new Label("My Udhaari Dashboard");
        dashTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label dashSubTitle = new Label("Real-time overview of your active credit lines and payment health.");
        dashSubTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        VBox headerBox = new VBox(5, dashTitle, dashSubTitle);

        Label lbl1Title = new Label("TOTAL OUTSTANDING");
        lbl1Title.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label lbl1Val = new Label("\u20B96,000.00");
        lbl1Val.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        Label lbl1Sub = new Label("\u2197 12% from last month");
        lbl1Sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #DC2626;");
        VBox card1 = new VBox(8, lbl1Title, lbl1Val, lbl1Sub);
        card1.setPrefSize(260, 110);
        card1.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label lbl2Title = new Label("REMAINING UDHARI");
        lbl2Title.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label lbl2Val = new Label("\u20B94,000.00");
        lbl2Val.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        Label lbl2Sub = new Label("Available credit balance");
        lbl2Sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        VBox card2 = new VBox(8, lbl2Title, lbl2Val, lbl2Sub);
        card2.setPrefSize(260, 110);
        card2.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label lbl3Title = new Label("UDHARI LIMIT");
        lbl3Title.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label lbl3Val = new Label("\u20B910,000");
        lbl3Val.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Button incLimitBtn = new Button("Increase Limit");
        incLimitBtn.setMaxWidth(Double.MAX_VALUE);
        incLimitBtn.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #2563EB; -fx-border-radius: 6px; -fx-text-fill: #2563EB; -fx-font-weight: bold;");
        VBox card3 = new VBox(8, lbl3Title, lbl3Val, incLimitBtn);
        card3.setPrefSize(260, 110);
        card3.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        HBox metricsHBox = new HBox(20, card1, card2, card3);

        Label tableTitle = new Label("Active Dues & Settlements");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        TableView<DueTransaction> table = new TableView<>();
        table.setPrefHeight(280);
        table.setMaxWidth(Double.MAX_VALUE);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        TableColumn<DueTransaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<DueTransaction, String> shopCol = new TableColumn<>("Shop Name");
        shopCol.setCellValueFactory(new PropertyValueFactory<>("shopName"));

        TableColumn<DueTransaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<DueTransaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<DueTransaction, String> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button payNowButton = new Button("Pay Now");
            {
                payNowButton.setStyle(
                        "-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px;");
                payNowButton.setOnAction(event -> {
                    DueTransaction item = getTableView().getItems().get(getIndex());
                    System.out.println("Processing payment for: " + item.getShopName());
                    // Route to the PayUdhari page inside the same Dashboard BorderPane
                    payAction.run();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                setGraphic(empty ? null : payNowButton);
            }
        });

        table.getColumns().addAll(dateCol, shopCol, amountCol, statusCol, actionCol);
        com.eudhari.model.UserModel currentCust = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        List<com.eudhari.model.UdhariModel> liveUdhari = com.eudhari.controller.UdhariController.getInstance().getUdhariForCustomer(currentCustId);
        javafx.collections.ObservableList<DueTransaction> tableData = FXCollections.observableArrayList();

        double totalOutstanding = 0.0;
        if (liveUdhari != null && !liveUdhari.isEmpty()) {
            for (com.eudhari.model.UdhariModel u : liveUdhari) {
                totalOutstanding += u.getRemainingAmount();
                String dt = u.getCreatedAt() != null && u.getCreatedAt().length() >= 10 ? u.getCreatedAt().substring(0, 10) : "Recent";
                tableData.add(new DueTransaction(
                        dt,
                        u.getShopName(),
                        String.format("₹%.2f", u.getRemainingAmount()),
                        u.getStatus()
                ));
            }
        }
        lbl1Val.setText(String.format("₹%.2f", totalOutstanding));
        lbl2Val.setText(String.format("₹%.2f", Math.max(0.0, 10000.0 - totalOutstanding)));
        table.setItems(tableData);

        VBox centerContent = new VBox(20, headerBox, metricsHBox, tableTitle, table);
        centerContent.setPadding(new Insets(25));
        VBox.setVgrow(table, Priority.ALWAYS);
        bp.setCenter(centerContent);

        this.root = bp;
    }

    public BorderPane getView() {
        return root;
    }

    public static class DueTransaction {
        private final SimpleStringProperty date;
        private final SimpleStringProperty shopName;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty status;

        public DueTransaction(String date, String shopName, String amount, String status) {
            this.date = new SimpleStringProperty(date);
            this.shopName = new SimpleStringProperty(shopName);
            this.amount = new SimpleStringProperty(amount);
            this.status = new SimpleStringProperty(status);
        }

        public String getDate() {
            return date.get();
        }

        public String getShopName() {
            return shopName.get();
        }

        public String getAmount() {
            return amount.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
}
