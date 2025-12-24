package com.example.philoxapp.component.modal;

import entity.Application;
import entity.Opportunity;
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
import service.ApplicationService;
import session.Session;

public class ApplyModalController {

    private final Opportunity opportunity;

    ApplyModalController(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public boolean showAndWait() {
        // Create modal content
        VBox root = createApplyModalContent();

        // Create and show modal with showAndWait()
        Stage dialog = createModalStage(root);
        dialog.showAndWait();

        // Return the result stored in the dialog's properties
        Boolean result = (Boolean) dialog.getProperties().get("applicationResult");
        return result != null ? result : false;
    }

    private VBox createApplyModalContent() {
        VBox root = new VBox();
        root.setSpacing(12);
        root.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12,0,0,4);");
        root.setMaxWidth(560);
        root.setMaxHeight(350);

        Label header = new Label("Confirm Application");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #111827;");

        String opportunityTitle = opportunity.getTitle() != null ? opportunity.getTitle() : "this opportunity";
        Label question = new Label("Do you want to apply for \"" + opportunityTitle + "\"?");
        question.setStyle("-fx-text-fill: #475569;");

        Label noteLabel = new Label("Add a short note to the organisation *");
        noteLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0A0A0A;");

        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Tell them why you're interested...");
        noteArea.setPrefHeight(120);
        noteArea.setWrapText(true);
        noteArea.setStyle("-fx-background-color: white; -fx-border-color: #e6e9eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        Label charCount = new Label("0/250");
        charCount.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        Label noteError = new Label("Short note is required to apply.");
        noteError.setStyle("-fx-text-fill: #E24560; -fx-font-size: 12px;");
        noteError.setVisible(false);
        noteError.setManaged(false);

        // Buttons row
        HBox btnRow = createButtonRow(noteArea, noteError);

        // Right-aligned char count
        HBox countRow = new HBox();
        Region countSpacer = new Region();
        HBox.setHgrow(countSpacer, Priority.ALWAYS);
        countRow.getChildren().addAll(countSpacer, charCount);

        // Setup text area listeners
        setupTextAreaValidation(noteArea, charCount, btnRow, noteError);

        // Assemble all components
        root.getChildren().addAll(header, question, noteLabel, noteArea, noteError, countRow, btnRow);

        return root;
    }

    private void setupTextAreaValidation(TextArea noteArea, Label charCount, HBox btnRow, Label noteError) {
        final int LIMIT = 250;
        Button confirmBtn = (Button) btnRow.getProperties().get("confirmBtn");

        // Enforce 250 non-whitespace char limit, update count and enable/disable Confirm
        noteArea.textProperty().addListener((obs, oldText, newText) -> {
            String src = newText == null ? "" : newText;
            StringBuilder sb = new StringBuilder(src.length());
            int nonWsCount = 0;

            // Iterate and append characters; whitespace always appended, non-whitespace only up to LIMIT
            for (int i = 0; i < src.length(); i++) {
                char c = src.charAt(i);
                if (Character.isWhitespace(c)) {
                    sb.append(c);
                } else {
                    if (nonWsCount < LIMIT) {
                        sb.append(c);
                        nonWsCount++;
                    }
                    // Skip further non-whitespace characters
                }
            }

            final String result = sb.toString();
            final int finalCount = nonWsCount;

            if (!result.equals(src)) {
                Platform.runLater(() -> {
                    int caret = noteArea.getCaretPosition();
                    noteArea.setText(result);
                    // Clamp caret to text length
                    noteArea.positionCaret(Math.min(caret, result.length()));
                    updateCharCountAndButton(charCount, confirmBtn, noteError, finalCount, LIMIT);
                });
            } else {
                Platform.runLater(() -> updateCharCountAndButton(charCount, confirmBtn, noteError, finalCount, LIMIT));
            }
        });

        // Show mandatory error when user leaves the field empty
        noteArea.focusedProperty().addListener((obs, oldF, focused) -> {
            if (!focused) {
                String t = noteArea.getText() == null ? "" : noteArea.getText();
                String stripped = t.replaceAll("\\s+", "");
                boolean show = stripped.isEmpty();
                noteError.setVisible(show);
                noteError.setManaged(show);
            } else {
                noteError.setVisible(false);
                noteError.setManaged(false);
            }
        });
    }

    private void updateCharCountAndButton(Label charCount, Button confirmBtn, Label noteError, int count, int limit) {
        charCount.setText(count + "/" + limit);
        confirmBtn.setDisable(count == 0);
        if (count > 0) {
            noteError.setVisible(false);
            noteError.setManaged(false);
        }
    }

    private HBox createButtonRow(TextArea noteArea, Label noteError) {
        HBox btnRow = new HBox();
        btnRow.setSpacing(12);
        Region btnSpacer = new Region();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e6e9eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 18;");
        cancelBtn.setOnAction(ev -> {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.getProperties().put("applicationResult", false);
            stage.close();
        });

        Button confirmBtn = new Button("Confirm");
        confirmBtn.setStyle("-fx-background-color: #04937a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 22;");
        confirmBtn.setDisable(true); // disabled until non-empty note
        confirmBtn.setOnAction(ev -> handleConfirmApplication(noteArea, noteError));

        btnRow.getChildren().addAll(cancelBtn, btnSpacer, confirmBtn);

        // Store confirmBtn reference in btnRow properties for access in validation
        btnRow.getProperties().put("confirmBtn", confirmBtn);

        return btnRow;
    }

    private void handleConfirmApplication(TextArea noteArea, Label noteError) {
        String note = noteArea.getText() == null ? "" : noteArea.getText().trim();
        // Final guard: check non-whitespace characters
        String stripped = note.replaceAll("\\s+", "");
        if (stripped.isEmpty()) {
            noteError.setVisible(true);
            noteError.setManaged(true);
            return;
        }

        // submit application in db
        Application app = new Application();
        app.setOpportunityId(opportunity.getOpportunityId());
        app.setVolunteerId(Session.getSession().getCurrentUser().getUserId()); //volunteer
        app.setApplicationComment(note);
        app.setStatus(0);
        boolean success = ApplicationService.applyForOpportunity(app);

        Stage stage = (Stage) noteArea.getScene().getWindow();
        Window ownerWindow = stage.getOwner();

        String opportunityTitle = opportunity.getTitle() != null ? opportunity.getTitle() : "Unknown Opportunity";
        String orgName = opportunity.getOrganisation() != null ? opportunity.getOrganisation().getName() : "Unknown Organization";

        if (success){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Application Submitted");
            a.setHeaderText(null);
            a.setContentText("Your application for \"" + opportunityTitle + "\" at " + orgName + " was submitted successfully.\n\nYour note: " + note);
            if (ownerWindow != null) a.initOwner(ownerWindow);
            a.showAndWait();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Application Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("There was an error submitting your application. Please try again later.");
            if (ownerWindow != null) errorAlert.initOwner(ownerWindow);
            errorAlert.showAndWait();
        }

        // Set the result and close the modal
        stage.getProperties().put("applicationResult", success);
        stage.close();
    }

    private Stage createModalStage(VBox root) {
        // Create modal stage without overlay background
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Initialize result to false by default
        dialog.getProperties().put("applicationResult", false);

        // Create scene directly with the root content (no overlay wrapper)
        Scene scene = new Scene(root);

        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.centerOnScreen();

        return dialog;
    }
}
