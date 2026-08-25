package com.eudhari.view.customer;

import com.eudhari.controller.ConnectionRequestController;
import com.eudhari.controller.ProfileController;
import com.eudhari.controller.shopkeppercontroller.ShopController;
import com.eudhari.model.ConnectionRequestModel;
import com.eudhari.model.ShopModel;
import com.eudhari.model.UserModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;


public class ConnectedShops {

    private final BorderPane root;

    public ConnectedShops(Runnable backAction) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #070d18; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        StackPane centerStack = new StackPane();

        VBox mainContent = new VBox(22);
        mainContent.setStyle("-fx-padding: 30 40 30 40;");

        Text title = new Text("Connected Shops & Requests");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #ffffff;");
        Text subtitle = new Text("Manage your active shop connections and pending requests.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-fill: #94a3b8;");
        VBox header = new VBox(5, title, subtitle);

        // Fetch current customer profile & approved requests
        UserModel currentCust = ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        List<ConnectionRequestModel> approvedRequests = ConnectionRequestController.getInstance().getApprovedConnectedShopsForCustomer(currentCustId);
        List<ConnectionRequestModel> allCustRequests = ConnectionRequestController.getInstance().getRequestsByCustomer(currentCustId);

        // Stats Box
        VBox statsBox = new VBox(12);
        statsBox.setPrefWidth(280);
        statsBox.setStyle("-fx-background-color: #0e1726; -fx-background-radius: 12; -fx-border-color: #1e293b; -fx-border-radius: 12; -fx-padding: 20;");
        Text statsTitle = new Text("📊 Connection Summary");
        statsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: white;");
        
        HBox row1 = createStatRow("Total Requests Sent", String.valueOf(allCustRequests.size()), false);
        HBox row2 = createStatRow("Active Connected Shops", String.valueOf(approvedRequests.size()), true);
        statsBox.getChildren().addAll(statsTitle, row1, row2);

        VBox promoBanner = new VBox(10);
        promoBanner.setStyle("-fx-background-color: linear-gradient(to right, #1e3a8a, #3b82f6); -fx-background-radius: 12; -fx-padding: 25;");
        Text promoTitle = new Text("Connect with Local Shops");
        promoTitle.setStyle("-fx-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Text promoSub = new Text("Request credit limits with trusted shopkeepers in your locality.");
        promoSub.setStyle("-fx-fill: #bfdbfe; -fx-font-size: 13px;");
        promoBanner.getChildren().addAll(promoTitle, promoSub);
        HBox.setHgrow(promoBanner, Priority.ALWAYS);

        HBox topSummaryRow = new HBox(20, statsBox, promoBanner);

        // Connected Shops Cards Container
        HBox cardsContainer = new HBox(20);
        cardsContainer.setStyle("-fx-padding: 10 0 10 0;");

        if (approvedRequests != null && !approvedRequests.isEmpty()) {
            for (ConnectionRequestModel req : approvedRequests) {
                ShopModel shop = ShopController.getInstance().getShopById(req.getShopId());
                VBox card = createConnectedShopCard(req, shop, centerStack, mainContent);
                cardsContainer.getChildren().add(card);
            }
        } else {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 30;");
            Text emptyText = new Text("No approved connected shops yet.");
            emptyText.setStyle("-fx-fill: #94a3b8; -fx-font-size: 15px; -fx-font-weight: bold;");
            Text emptySub = new Text("Go to Customer Dashboard to browse active shops and send connection requests!");
            emptySub.setStyle("-fx-fill: #64748b; -fx-font-size: 13px;");
            emptyBox.getChildren().addAll(emptyText, emptySub);
            cardsContainer.getChildren().add(emptyBox);
        }

        ScrollPane cardsScroll = new ScrollPane(cardsContainer);
        cardsScroll.setFitToHeight(true);
        cardsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        cardsScroll.setStyle("-fx-background-color: transparent; -fx-background: #070d18; -fx-border-color: transparent; -fx-padding: 0;");

        mainContent.getChildren().addAll(header, topSummaryRow, cardsScroll);
        centerStack.getChildren().add(mainContent);

        ScrollPane outerScroll = new ScrollPane(centerStack);
        outerScroll.setFitToWidth(true);
        outerScroll.setStyle("-fx-background-color: transparent; -fx-background: #c1e1ff; -fx-border-color: transparent;");

        borderPane.setCenter(outerScroll);
        this.root = borderPane;
    }

    private HBox createStatRow(String label, String val, boolean highlighted) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: " + (highlighted ? "#4ade80" : "#cbd5e1") + "; -fx-font-size: 12px;");
        Label v = new Label(val);
        v.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox row = new HBox(l, sp, v);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(highlighted ? "-fx-background-color: #064e3b; -fx-background-radius: 8; -fx-padding: 10 12;" : "-fx-background-color: #131e33; -fx-background-radius: 8; -fx-padding: 10 12;");
        return row;
    }

    private VBox createConnectedShopCard(ConnectionRequestModel req, ShopModel shop, StackPane centerStack, VBox mainContent) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: #0e1726; -fx-border-color: #1e293b; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 20;");
        card.setPrefWidth(340);
        card.setMinWidth(340);

        String shopNameStr = shop != null && shop.getShopName() != null ? shop.getShopName() : req.getShopName();
        String addressStr = shop != null && shop.getAddress() != null ? shop.getAddress() : "Local Area";
        String catStr = shop != null && shop.getBusinessCategory() != null ? shop.getBusinessCategory() : "Grocery";

        Circle avatar = new Circle(22.5, Color.web("#2563eb"));
        Text sName = new Text(shopNameStr);
        sName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: white;");
        Text sLoc = new Text("📍 " + addressStr);
        sLoc.setStyle("-fx-fill: #94a3b8; -fx-font-size: 12px;");

        Label badge = new Label("● CONNECTED");
        badge.setStyle("-fx-background-color: #064e3b; -fx-text-fill: #4ade80; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 12;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        HBox cardHeader = new HBox(12, avatar, new VBox(2, sName, sLoc), sp, badge);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label statusIcon = new Label("✓");
        statusIcon.setStyle("-fx-background-color: #064e3b; -fx-text-fill: #4ade80; -fx-padding: 8 12; -fx-background-radius: 25; -fx-font-size: 16px; -fx-font-weight: bold;");
        Text statusTitle = new Text("Active Credit Relationship");
        statusTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: white;");
        Text statusDesc = new Text("Shopkeeper approved your connection request.\nCategory: " + catStr);
        statusDesc.setStyle("-fx-fill: #94a3b8; -fx-font-size: 12px;");

        HBox statusBox = new HBox(12, statusIcon, new VBox(4, statusTitle, statusDesc));
        statusBox.setStyle("-fx-background-color: #131e33; -fx-border-color: #1e293b; -fx-border-radius: 8; -fx-padding: 12;");

        Button viewShopBtn = new Button("View Shop Products  >");
        viewShopBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        viewShopBtn.setOnAction(event -> {
            shopProductView productView = new shopProductView(shopNameStr, () -> {
                centerStack.getChildren().set(0, mainContent);
            });
            centerStack.getChildren().set(0, productView.getView());
        });

        HBox footer = new HBox(viewShopBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(cardHeader, statusBox, footer);
        return card;
    }

    public BorderPane getView() {
        return root;
    }
}