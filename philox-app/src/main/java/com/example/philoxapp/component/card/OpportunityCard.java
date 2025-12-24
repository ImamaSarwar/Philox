package com.example.philoxapp.component.card;

import entity.Opportunity;
import model.OpportunityStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OpportunityCard {
    @FXML private Label orgLabel;
    @FXML private Label statusLabel;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private ImageView cardlocationIcon;


    public void setOpportunity(Opportunity opp) {
        orgLabel.setText(opp.getOrganisation().getName());
        //map status int to enum
        var status = model.OpportunityStatus.fromCode(opp.getStatus());
        statusLabel.setText(status.toString());
        titleLabel.setText(opp.getTitle());
        descriptionLabel.setText(opp.getDescription());
        locationLabel.setText(opp.getLocation());
        // map start -> end date as range
        String dateRange = opp.getStartDate() + " - " + opp.getEndDate();
        dateLabel.setText(dateRange);

        cardlocationIcon.setImage(new Image(getClass().getResource("/assets/locationIcon.png").toExternalForm()));

    }
}
