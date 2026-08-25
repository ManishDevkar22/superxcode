package com.eudhari.view.customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reusable "Notifications" view. Same pattern as PayUdhari:
 * - no Application / Stage / Scene of its own
 * - no sidebar of its own (the Dashboard sidebar stays visible)
 * - exposes getView() so the Dashboard can drop it into its center
 * - backAction -> returns to Dashboard center
 * - payUdhariAction -> tells the Dashboard to swap center to PayUdhari
 *
 * Everything (data, top bar, header, stat cards, filters, the scrollable
 * notification list, and quick actions) is built inside the constructor,
 * as one method. Repeated bits of logic (styling a filter button, looking
 * up a category's icon/color, building one notification card, showing an
 * info alert, refreshing the list) are kept as local lambda variables
 * instead of separate private methods, so there is still only one place
 * doing the work - it's just not copy-pasted four times.
 */
public class Notifications {

    private final BorderPane root;

    // ===== NAVIGATION ADDED =====
    public Notifications(Runnable backAction, Runnable payUdhariAction,
            Runnable connectedShopsAction) {

        // Fetch live notifications from Firestore
        com.eudhari.model.UserModel currentCust = com.eudhari.controller.ProfileController.getInstance().getCurrentUserProfile();
        String currentCustId = currentCust != null && currentCust.getUid() != null ? currentCust.getUid() : "";

        List<com.eudhari.model.NotificationModel> liveNotifs = com.eudhari.controller.NotificationController.getInstance().getNotificationsForUser(currentCustId);
        ObservableList<NotificationModel> allNotifications = FXCollections.observableArrayList();

        if (liveNotifs != null && !liveNotifs.isEmpty()) {
            for (com.eudhari.model.NotificationModel n : liveNotifs) {
                String cat = n.getType() != null ? n.getType().toUpperCase() : "SYSTEM";
                if ("CONNECTION".equals(cat)) cat = "SHOP";
                if ("ORDER".equals(cat) || "BILLING".equals(cat)) cat = "UDHAARI";
                String action = !n.isRead() ? "Mark Read" : null;
                String timeStr = n.getCreatedAt() != null && n.getCreatedAt().length() >= 10 ? n.getCreatedAt().substring(0, 10) : "Recent";
                allNotifications.add(new NotificationModel(
                        n.getTitle(),
                        n.getMessage(),
                        timeStr,
                        cat,
                        action,
                        !n.isRead()
                ));
            }
        } else {
            allNotifications.add(new NotificationModel(
                    "Welcome to eUdhari",
                    "Your notifications will appear here in real-time as you order and manage udhari.",
                    "Today",
                    "SHOP",
                    null,
                    false
            ));
        }

        // ===================== MUTABLE STATE =====================
        // currentFilter needs to change inside a button's onAction lambda, and a
        // lambda can only capture "effectively final" variables - so instead of a
        // plain String we use a 1-element array as a mutable holder.
        String[] currentFilter = { "All" };
        Map<String, Button> filterButtons = new LinkedHashMap<>();

        VBox notificationListBox = new VBox(14);
        notificationListBox.setPadding(new Insets(4, 4, 4, 2));

        TextField searchField = new TextField();
        searchField.setPromptText("Search notifications...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-color: #f1f3f7; -fx-background-radius: 16; -fx-padding: 6 14;");

        Label unreadStatValue = new Label();
        Label paymentsStatValue = new Label();
        Label udhaariStatValue = new Label();
        Label securityStatValue = new Label();

        // ===================== SMALL REUSABLE LOOKUPS =====================
        Function<String, String> categoryIcon = category -> {
            switch (category) {
                case "UDHAARI":
                    return "\uD83D\uDCD8";
                case "PAYMENT":
                    return "\u2713";
                case "REMINDER":
                    return "\u23F0";
                case "OFFER":
                    return "\uD83C\uDFF7";
                case "SHOP":
                    return "\uD83C\uDFEC";
                case "SECURITY":
                    return "\uD83D\uDEE1";
                default:
                    return "\u2022";
            }
        };
        Function<String, String> categoryIconColor = category -> {
            switch (category) {
                case "UDHAARI":
                    return "#0f37a0";
                case "PAYMENT":
                    return "#059669";
                case "REMINDER":
                    return "#dc2626";
                case "SECURITY":
                    return "#dc2626";
                default:
                    return "#8a93a3";
            }
        };
        Function<String, String> categoryBadgeColor = category -> {
            switch (category) {
                case "UDHAARI":
                    return "#e0e7ff";
                case "PAYMENT":
                    return "#d1fae5";
                case "REMINDER":
                    return "#fee2e2";
                case "SECURITY":
                    return "#fee2e2";
                default:
                    return "#f3f4f6";
            }
        };
        Function<String, String> categoryTextColor = category -> {
            switch (category) {
                case "UDHAARI":
                    return "#3730a3";
                case "PAYMENT":
                    return "#047857";
                case "REMINDER":
                    return "#b91c1c";
                case "SECURITY":
                    return "#b91c1c";
                default:
                    return "#4b5563";
            }
        };

        // Shows a simple info alert. headerText is used for both the alert title
        // and header (Part 12: View Details / View Receipt / Review Activity / etc).
        BiConsumer<String, String> showInfo = (headerText, contentText) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(headerText);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.showAndWait();
        };

        // Decides what a notification's action button actually does (Part 12).
        Consumer<NotificationModel> handleAction = n -> {
            if (n.getActionLabel() == null) {
                return;
            }
            switch (n.getActionLabel()) {
                case "Pay Now":
                    // Udhaari Reminder card's "Pay Now" -> open existing PayUdhari view (Part 6)
                    payUdhariAction.run();
                    break;
                case "View Details":
                    showInfo.accept("Notification Details", n.getDescription() + "\n\n" + n.getTime());
                    break;
                case "View Receipt":
                    showInfo.accept("Payment Receipt",
                            n.getDescription() + "\n\n(This is a placeholder receipt view.)");
                    break;
                case "Review Activity":
                    showInfo.accept("Security Activity",
                            n.getDescription() + "\n\nIf this wasn't you, please secure your account.");
                    break;
                default:
                    showInfo.accept(n.getTitle(), n.getDescription());
            }
        };

        // Builds one notification card (Part 4).
        Function<NotificationModel, HBox> buildCard = n -> {
            Label icon = new Label(categoryIcon.apply(n.getCategory()));
            icon.setMinSize(40, 40);
            icon.setPrefSize(40, 40);
            icon.setAlignment(Pos.CENTER);
            icon.setStyle("-fx-background-radius: 20; -fx-background-color: " + categoryIconColor.apply(n.getCategory())
                    + "; -fx-text-fill: white; -fx-font-size: 15px;");

            Label titleLabel = new Label(n.getTitle());
            titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #10223b;");

            Label categoryBadge = new Label(n.getCategory());
            categoryBadge.setStyle("-fx-background-color: " + categoryBadgeColor.apply(n.getCategory())
                    + "; -fx-text-fill: " + categoryTextColor.apply(n.getCategory())
                    + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 10;");

            HBox titleRow = new HBox(10, titleLabel, categoryBadge);
            titleRow.setAlignment(Pos.CENTER_LEFT);
            if (n.isUnread()) {
                Label dot = new Label("\u25CF");
                dot.setStyle("-fx-text-fill: #0f37a0; -fx-font-size: 10px;");
                titleRow.getChildren().add(dot);
            }

            Label description = new Label(n.getDescription());
            description.setWrapText(true);
            description.setMaxWidth(520);
            description.setStyle("-fx-font-size: 13px; -fx-text-fill: #5d6472;");

            VBox textColumn = new VBox(6, titleRow, description);

            if (n.getActionLabel() != null) {
                Button actionButton = new Button(n.getActionLabel() + "  >");
                if ("Pay Now".equals(n.getActionLabel())) {
                    actionButton.setStyle("-fx-background-color: #0f37a0; -fx-text-fill: white; -fx-font-weight: bold; "
                            + "-fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand;");
                } else {
                    actionButton.setStyle(
                            "-fx-background-color: transparent; -fx-text-fill: #0f37a0; -fx-font-weight: bold; "
                                    + "-fx-cursor: hand; -fx-padding: 4 0;");
                }
                actionButton.setOnAction(e -> handleAction.accept(n));
                textColumn.getChildren().add(actionButton);
            }

            Label time = new Label(n.getTime());
            time.setStyle("-fx-font-size: 12px; -fx-text-fill: #8a93a3;");

            Region cardSpacer = new Region();
            HBox.setHgrow(cardSpacer, Priority.ALWAYS);

            HBox card = new HBox(16, icon, textColumn, cardSpacer, time);
            card.setAlignment(Pos.TOP_LEFT);
            card.setPadding(new Insets(16, 20, 16, 20));
            card.setMaxWidth(Double.MAX_VALUE);

            if (n.isUnread()) {
                card.setStyle("-fx-background-color: #eef3ff; -fx-border-color: #0f37a0 #e5e7eb #e5e7eb #0f37a0; "
                        + "-fx-border-width: 1 1 1 4; -fx-background-radius: 10; -fx-border-radius: 10;");
            } else {
                card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; "
                        + "-fx-background-radius: 10; -fx-border-radius: 10;");
            }
            return card;
        };

        // Rebuilds the card list + stat numbers from allNotifications + currentFilter +
        // search text.
        Runnable refresh = () -> {
            notificationListBox.getChildren().clear();

            String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

            List<NotificationModel> filtered = allNotifications.stream()
                    .filter(n -> {
                        switch (currentFilter[0]) {
                            case "Unread":
                                return n.isUnread();
                            case "Udhaari":
                                return n.getCategory().equals("UDHAARI") || n.getCategory().equals("REMINDER");
                            case "Payments":
                                return n.getCategory().equals("PAYMENT");
                            case "Shops":
                                return n.getCategory().equals("SHOP") || n.getCategory().equals("OFFER");
                            case "Security":
                                return n.getCategory().equals("SECURITY");
                            case "All":
                            default:
                                return true;
                        }
                    })
                    .filter(n -> query.isEmpty()
                            || n.getTitle().toLowerCase().contains(query)
                            || n.getDescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                Label empty = new Label("No notifications found.");
                empty.setStyle("-fx-text-fill: #8a93a3; -fx-padding: 30;");
                notificationListBox.getChildren().add(empty);
            } else {
                for (NotificationModel n : filtered) {
                    notificationListBox.getChildren().add(buildCard.apply(n));
                }
            }

            unreadStatValue
                    .setText(String.valueOf(allNotifications.stream().filter(NotificationModel::isUnread).count()));
            paymentsStatValue.setText(String.valueOf(allNotifications.stream()
                    .filter(n -> n.getCategory().equals("PAYMENT")).count()));
            udhaariStatValue.setText(String.valueOf(allNotifications.stream()
                    .filter(n -> n.getCategory().equals("UDHAARI") || n.getCategory().equals("REMINDER")).count()));
            securityStatValue.setText(String.valueOf(allNotifications.stream()
                    .filter(n -> n.getCategory().equals("SECURITY")).count()));
        };

        // ===================== TOP BAR =====================
        // TextField topSearchBar = new TextField();
        // topSearchBar.setPromptText("Search settings or alerts...");
        // topSearchBar.setPrefWidth(380);
        // topSearchBar.setStyle("-fx-background-color: #f1f3f7; -fx-background-radius:
        // 20; -fx-padding: 8 16;");

        // Label bell = new Label("\uD83D\uDD14");
        // bell.setStyle("-fx-font-size: 18px;");
        // Label mail = new Label("\u2709");
        // mail.setStyle("-fx-font-size: 18px;");

        // Button payNowTop = new Button("Pay Now");
        // payNowTop.setStyle("-fx-background-color: #123aa0; -fx-text-fill: white;
        // -fx-font-weight: bold; "
        // + "-fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        // payNowTop.setOnAction(e -> payUdhariAction.run());

        // Region topSpacer = new Region();
        // HBox.setHgrow(topSpacer, Priority.ALWAYS);

        // HBox topBar = new HBox(20, topSearchBar, topSpacer, bell, mail, payNowTop);
        // topBar.setAlignment(Pos.CENTER_LEFT);
        // topBar.setPadding(new Insets(18, 35, 15, 35));
        // topBar.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb;
        // -fx-border-width: 0 0 1 0;");

        // ===================== HEADER (title + mark all as read + settings)
        // =====================
        Text pageTitle = new Text("Notifications");
        pageTitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-fill: #10223b;");
        Text pageSubtitle = new Text("Stay updated with your udhaari, payments, shops and account activity.");
        pageSubtitle.setStyle("-fx-font-size: 14px; -fx-fill: #5d6472;");
        VBox titleBlock = new VBox(5, pageTitle, pageSubtitle);

        Button markAllRead = new Button("Mark all as read");
        markAllRead.setStyle("-fx-background-color: transparent; -fx-text-fill: #0f37a0; -fx-font-weight: bold; "
                + "-fx-cursor: hand;");
        markAllRead.setOnAction(e -> {
            for (NotificationModel n : allNotifications) {
                n.setUnread(false);
            }
            refresh.run();
        });

        Button notifSettings = new Button("\u2699  Notification Settings");
        notifSettings.setStyle("-fx-background-color: white; -fx-text-fill: #10223b; -fx-border-color: #cbd5e1; "
                + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
        notifSettings.setOnAction(e -> showInfo.accept("Notification Settings",
                "Settings screen not built yet - hook this up whenever you're ready."));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerRow = new HBox(15, titleBlock, headerSpacer, markAllRead, notifSettings);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // ===================== STAT CARDS =====================
        BiFunction<String, Label, VBox> statCard = (labelText, valueLabel) -> {
            Label label = new Label(labelText);
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: #8a93a3; -fx-font-weight: bold;");
            valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #10223b;");
            VBox card = new VBox(8, label, valueLabel);
            card.setPadding(new Insets(16, 20, 16, 20));
            card.setPrefWidth(160);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; "
                    + "-fx-background-radius: 10;");
            return card;
        };
        HBox statsRow = new HBox(18,
                statCard.apply("UNREAD", unreadStatValue),
                statCard.apply("PAYMENTS", paymentsStatValue),
                statCard.apply("UDHAARI UPDATES", udhaariStatValue),
                statCard.apply("SECURITY ALERTS", securityStatValue));

        // ===================== FILTER BAR + SEARCH =====================
        BiConsumer<Button, Boolean> styleFilterButton = (btn, active) -> {
            if (active) {
                btn.setStyle("-fx-background-color: #0f37a0; -fx-text-fill: white; -fx-background-radius: 16; "
                        + "-fx-padding: 7 16; -fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                btn.setStyle("-fx-background-color: white; -fx-text-fill: #5d6472; -fx-border-color: #e5e7eb; "
                        + "-fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 7 16; -fx-cursor: hand;");
            }
        };

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        String[] filters = { "All", "Unread", "Udhaari", "Payments", "Shops", "Security" };
        for (String filterName : filters) {
            Button btn = new Button(filterName);
            styleFilterButton.accept(btn, filterName.equals(currentFilter[0]));
            btn.setOnAction(e -> {
                currentFilter[0] = filterName;
                for (Map.Entry<String, Button> entry : filterButtons.entrySet()) {
                    styleFilterButton.accept(entry.getValue(), entry.getKey().equals(currentFilter[0]));
                }
                refresh.run();
            });
            filterButtons.put(filterName, btn);
            filterRow.getChildren().add(btn);
        }

        searchField.textProperty().addListener((obs, oldVal, newVal) -> refresh.run());

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);
        filterRow.getChildren().addAll(filterSpacer, searchField);

        // ===================== NOTIFICATION LIST (scrollable) =====================
        ScrollPane listScroll = new ScrollPane(notificationListBox);
        listScroll.setFitToWidth(true);
        listScroll.setPrefHeight(560);
        listScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox notificationListArea = new VBox(listScroll);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        refresh.run(); // populate the list + stat numbers for the first time

        // ===================== QUICK ACTIONS (right column) =====================
        Function<String, Button> quickActionButton = text -> {
            Button btn = new Button(text + "   >");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle("-fx-background-color: #f7f9fc; -fx-text-fill: #10223b; -fx-font-weight: bold; "
                    + "-fx-background-radius: 8; -fx-padding: 12 14; -fx-cursor: hand;");
            return btn;
        };

        Button payOutstanding = quickActionButton.apply("\uD83D\uDCB3  Pay Outstanding Udhaari");
        payOutstanding.setOnAction(e -> payUdhariAction.run());

        Button viewHistory = quickActionButton.apply("\uD83D\uDD52  View Payment History");
        viewHistory.setOnAction(e -> showInfo.accept("Payment History",
                "Full payment history view isn't wired up yet - PayUdhari already has a preview table you can reuse."));

        Button connectedShopsBtn = quickActionButton.apply("\uD83C\uDFEC  Connected Shops");
        // ===== NAVIGATION ADDED =====
        connectedShopsBtn.setOnAction(e -> connectedShopsAction.run());

        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #10223b;");
        VBox actionsCard = new VBox(12, actionsTitle, payOutstanding, viewHistory, connectedShopsBtn);
        actionsCard.setPadding(new Insets(20));
        actionsCard.setPrefWidth(260);
        actionsCard.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; "
                + "-fx-background-radius: 12;");

        Label statusTitle = new Label("System Status");
        statusTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #10223b;");
        Label statusValue = new Label("\u25CF  All systems operational");
        statusValue.setStyle("-fx-text-fill: #047857; -fx-font-weight: bold; -fx-background-color: #ecfdf5; "
                + "-fx-padding: 10; -fx-background-radius: 8;");
        VBox statusCard = new VBox(10, statusTitle, statusValue);
        statusCard.setPadding(new Insets(20));
        statusCard.setPrefWidth(260);
        statusCard.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; "
                + "-fx-background-radius: 12;");

        VBox quickActionsColumn = new VBox(20, actionsCard, statusCard);

        // ===================== ASSEMBLE =====================
        HBox mainRow = new HBox(25, notificationListArea, quickActionsColumn);
        HBox.setHgrow(notificationListArea, Priority.ALWAYS);

        VBox centerContent = new VBox(22, headerRow, statsRow, filterRow, mainRow);
        centerContent.setPadding(new Insets(30, 35, 40, 35));

        ScrollPane pageScroll = new ScrollPane(centerContent);
        pageScroll.setFitToWidth(true);
        pageScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #f7f9fc; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        // borderPane.setTop(topBar);
        borderPane.setCenter(pageScroll);

        this.root = borderPane;
    }

    public BorderPane getView() {
        return root;
    }

    public static class NotificationModel {

        private String title;
        private String description;
        private String time;
        private String category; // "UDHAARI", "PAYMENT", "REMINDER", "OFFER", "SHOP", "SECURITY"
        private String actionLabel; // button text shown on the card, or null if the card has no button
        private boolean unread;

        public NotificationModel(String title, String description, String time,
                String category, String actionLabel, boolean unread) {
            this.title = title;
            this.description = description;
            this.time = time;
            this.category = category;
            this.actionLabel = actionLabel;
            this.unread = unread;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getTime() {
            return time;
        }

        public String getCategory() {
            return category;
        }

        public String getActionLabel() {
            return actionLabel;
        }

        public boolean isUnread() {
            return unread;
        }

        public void setUnread(boolean unread) {
            this.unread = unread;
        }
    }
}
