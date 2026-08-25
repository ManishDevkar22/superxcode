package com.eudhari.view.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockWidget {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a");

    public static HBox createClockBox(String textColorHex, String bgStyle) {
        Label dateLabel = new Label();
        Label timeLabel = new Label();

        if (textColorHex == null || textColorHex.isEmpty()) {
            textColorHex = "#38bdf8";
        }

        dateLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
        timeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + textColorHex + ";");

        Runnable updateClock = () -> {
            dateLabel.setText("📅 " + LocalDate.now().format(DATE_FORMATTER));
            timeLabel.setText("⏰ " + LocalTime.now().format(TIME_FORMATTER));
        };

        updateClock.run();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        HBox container = new HBox(8, dateLabel, timeLabel);
        container.setAlignment(Pos.CENTER);
        if (bgStyle != null && !bgStyle.isEmpty()) {
            container.setStyle(bgStyle);
        } else {
            container.setStyle("-fx-background-color: #131e33; -fx-padding: 6 12; -fx-background-radius: 8; -fx-border-color: #1e293b; -fx-border-radius: 8;");
        }
        return container;
    }
}
