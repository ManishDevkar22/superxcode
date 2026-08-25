package com.eudhari.view.shopkepper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Theme {
        // Admin Style Colors & Tokens
        public static final String BG_DARK = "#c1e1ff"; // Primary window/page background
        public static final String BG_HEADER = "#1d3b8f"; // Top header background
        public static final String BG_SIDEBAR = "#122f58"; // Sidebar background (Dark Navy)
        public static final String BG_CARD = "#FFFFFF"; // Card/Panel light background
        public static final String BG_CARD_ALT = "#F8FAFC"; // Light card background
        public static final String BG_INPUT = "#F8FAFC"; // Input field background

        // Borders
        public static final String BORDER_DARK = "#E2E8F0"; // Light card border
        public static final String BORDER_INPUT = "#CBD5E1"; // Input border

        // Text Colors
        public static final String TEXT_PRIMARY = "#0F172A"; // Main dark text
        public static final String TEXT_SECONDARY = "#64748B";// Muted gray text
        public static final String TEXT_MUTED = "#94A3B8"; // Darker muted text

        // Primary Highlights & Active States (Admin Blue #3A57E8)
        public static final String SKY_BLUE = "#3A57E8"; // Primary accent
        public static final String SKY_BLUE_HOVER = "#2D44C2"; // Hover state accent
        public static final String SKY_BLUE_DARK = "#3A57E8"; // Primary button color
        public static final String SKY_BLUE_BG = "#EEF2FF"; // Accent light tint background

        // Beige / Amber Accents
        public static final String WARM_BEIGE_BG = "#FEF3C7"; // Light amber background
        public static final String WARM_BROWN_TEXT = "#D97706";// Warm amber text
        public static final String WARM_BROWN_BORDER = "#FCD34D";// Amber border

        // Standard Styles matching Admin design
        public static final String STYLE_CARD = String.format(
                        "-fx-background-color:%s; -fx-background-radius:12; -fx-border-color:%s; -fx-border-radius:12; -fx-padding:18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 0, 3);",
                        BG_CARD, BORDER_DARK);
        public static final String STYLE_INPUT = String.format(
                        "-fx-background-color:%s; -fx-text-fill:%s; -fx-prompt-text-fill:%s; -fx-border-color:%s; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:10 14;",
                        BG_INPUT, TEXT_PRIMARY, TEXT_MUTED, BORDER_INPUT);
        public static final String STYLE_BUTTON_PRIMARY = String.format(
                        "-fx-background-color:%s; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:8; -fx-padding:10 18; -fx-cursor:hand;",
                        SKY_BLUE_DARK);
        public static final String STYLE_BUTTON_SECONDARY = String.format(
                        "-fx-background-color:%s; -fx-text-fill:%s; -fx-border-color:#C7D2FE; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:8 14; -fx-cursor:hand;",
                        BG_CARD, SKY_BLUE);

        public static void applyHeaderStyle(HBox header) {
                header.setStyle(String.format("-fx-background-color:%s; -fx-border-color:%s; -fx-border-width:0 0 1 0; -fx-pref-height: 64px;",
                                BG_HEADER, BORDER_DARK));
        }

        public static void styleTextField(TextField field) {
                field.setStyle(STYLE_INPUT);
        }

        public static void styleComboBox(ComboBox<?> combo) {
                combo.setStyle(String.format(
                                "-fx-background-color:%s; -fx-text-fill:%s; -fx-border-color:%s; -fx-border-radius:6; -fx-background-radius:6;",
                                BG_INPUT, TEXT_PRIMARY, BORDER_INPUT));
        }

        public static void applyScrollDarkStyle(ScrollPane scroll) {
                scroll.setFitToWidth(true);
                scroll.setStyle(String.format(
                                "-fx-background:%s; -fx-background-color:%s; -fx-border-color:transparent;",
                                BG_DARK, BG_DARK));
        }
}
