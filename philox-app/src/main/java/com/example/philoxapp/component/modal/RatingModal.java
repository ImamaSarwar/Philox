package com.example.philoxapp.component.modal;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import service.RatingService;

/**
 * Reusable rating modal that supports both volunteer->organization and organization->volunteer ratings
 */
public class RatingModal {

    public enum RatingType {
        VOLUNTEER_TO_ORGANIZATION,
        ORGANIZATION_TO_VOLUNTEER
    }

    private final RatingType ratingType;
    private final int fromUserId;
    private final int toUserId;
    private final String targetName;
    private final Window owner;

    public RatingModal(RatingType ratingType, int fromUserId, int toUserId, String targetName, Window owner) {
        this.ratingType = ratingType;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.targetName = targetName;
        this.owner = owner;
    }

    /**
     * Shows the rating modal and returns true if rating was successfully submitted
     */
    public boolean showAndWait() {
        // Check if user has already rated
        boolean hasAlreadyRated = RatingService.hasUserRatedUser(fromUserId, toUserId);
        if (hasAlreadyRated) {
            showAlreadyRatedDialog();
            return false;
        }

        // Create the rating dialog
        Stage dlg = new Stage(StageStyle.TRANSPARENT);
        dlg.initModality(Modality.WINDOW_MODAL);
        if (owner != null) dlg.initOwner(owner);
        dlg.setResizable(false);

        VBox dialog = new VBox(14);
        dialog.setPadding(new javafx.geometry.Insets(14));
        dialog.setMaxWidth(720);
        dialog.setPrefHeight(260);
        dialog.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20,0,0,8);");

        // Header
        HBox header = createHeader(dlg);

        // Stars row
        HBox starsRow = new HBox(8);
        starsRow.setAlignment(Pos.CENTER_LEFT);
        final int[] currentRating = {0};
        Label[] stars = new Label[5];

        java.util.function.Consumer<Integer> render = r -> {
            currentRating[0] = r;
            for (int i = 0; i < stars.length; i++) {
                if (i < r) {
                    stars[i].setText("\u2605");
                    stars[i].setStyle("-fx-font-size: 26px; -fx-text-fill: #F59E0B; -fx-cursor: hand;");
                } else {
                    stars[i].setText("\u2606");
                    stars[i].setStyle("-fx-font-size: 26px; -fx-text-fill: #F59E0B; -fx-opacity: 0.28; -fx-cursor: hand;");
                }
            }
        };

        // Create star labels (click handlers will be set up in createButtonRow)
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            Label s = new Label("\u2606");
            s.setStyle("-fx-font-size: 26px; -fx-text-fill: #F59E0B; -fx-opacity: 0.28; -fx-cursor: hand;");
            s.setOnMouseEntered(e -> {
                for (int j = 0; j <= idx; j++) {
                    if (stars[j] != null) {
                        stars[j].setText("\u2605");
                        stars[j].setStyle("-fx-font-size: 26px; -fx-text-fill: #F59E0B; -fx-cursor: hand;");
                    }
                }
                for (int j = idx + 1; j < stars.length; j++) {
                    if (stars[j] != null) {
                        stars[j].setText("\u2606");
                        stars[j].setStyle("-fx-font-size: 26px; -fx-text-fill: #F59E0B; -fx-opacity: 0.28; -fx-cursor: hand;");
                    }
                }
            });
            s.setOnMouseExited(e -> render.accept(currentRating[0]));
            // Click handler will be set in createButtonRow
            stars[i] = s;
            starsRow.getChildren().add(s);
        }

        // Note area
        String notePrompt = ratingType == RatingType.VOLUNTEER_TO_ORGANIZATION ?
            "Tell the organization why you gave this rating..." :
            "Tell the volunteer why you gave this rating...";

        Label noteLabel = new Label("Add a short note (Optional)");
        noteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #0A0A0A; -fx-font-weight: 500;");

        TextArea noteArea = new TextArea();
        noteArea.setPromptText(notePrompt);
        noteArea.setPrefHeight(64);
        noteArea.setWrapText(true);
        noteArea.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        Label charCount = new Label("0/100");
        charCount.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Enforce 100 non-whitespace char limit
        final int LIMIT = 100;
        noteArea.textProperty().addListener((obs, oldT, newT) -> {
            String src = newT == null ? "" : newT;
            StringBuilder sb = new StringBuilder(src.length());
            int nonWs = 0;
            for (int i = 0; i < src.length(); i++) {
                char c = src.charAt(i);
                if (Character.isWhitespace(c)) {
                    sb.append(c);
                } else {
                    if (nonWs < LIMIT) {
                        sb.append(c);
                        nonWs++;
                    }
                }
            }
            String result = sb.toString();
            final int finalNonWs = nonWs;
            if (!result.equals(src)) {
                int caret = noteArea.getCaretPosition();
                Platform.runLater(() -> {
                    noteArea.setText(result);
                    noteArea.positionCaret(Math.min(caret, result.length()));
                    charCount.setText(Math.min(finalNonWs, LIMIT) + "/" + LIMIT);
                });
            } else {
                charCount.setText(Math.min(finalNonWs, LIMIT) + "/" + LIMIT);
            }
        });

        // Buttons
        HBox btnRow = createButtonRow(dlg, currentRating, noteArea, render, stars);

        // Character count row
        HBox countRow = new HBox();
        Region countSpacer = new Region();
        HBox.setHgrow(countSpacer, Priority.ALWAYS);
        countRow.getChildren().addAll(countSpacer, charCount);

        // Assemble dialog
        dialog.getChildren().addAll(header, starsRow, noteLabel, noteArea, countRow, btnRow);

        // Set up stage and show
        setupAndShowStage(dlg, dialog);

        return true; // Modal was shown (doesn't guarantee rating was submitted)
    }

    private HBox createHeader(Stage dlg) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        String titleText = ratingType == RatingType.VOLUNTEER_TO_ORGANIZATION ?
            "Rate " + targetName :
            "Rate " + targetName;

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #274C56;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: 600;");
        closeBtn.setOnAction(ev -> dlg.close());

        header.getChildren().addAll(title, headerSpacer, closeBtn);
        return header;
    }

    private HBox createButtonRow(Stage dlg, int[] currentRating, TextArea noteArea, java.util.function.Consumer<Integer> render, Label[] stars) {
        HBox btnRow = new HBox();
        btnRow.setSpacing(10);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e6e9eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 14;");
        cancelBtn.setOnAction(ev -> dlg.close());

        Button confirmBtn = new Button("Submit Rating");
        confirmBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6 18;");
        confirmBtn.setDisable(true);

        // Enable confirm when rating chosen
        java.util.function.Consumer<Integer> enableWrapper = r -> {
            render.accept(r);
            confirmBtn.setDisable(r == 0);
        };

        // Connect star clicks to enable wrapper
        for (int i = 0; i < stars.length; i++) {
            final int idx = i;
            stars[i].setOnMouseClicked(ev -> enableWrapper.accept(idx + 1));
        }

        confirmBtn.setOnAction(ev -> {
            int rating = currentRating[0];
            String note = noteArea.getText() == null ? "" : noteArea.getText().trim();

            boolean success = submitRating(rating, note);
            showResultDialog(success, rating, note);
            dlg.close();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        btnRow.getChildren().addAll(cancelBtn, spacer, confirmBtn);

        return btnRow;
    }

    private boolean submitRating(int rating, String note) {
        if (ratingType == RatingType.VOLUNTEER_TO_ORGANIZATION) {
            System.out.println("Submitting organization rating from " + fromUserId + " to " + toUserId + " with rating " + rating + " and note: " + note);
            return RatingService.addOrganisationRating(fromUserId, toUserId, rating, note);
        } else {
            System.out.println("Submitting volunteer rating from " + fromUserId + " to " + toUserId + " with rating " + rating + " and note: " + note);
            return RatingService.addVolunteerRating(fromUserId, toUserId, rating, note);
        }
    }

    private void showResultDialog(boolean success, int rating, String note) {
        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(success ? "Rating Submitted" : "Rating Failed");
        alert.setHeaderText(null);

        if (success) {
            alert.setContentText("Rating: " + rating + " stars\nNote: " + (note.isEmpty() ? "None" : note));
        } else {
            alert.setContentText("Failed to submit rating. You may have already rated this " +
                (ratingType == RatingType.VOLUNTEER_TO_ORGANIZATION ? "organization" : "volunteer") + ".");
        }

        if (owner != null) alert.initOwner(owner);
        alert.showAndWait();
    }

    private void showAlreadyRatedDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Already Rated");
        alert.setHeaderText(null);
        alert.setContentText("You have already rated this " +
            (ratingType == RatingType.VOLUNTEER_TO_ORGANIZATION ? "organization" : "volunteer") + ".");

        if (owner != null) alert.initOwner(owner);
        alert.showAndWait();
    }

    private void setupAndShowStage(Stage dlg, VBox dialog) {
        // Compute preferred size
        dialog.applyCss();
        dialog.layout();

        double prefW = Math.max(600, Math.min(720, dialog.prefWidth(-1)));
        double prefH = Math.max(160, Math.min(420, dialog.prefHeight(-1)));
        dialog.setPrefWidth(prefW);
        dialog.setPrefHeight(prefH);
        dialog.setMaxWidth(prefW);
        dialog.setMaxHeight(prefH);

        // Determine owner size for full-window dimmer
        double ow, oh;
        if (owner != null) {
            ow = owner.getWidth();
            oh = owner.getHeight();
            if (ow <= 0) ow = prefW;
            if (oh <= 0) oh = prefH;
        } else {
            javafx.geometry.Rectangle2D vb = javafx.stage.Screen.getPrimary().getVisualBounds();
            ow = vb.getWidth();
            oh = vb.getHeight();
        }

        // Create full-window wrapper with dimmer
        StackPane wrapper = new StackPane();
        wrapper.setPrefSize(ow, oh);
        wrapper.setMinSize(ow, oh);
        wrapper.setMaxSize(ow, oh);
        wrapper.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        wrapper.getChildren().add(dialog);
        StackPane.setAlignment(dialog, Pos.CENTER);

        // Create scene and show
        Scene scene = new Scene(wrapper, ow, oh, Color.TRANSPARENT);

        if (owner != null) {
            dlg.initOwner(owner);
            dlg.setX(owner.getX());
            dlg.setY(owner.getY());
        } else {
            dlg.centerOnScreen();
        }

        dlg.setScene(scene);
        dlg.showAndWait();
    }
}

