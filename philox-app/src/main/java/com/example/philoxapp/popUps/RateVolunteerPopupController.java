package com.example.philoxapp.popUps;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RateVolunteerPopupController {

    @FXML private HBox starsContainer;
    @FXML private SVGPath star1, star2, star3, star4, star5;
    @FXML private Button submitBtn;

    private final List<SVGPath> stars = new ArrayList<>();
    private int currentRating = 0;
    private String volunteerName;

    @FXML
    public void initialize() {
        stars.add(star1);
        stars.add(star2);
        stars.add(star3);
        stars.add(star4);
        stars.add(star5);

        // Add click listeners
        for (int i = 0; i < stars.size(); i++) {
            int ratingValue = i + 1;
            stars.get(i).setOnMouseClicked(event -> updateStarDisplay(ratingValue));
        }
    }

    public void setVolunteerName(String name) {
        this.volunteerName = name;
    }

    private void updateStarDisplay(int rating) {
        this.currentRating = rating;
        submitBtn.setDisable(false); // Enable submit button

        for (int i = 0; i < stars.size(); i++) {
            SVGPath star = stars.get(i);
            if (i < rating) {
                // Filled Gold
                star.setFill(Color.web("#F59E0B"));
                star.setStroke(Color.web("#F59E0B"));
            } else {
                // Empty Grey
                star.setFill(Color.web("#E5E7EB"));
                star.setStroke(Color.web("#D1D5DB"));
            }
        }
    }

    @FXML
    private void handleSubmit() {
        if (currentRating > 0) {
            // Mock Database logic
            System.out.println("Rating submitted for " + volunteerName + ": " + currentRating + "/5");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rating Submitted");
            alert.setHeaderText(null);
            alert.setContentText("You successfully rated " + volunteerName + " " + currentRating + " stars!");
            alert.showAndWait();

            closePopup();
        }
    }

    @FXML
    private void handleCancel() {
        closePopup();
    }

    private void closePopup() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }
}