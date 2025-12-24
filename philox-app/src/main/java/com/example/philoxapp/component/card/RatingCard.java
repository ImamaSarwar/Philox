package com.example.philoxapp.component.card;

import entity.Rating;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

/**
 * Reusable rating card component for displaying individual ratings
 */
public class RatingCard extends VBox {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

    public RatingCard(Rating rating) {
        setupCard(rating);
    }

    private void setupCard(Rating rating) {
        this.setPrefWidth(260);
        this.setSpacing(10);
        this.setStyle("-fx-background-color:white; -fx-background-radius:12; " +
                "-fx-padding:16; -fx-border-color:#E2E8F0; -fx-border-radius:12;");

        // Stars
        Label stars = new Label("★".repeat(rating.getRatingStars()));
        stars.setStyle("-fx-text-fill:#FFD54A; -fx-font-size:16px; -fx-font-weight:bold;");

        // Comment
        Label comment = new Label(rating.getComment() != null ? rating.getComment() : "No comment");
        comment.setWrapText(true);
        comment.setStyle("-fx-text-fill:#364153; -fx-font-size:13px;");

        // Footer with rater name and date
        String raterName = (rating.getRater() != null && rating.getRater().getName() != null) ?
            rating.getRater().getName() : "Anonymous";

        String dateText = (rating.getCreatedAt() != null) ?
            rating.getCreatedAt().format(DATE_FORMATTER) : "Unknown date";

        Label footer = new Label(raterName + " • " + dateText);
        footer.setStyle("-fx-text-fill:#6A7282; -fx-font-size:12px;");

        this.getChildren().addAll(stars, comment, footer);
    }
}
