package com.eudhari.view.customer;

import javafx.collections.FXCollections;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;
import javafx.scene.text.Text;


public class PayUdhari {

    private final BorderPane root;

    public PayUdhari(Runnable backAction) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #f7f9fc; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Top bar (with Back button)
        // Label bell = new Label("🔔");
        // bell.setStyle("-fx-font-size: 18px;");
        // Label wallet = new Label("▣");
        // wallet.setStyle("-fx-font-size: 18px;");
        // Button payNow = new Button("Pay Now");
        // payNow.setStyle("-fx-background-color: #123aa0; -fx-text-fill: white;
        // -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");
        // Button backButton = new Button("\u2190 Back to Dashboard");
        // backButton.setStyle("-fx-background-color: #123aa0; -fx-text-fill: white;
        // -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;
        // -fx-cursor: hand;");
        // backButton.setOnAction(e -> backAction.run());
        // Label avatar = new Label("O");
        // avatar.setStyle("-fx-background-color: #cbd5e1; -fx-font-weight: bold;
        // -fx-padding: 8; -fx-background-radius: 50;");

        // Region topSpacer = new Region();
        // HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // HBox topBar = new HBox(22, backButton, topSpacer, bell, wallet, payNow,
        // avatar);
        // topBar.setAlignment(Pos.CENTER_LEFT);
        // topBar.setPadding(new Insets(18, 35, 15, 35));
        // topBar.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb;
        // -fx-border-width: 0 0 1 0;");
        // borderPane.setTop(topBar);

        // Center title
        Text pageTitle = new Text("Settle Outstanding Dues");
        pageTitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-fill: #10223b;");
        Text pageSubtitle = new Text("Complete your payment securely via UPI or QR scan.");
        pageSubtitle.setStyle("-fx-font-size: 14px; -fx-fill: #5d6472;");
        VBox pageHeader = new VBox(5, pageTitle, pageSubtitle);

        // Settlement amount card
        VBox amountCard = new VBox(18);
        amountCard.setPrefSize(280, 260);
        amountCard.setPadding(new Insets(25));
        amountCard.setStyle(
                "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 0 4; -fx-border-radius: 12; -fx-background-radius: 12;");
        Label settlementTitle = new Label("TOTAL SETTLEMENT AMOUNT");
        settlementTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #5d6472; -fx-font-weight: bold;");
        Label settlementAmount = new Label("₹4000");
        settlementAmount.setStyle("-fx-font-size: 46px; -fx-text-fill: #0f37a0; -fx-font-weight: bold;");
        Label balance = new Label("REMAINING BALANCE: ₹2,000");
        balance.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        Label invoiceNote = new Label("ⓘ  Includes 3 pending invoices\n     from Green Valley Mart.");
        invoiceNote.setStyle(
                "-fx-background-color: #eff4ff; -fx-text-fill: #254fbd; -fx-padding: 12; -fx-background-radius: 8; -fx-font-size: 13px;");
        amountCard.getChildren().addAll(settlementTitle, settlementAmount, balance, invoiceNote);

        VBox secureCard = new VBox(4);
        secureCard.setPrefWidth(280);
        secureCard.setPadding(new Insets(18));
        secureCard.setStyle(
                "-fx-background-color: #e0eff0; -fx-border-color: #b9d5d7; -fx-background-radius: 12; -fx-border-radius: 12;");
        Label secureTitle = new Label("✓  Secure Payment");
        secureTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #065f46;");
        Label secureSub = new Label("     SSL Encrypted & PCI-DSS\n     Compliant");
        secureSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
        secureCard.getChildren().addAll(secureTitle, secureSub);
        VBox leftColumn = new VBox(22, amountCard, secureCard);

        // Select UPI app card
        VBox upiCard = new VBox(12);
        upiCard.setPrefSize(280, 390);
        upiCard.setPadding(new Insets(25));
        upiCard.setStyle(
                "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-background-radius: 12; -fx-border-radius: 12;");
        Label upiTitle = new Label("Select UPI App");
        upiTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #10223b;");
        ToggleGroup upiApps = new ToggleGroup();
        RadioButton phonePe = new RadioButton("PhonePe");
        RadioButton googlePay = new RadioButton("Google Pay");
        RadioButton bhimUpi = new RadioButton("BHIM UPI");
        phonePe.setToggleGroup(upiApps);
        googlePay.setToggleGroup(upiApps);
        bhimUpi.setToggleGroup(upiApps);
        phonePe.setSelected(true);
        for (RadioButton button : new RadioButton[] { phonePe, googlePay, bhimUpi }) {
            button.setPrefWidth(230);
            button.setPadding(new Insets(14));
            button.setStyle(
                    "-fx-border-color: #cbd5e1; -fx-border-radius: 10; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
        Label upiIdLabel = new Label("OR ENTER UPI ID");
        upiIdLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #5d6472;");
        TextField upiId = new TextField();
        upiId.setPromptText("username@upi");
        upiId.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 7; -fx-padding: 10;");
        Button confirmPayment = new Button("Confirm Payment  >");
        confirmPayment.setMaxWidth(Double.MAX_VALUE);
        confirmPayment.setStyle(
                "-fx-background-color: #0f37a0; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 14; -fx-cursor: hand;");

        upiCard.getChildren().addAll(upiTitle, phonePe, googlePay, bhimUpi, upiIdLabel, upiId, confirmPayment);

        HBox paymentRow = new HBox(25, leftColumn, upiCard);

        // Bottom payment history table
        Label historyTitle = new Label("Payment History Preview");
        historyTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #10223b;");

        TableView<PayudhariModel> historyTable = new TableView<>();
        historyTable.setPrefHeight(270);
        historyTable.setStyle(
                "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-background-radius: 12;");
        TableColumn<PayudhariModel, String> dateColumn = new TableColumn<>("DATE");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<PayudhariModel, String> shopColumn = new TableColumn<>("SHOP NAME");
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        TableColumn<PayudhariModel, String> methodColumn = new TableColumn<>("METHOD");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));
        TableColumn<PayudhariModel, String> amountColumn = new TableColumn<>("AMOUNT");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<PayudhariModel, String> statusColumn = new TableColumn<>("STATUS");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        historyTable.getColumns().addAll(dateColumn, shopColumn, methodColumn, amountColumn, statusColumn);
        historyTable.setItems(FXCollections.observableArrayList());

        Label paymentMessage = new Label();
        paymentMessage.setStyle("-fx-text-fill: #047857; -fx-font-weight: bold; -fx-font-size: 14px;");
        paymentMessage.setVisible(false);
        paymentMessage.setManaged(false);

        com.eudhari.model.UserModel currentCust = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        List<com.eudhari.model.UdhariModel> pendingUdhariList = com.eudhari.controller.UdhariController.getInstance().getUdhariForCustomer(currentCustId);
        double totalSettlement = 0.0;
        for (com.eudhari.model.UdhariModel u : pendingUdhariList) {
            totalSettlement += u.getRemainingAmount();
        }
        settlementAmount.setText(String.format("₹%.2f", totalSettlement));

        confirmPayment.setOnAction(event -> {
            String app = ((RadioButton) upiApps.getSelectedToggle()).getText();
            String enteredUpiId = upiId.getText().trim();
            String paymentMethod = enteredUpiId.isEmpty() ? app : enteredUpiId;

            List<com.eudhari.model.UdhariModel> activeRecords = com.eudhari.controller.UdhariController.getInstance().getUdhariForCustomer(currentCustId);
            if (activeRecords.isEmpty()) {
                paymentMessage.setText("No pending Udhari records to pay!");
                paymentMessage.setVisible(true);
                paymentMessage.setManaged(true);
                return;
            }

            // Pay the first pending record
            com.eudhari.model.UdhariModel targetRecord = activeRecords.get(0);
            double amountToPay = targetRecord.getRemainingAmount();

            boolean paid = com.eudhari.controller.UdhariController.getInstance().payUdhari(targetRecord.getUdhariId(), amountToPay);
            if (paid) {
                paymentMessage.setText("Payment of ₹" + String.format("%.2f", amountToPay) + " confirmed for " + targetRecord.getShopName() + " using " + paymentMethod + ".");
                paymentMessage.setVisible(true);
                paymentMessage.setManaged(true);

                historyTable.getItems().add(0, new PayudhariModel("Today", targetRecord.getShopName(), paymentMethod, String.format("₹%.2f", amountToPay), "Success"));
                settlementAmount.setText("₹0.00");
            } else {
                paymentMessage.setText("Failed to process payment. Please try again.");
                paymentMessage.setVisible(true);
                paymentMessage.setManaged(true);
            }
        });
        // payNow.setOnAction(event -> confirmPayment.fire());

        VBox centerContent = new VBox(28, pageHeader, paymentRow, historyTitle, historyTable, paymentMessage);
        centerContent.setPadding(new Insets(35, 38, 30, 38));
        centerContent.setStyle("-fx-background-color: #c1e1ff;");
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        borderPane.setCenter(centerContent);

        this.root = borderPane;
    }

    public BorderPane getView() {
        return root;
    }

    public static class PayudhariModel {
        private final SimpleStringProperty date;
        private final SimpleStringProperty shopName;
        private final SimpleStringProperty method;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty status;

        public PayudhariModel(String date, String shopName, String method, String amount, String status) {
            this.date = new SimpleStringProperty(date);
            this.shopName = new SimpleStringProperty(shopName);
            this.method = new SimpleStringProperty(method);
            this.amount = new SimpleStringProperty(amount);
            this.status = new SimpleStringProperty(status);
        }

        public String getDate() {
            return date.get();
        }

        public String getShopName() {
            return shopName.get();
        }

        public String getMethod() {
            return method.get();
        }

        public String getAmount() {
            return amount.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
}
