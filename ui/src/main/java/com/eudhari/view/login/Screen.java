package com.eudhari.view.login;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Screen {

    private static final Color BG = Color.web("#060608");
    private static final Color HOLDER_A = Color.web("#ff6a3d");
    private static final Color HOLDER_B = Color.web("#ff3d77");
    private static final Color PROOF_A = Color.web("#7d5dff");
    private static final Color PROOF_B = Color.web("#3d8bff");
    private static final Color APPS_A = Color.web("#3d8bff");
    private static final Color APPS_B = Color.web("#5dd0ff");
    private static final Color TEXT_DIM = Color.web("#c9c9d6");
    private static final Color TEXT_BRIGHT = Color.web("#f5f5f8");

    private StackPane holderIcon, proofIcon, appsIcon;
    private Label holderLabel, proofLabel, appsLabel;
    private Rectangle track1Fill, track2Fill;
    private Text protoLabel, genProofLabel, verifiedLabel;
    private Rectangle progressFill;
    private Label percentLabel, statusLabel;
    private Stage activeStage;
    private Runnable completionAction;
    private boolean closeStageOnFinish = true;

    public static void showSplash(Stage primaryStage) {
        showSplash(primaryStage, null, true);
    }

    public static void showSplash(Stage primaryStage, Runnable onFinished) {
        showSplash(primaryStage, onFinished, false);
    }

    private static void showSplash(Stage primaryStage, Runnable onFinished, boolean closeStageOnFinish) {
        Screen screen = new Screen();
        screen.show(primaryStage, onFinished, closeStageOnFinish);
    }

    public void show(Stage primaryStage) {
        show(primaryStage, null, true);
    }

    public void show(Stage primaryStage, Runnable onFinished) {
        show(primaryStage, onFinished, false);
    }

    public void show(Stage primaryStage, Runnable onFinished, boolean closeStageOnFinish) {
        this.activeStage = primaryStage;
        this.completionAction = onFinished;
        this.closeStageOnFinish = closeStageOnFinish;

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(
                new RadialGradient(0, 0, 0.5, 0.42, 0.9, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#141018")),
                        new Stop(1, BG)),
                CornerRadii.EMPTY, Insets.EMPTY)));
        root.setPrefSize(1000, 640);

        VBox content = new VBox(48);
        content.setAlignment(Pos.CENTER);

        VBox titleBox = new VBox(6);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("eUdhari");
        title.setFill(TEXT_BRIGHT);
        title.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 40));
        Text subtitle = new Text("Verifiable Digital Identity & Proof Network");
        subtitle.setFill(TEXT_DIM);
        subtitle.setFont(Font.font("Segoe UI", 14));
        titleBox.getChildren().addAll(title, subtitle);
        titleBox.setOpacity(0);

        HBox flowRow = new HBox(0);
        flowRow.setAlignment(Pos.CENTER);

        holderIcon = buildHolderNode();
        proofIcon = buildProofNode();
        appsIcon = buildAppsNode();

        VBox track1 = buildConnector(HOLDER_A, PROOF_A, "Protocol", "Generate proof");
        track1Fill = (Rectangle) track1.getProperties().get("fillClip");
        protoLabel = (Text) ((HBox) track1.getChildren().get(0)).getChildren().get(0);
        genProofLabel = (Text) ((HBox) track1.getChildren().get(0)).getChildren().get(1);

        VBox track2 = buildConnector(PROOF_B, APPS_A, null, "Verified Proof");
        track2Fill = (Rectangle) track2.getProperties().get("fillClip");
        verifiedLabel = (Text) ((HBox) track2.getChildren().get(0)).getChildren().get(0);

        VBox holderCol = wrapWithLabel(holderIcon, "Holder", HOLDER_B);
        VBox proofCol = wrapWithLabel(proofIcon, "Proof", PROOF_A);
        VBox appsCol = wrapWithLabel(appsIcon, "Applications", APPS_A);

        holderLabel = (Label) holderCol.getChildren().get(1);
        proofLabel = (Label) proofCol.getChildren().get(1);
        appsLabel = (Label) appsCol.getChildren().get(1);

        flowRow.getChildren().addAll(holderCol, track1, proofCol, track2, appsCol);

        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setMaxWidth(360);

        StackPane barTrack = new StackPane();
        barTrack.setPrefSize(360, 6);
        barTrack.setMaxSize(360, 6);
        barTrack.setBackground(new Background(new BackgroundFill(
                Color.web("#1c1c22"), new CornerRadii(4), Insets.EMPTY)));

        progressFill = new Rectangle(0, 6);
        progressFill.setArcWidth(4);
        progressFill.setArcHeight(4);
        progressFill.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, HOLDER_A), new Stop(0.5, PROOF_A), new Stop(1, APPS_B)));
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        barTrack.getChildren().add(progressFill);

        statusLabel = new Label("Initializing secure channel…");
        statusLabel.setTextFill(TEXT_DIM);
        statusLabel.setFont(Font.font("Segoe UI", 12));

        percentLabel = new Label("0%");
        percentLabel.setTextFill(TEXT_BRIGHT);
        percentLabel.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 12));

        HBox statusRow = new HBox(8, statusLabel, percentLabel);
        statusRow.setAlignment(Pos.CENTER);

        progressBox.getChildren().addAll(barTrack, statusRow);
        progressBox.setOpacity(0);

        content.getChildren().addAll(titleBox, flowRow, progressBox);
        root.getChildren().add(content);

        Scene scene = new Scene(root, 1000, 640, BG);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setTitle("eUdhari — Loading");
        primaryStage.centerOnScreen();
        primaryStage.show();

        playIntro(titleBox, progressBox, root);
    }


    private void playIntro(VBox titleBox, VBox progressBox, StackPane root) {
        holderIcon.setOpacity(0);
        proofIcon.setOpacity(0);
        appsIcon.setOpacity(0);
        holderIcon.setScaleX(0.7);
        holderIcon.setScaleY(0.7);
        proofIcon.setScaleX(0.7);
        proofIcon.setScaleY(0.7);
        appsIcon.setScaleX(0.7);
        appsIcon.setScaleY(0.7);
        holderLabel.setOpacity(0);
        proofLabel.setOpacity(0);
        appsLabel.setOpacity(0);
        track1Fill.setWidth(0);
        track2Fill.setWidth(0);
        protoLabel.setOpacity(0);
        genProofLabel.setOpacity(0);
        verifiedLabel.setOpacity(0);

        Timeline tl = new Timeline();

        addFade(tl, titleBox, 0.0, 0.5, 0, 1);
        addFade(tl, progressBox, 0.15, 0.5, 0, 1);

        addFadeScale(tl, holderIcon, 0.35, 0.45, 0.7, 1.0);
        addFade(tl, holderLabel, 0.4, 0.4, 0, 1);

        addFade(tl, protoLabel, 0.9, 0.35, 0, 1);
        addWidthFill(tl, track1Fill, 130, 1.0, 0.9);
        addFade(tl, genProofLabel, 1.4, 0.35, 0, 1);

        addFadeScale(tl, proofIcon, 2.0, 0.45, 0.7, 1.0);
        addFade(tl, proofLabel, 2.05, 0.4, 0, 1);
        addPulse(tl, proofIcon, 2.45, PROOF_A);

        addFade(tl, verifiedLabel, 2.7, 0.35, 0, 1);
        addWidthFill(tl, track2Fill, 130, 2.7, 0.9);

        addFadeScale(tl, appsIcon, 3.7, 0.45, 0.7, 1.0);
        addFade(tl, appsLabel, 3.75, 0.4, 0, 1);
        addPulse(tl, appsIcon, 4.15, APPS_A);

        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(0.9),
                e -> statusLabel.setText("Generating proof request…")));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(2.0),
                e -> statusLabel.setText("Verifying credentials…")));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(2.7),
                e -> statusLabel.setText("Proof verified…")));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(3.7),
                e -> statusLabel.setText("Connecting to applications…")));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(4.5),
                e -> statusLabel.setText("Ready.")));

        tl.play();

        double totalSeconds = 4.6;
        DoublePropertyProxy progress = new DoublePropertyProxy();
        Timeline progressTl = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.prop, 0)),
                new KeyFrame(Duration.seconds(totalSeconds), e -> {
                }, new KeyValue(progress.prop, 1, Interpolator.EASE_BOTH)));
        progress.prop.addListener((obs, oldV, newV) -> {
            double pct = newV.doubleValue();
            progressFill.setWidth(360 * pct);
            percentLabel.setText((int) Math.round(pct * 100) + "%");
        });
        progressTl.play();

        PauseTransition wait = new PauseTransition(Duration.seconds(totalSeconds + 0.5));
        wait.setOnFinished(e -> {
            FadeTransition out = new FadeTransition(Duration.seconds(0.6), root);
            out.setFromValue(1);
            out.setToValue(0);
            out.setOnFinished(ev -> onSplashFinished());
            out.play();
        });
        wait.play();
    }

    private void onSplashFinished() {
        System.out.println("eUdhari splash complete.");
        if (completionAction != null) {
            completionAction.run();
        }
        if (closeStageOnFinish && activeStage != null) {
            activeStage.close();
        }
    }

    private void addFade(Timeline tl, javafx.scene.Node n, double startSec, double durSec, double from, double to) {
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec), new KeyValue(n.opacityProperty(), from)));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec + durSec),
                new KeyValue(n.opacityProperty(), to, Interpolator.EASE_OUT)));
    }

    private void addFadeScale(Timeline tl, javafx.scene.Node n, double startSec, double durSec, double fromScale,
            double toScale) {
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec),
                new KeyValue(n.opacityProperty(), 0),
                new KeyValue(n.scaleXProperty(), fromScale),
                new KeyValue(n.scaleYProperty(), fromScale)));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec + durSec),
                new KeyValue(n.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(n.scaleXProperty(), toScale, Interpolator.EASE_OUT),
                new KeyValue(n.scaleYProperty(), toScale, Interpolator.EASE_OUT)));
    }

    private void addWidthFill(Timeline tl, Rectangle clip, double fullWidth, double startSec, double durSec) {
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec), new KeyValue(clip.widthProperty(), 0)));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec + durSec),
                new KeyValue(clip.widthProperty(), fullWidth, Interpolator.EASE_BOTH)));
    }

    private void addPulse(Timeline tl, javafx.scene.Node n, double startSec, Color glowColor) {
        DropShadow ds = new DropShadow(30, glowColor);
        ds.setSpread(0.3);
        n.setEffect(ds);
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec), new KeyValue(ds.radiusProperty(), 15)));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec + 0.1),
                new KeyValue(ds.radiusProperty(), 45, Interpolator.EASE_OUT)));
        tl.getKeyFrames().add(new KeyFrame(Duration.seconds(startSec + 1.0),
                new KeyValue(ds.radiusProperty(), 22, Interpolator.EASE_IN)));
    }

    private static class DoublePropertyProxy {
        final javafx.beans.property.SimpleDoubleProperty prop = new javafx.beans.property.SimpleDoubleProperty(0);
    }

    private VBox wrapWithLabel(StackPane icon, String text, Color accent) {
        Label label = new Label(text);
        label.setTextFill(TEXT_BRIGHT);
        label.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 13));
        label.setPadding(new Insets(6, 18, 6, 18));
        label.setBackground(
                new Background(new BackgroundFill(Color.web("#0d0d10"), new CornerRadii(20), Insets.EMPTY)));
        label.setBorder(new Border(new BorderStroke(accent.deriveColor(0, 1, 1, 0.7),
                BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(1.4))));

        VBox box = new VBox(16, icon, label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0, 26, 0, 26));
        return box;
    }

    private StackPane buildHolderNode() {
        StackPane square = new StackPane();
        square.setPrefSize(110, 110);
        square.setMaxSize(110, 110);
        square.setBackground(
                new Background(new BackgroundFill(Color.web("#0b0b0e"), new CornerRadii(26), Insets.EMPTY)));
        square.setBorder(new Border(new BorderStroke(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, HOLDER_A), new Stop(1, HOLDER_B)),
                BorderStrokeStyle.SOLID, new CornerRadii(26), new BorderWidths(2))));
        DropShadow glow = new DropShadow(24, HOLDER_A);
        glow.setSpread(0.15);
        square.setEffect(glow);
        square.getChildren().add(personIcon(46, Color.web("#e8e8ee")));
        return square;
    }

    private StackPane buildProofNode() {
        StackPane wrap = new StackPane();
        wrap.setPrefSize(130, 130);
        wrap.setMaxSize(130, 130);

        Polygon octagon = octagon(62);
        octagon.setFill(Color.web("#0b0b0e"));
        octagon.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, PROOF_A), new Stop(1, PROOF_B)));
        octagon.setStrokeWidth(2);
        DropShadow glow = new DropShadow(30, PROOF_A);
        glow.setSpread(0.15);
        wrap.setEffect(glow);

        Circle dashed = new Circle(44);
        dashed.setFill(Color.TRANSPARENT);
        dashed.setStroke(Color.web("#6a6a78"));
        dashed.setStrokeWidth(1.2);
        dashed.getStrokeDashArray().addAll(4.0, 5.0);

        StackPane person = personIcon(40, Color.web("#dcdce4"));

        wrap.getChildren().addAll(octagon, dashed, person);
        return wrap;
    }

    private StackPane buildAppsNode() {
        StackPane wrap = new StackPane();
        wrap.setPrefSize(130, 130);
        wrap.setMaxSize(130, 130);
        wrap.setAlignment(Pos.CENTER);

        Polygon octagon = octagon(62);
        octagon.setFill(Color.web("#0b0b0e"));
        octagon.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, APPS_A), new Stop(1, APPS_B)));
        octagon.setStrokeWidth(2);
        DropShadow glow = new DropShadow(30, APPS_A);
        glow.setSpread(0.15);
        wrap.setEffect(glow);

        Pane network = networkIcon();
        network.setTranslateY(1);

        wrap.getChildren().addAll(octagon, network);
        return wrap;
    }

    private StackPane personIcon(double size, Color color) {
        StackPane p = new StackPane();
        Circle head = new Circle(size * 0.22);
        head.setFill(Color.TRANSPARENT);
        head.setStroke(color);
        head.setStrokeWidth(2.4);
        head.setTranslateY(-size * 0.28);

        Arc body = new Arc(0, size * 0.30, size * 0.42, size * 0.34, 0, 180);
        body.setType(ArcType.OPEN);
        body.setFill(Color.TRANSPARENT);
        body.setStroke(color);
        body.setStrokeWidth(2.4);

        p.getChildren().addAll(head, body);
        return p;
    }

    private Pane networkIcon() {
        Pane p = new Pane();
        p.setPrefSize(78, 78);
        p.setMaxSize(78, 78);

        double cx = 39, cy = 39, r = 18;
        Circle center = new Circle(cx, cy, 4.5, Color.web("#dcdce4"));
        p.getChildren().add(center);

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 90);
            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);
            Line line = new Line(cx, cy, x, y);
            line.setStroke(Color.web("#4a4a58"));
            line.setStrokeWidth(1.2);
            Circle dot = new Circle(x, y, 5, Color.web("#c4c4d0"));
            p.getChildren().addAll(line, dot);
        }

        return p;
    }

    private Polygon octagon(double r) {
        Polygon oct = new Polygon();
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(45 * i - 22.5);
            oct.getPoints().addAll(r * Math.cos(angle), r * Math.sin(angle));
        }
        return oct;
    }

    private VBox buildConnector(Color colorA, Color colorB, String topLabel, String bottomLabel) {
        double width = 130, height = 34;

        Text top = new Text(topLabel == null ? "" : " ⟡ " + topLabel);
        top.setFill(TEXT_DIM);
        top.setFont(Font.font("Segoe UI", 12));
        Text bottom = new Text(bottomLabel);
        bottom.setFill(TEXT_BRIGHT);
        bottom.setFont(Font.font("Segoe UI Semibold", FontWeight.BOLD, 12));
        HBox labels = new HBox(14, top, bottom);
        labels.setAlignment(Pos.CENTER);

        StackPane beam = new StackPane();
        beam.setPrefSize(width, height);
        beam.setMaxSize(width, height);

        Region dimTrack = new Region();
        dimTrack.setPrefSize(width, height * 0.5);
        dimTrack.setMaxSize(width, height * 0.5);
        dimTrack.setBackground(new Background(new BackgroundFill(
                colorA.deriveColor(0, 1, 1, 0.10), new CornerRadii(height * 0.25), Insets.EMPTY)));

        Region glowFill = new Region();
        glowFill.setPrefSize(width, height * 0.5);
        glowFill.setMaxSize(width, height * 0.5);
        glowFill.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, colorA), new Stop(1, colorB)),
                new CornerRadii(height * 0.25), Insets.EMPTY)));
        Glow glowEffect = new Glow(0.8);
        glowFill.setEffect(glowEffect);

        Rectangle clip = new Rectangle(0, height * 0.5);
        glowFill.setClip(clip);

        beam.getChildren().addAll(dimTrack, glowFill);
        StackPane.setAlignment(dimTrack, Pos.CENTER);
        StackPane.setAlignment(glowFill, Pos.CENTER_LEFT);

        VBox col = new VBox(10, labels, beam);
        col.setAlignment(Pos.CENTER);
        col.getProperties().put("fillClip", clip);
        return col;
    }

}
