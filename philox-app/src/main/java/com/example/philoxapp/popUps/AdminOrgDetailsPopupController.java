package com.example.philoxapp.popUps;

import entity.Organisation;
import com.example.philoxapp.adminScreens.AdminOrganisationsController; // Ensure this import matches the controller location
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.AdministrativeService;

import java.awt.Desktop;
import java.io.File;

public class AdminOrgDetailsPopupController {

    @FXML private Label nameLabel, emailLabel, statusLabel;
    @FXML private Label missionLabel, addressLabel, websiteLabel, regDateLabel;
    @FXML private Label repNameLabel, repCnicLabel, repContactLabel, repEmailLabel;
    @FXML private HBox actionBox;
    @FXML private Button approveButton, rejectButton;

    private Organisation organisation;
    private AdminOrganisationsController parentController;

    public void setOrganisation(Organisation org, AdminOrganisationsController parent) {
        this.organisation = org;
        this.parentController = parent;

        nameLabel.setText(org.getName());
        emailLabel.setText(org.getEmail());
        missionLabel.setText(org.getMission());
        addressLabel.setText(org.getAddress());
        websiteLabel.setText(org.getWebsite());
        regDateLabel.setText(String.valueOf(org.getRegistrationDate()));

        repNameLabel.setText(org.getRepName());
        repCnicLabel.setText(org.getRepCnic());
        repContactLabel.setText(org.getRepContactNumber());
        repEmailLabel.setText(org.getRepEmail());

        if (org.getStatus() != 0) {
            actionBox.setVisible(false);
            actionBox.setManaged(false);
            statusLabel.setText(org.getStatus() == 1 ? "Approved" : "Rejected");
            statusLabel.setStyle("-fx-background-color: " + (org.getStatus() == 1 ? "#E6FFFA" : "#FEF2F2") +
                    "; -fx-text-fill: " + (org.getStatus() == 1 ? "#047857" : "#B91C1C") +
                    "; -fx-padding: 6 12; -fx-background-radius: 15; -fx-font-weight: bold;");
        } else {
            statusLabel.setText("Pending");
            statusLabel.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #B45309; -fx-padding: 6 12; -fx-background-radius: 15; -fx-font-weight: bold;");
        }
    }

    @FXML private void handleApprove() {
        AdministrativeService.approveOrRejectOrganisation(organisation.getOrganisationId(), true);
        parentController.refreshData();
        handleClose();
    }

    @FXML private void handleReject() {
        AdministrativeService.approveOrRejectOrganisation(organisation.getOrganisationId(), false);
        parentController.refreshData();
        handleClose();
    }

    @FXML private void handleClose() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }

    @FXML private void viewReg() { openFile(organisation.getRegistrationProofPath()); }
    @FXML private void viewTax() { openFile(organisation.getTaxDocumentPath()); }
    @FXML private void viewCnic() { openFile(organisation.getCnicProofPath()); }

    private void openFile(String path) {
        if(path == null || path.trim().isEmpty()) {
            System.out.println("File path is null or empty");
            return;
        }
        try {
            // Remove leading slash if present for Windows compatibility
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            // Try as relative path from project root
            File f = new File(System.getProperty("user.dir"), cleanPath);
            System.out.println("Trying to open file: " + f.getAbsolutePath());
            if (f.exists()) {
                System.out.println("File exists, opening: " + f.getAbsolutePath());
                boolean opened = false;
                // Try Desktop API first
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(f);
                        opened = true;
                        System.out.println("File opened successfully using Desktop API");
                    } catch (Exception desktopEx) {
                        System.out.println("Desktop API failed: " + desktopEx.getMessage());
                    }
                }
                // Fallback to system command for Windows
                if (!opened) {
                    try {
                        String os = System.getProperty("os.name").toLowerCase();
                        if (os.contains("win")) {
                            new ProcessBuilder("cmd", "/c", "start", "\"\"", f.getAbsolutePath()).start();
                            System.out.println("File opened using Windows cmd");
                        } else if (os.contains("mac")) {
                            new ProcessBuilder("open", f.getAbsolutePath()).start();
                            System.out.println("File opened using macOS open");
                        } else {
                            new ProcessBuilder("xdg-open", f.getAbsolutePath()).start();
                            System.out.println("File opened using xdg-open");
                        }
                    } catch (Exception cmdEx) {
                        System.out.println("System command failed: " + cmdEx.getMessage());
                    }
                }
            } else {
                System.out.println("File does not exist: " + f.getAbsolutePath());
                System.out.println("Original path was: " + path);
                System.out.println("Cleaned path was: " + cleanPath);
            }
        } catch(Exception e) {
            System.out.println("Error opening file: " + path);
            e.printStackTrace();
        }
    }
}