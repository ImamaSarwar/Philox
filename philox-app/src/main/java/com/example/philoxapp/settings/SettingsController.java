package com.example.philoxapp.settings;

import com.example.philoxapp.UserProfileController;
import com.example.philoxapp.component.sidepanel.SidePanel;
import db.repository.UserRepository;
import entity.Organisation;
import entity.Volunteer;
import entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.auth.AuthService;

import java.util.Optional;

public class SettingsController extends UserProfileController {

    @FXML private VBox sidebarContainer;

    private User currentUser;

    public void setOrganisation(Organisation org) {
        this.currentUser = org;
        setUser(org);
        setupSidebar();
    }

    public void setVolunteer(Volunteer volunteer) {
        this.currentUser = volunteer;
        setUser(volunteer);
        setupSidebar();
    }

    private void setupSidebar() {
        if (currentUser != null && sidebarContainer != null) {
            SidePanel sidePanel = new SidePanel(currentUser, "Settings");
            sidebarContainer.getChildren().setAll(sidePanel.getNode());
        }
    }

    @FXML
    public void initialize() { }

    @FXML
    private void handleChangePassword() {
        // Step 1: Ask for Current Password
        Dialog<String> passDialog = new Dialog<>();
        passDialog.setTitle("Change Password");
        passDialog.setHeaderText("Verify Identity");

        ButtonType loginButtonType = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        passDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Current Password");

        VBox vbox = new VBox(new Label("Enter current password:"), pwd);
        vbox.setSpacing(10);
        passDialog.getDialogPane().setContent(vbox);

        passDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) return pwd.getText();
            return null;
        });

        Optional<String> result = passDialog.showAndWait();

        result.ifPresent(currentPassword -> {
            boolean isCorrect = AuthService.verifyPasswordBeforeChange(currentUser.getUserId(), currentPassword);

            if (isCorrect) {
                showNewPasswordDialog();
            } else {
                showError("Incorrect Password", "The password you entered is incorrect. Please try again.");
            }
        });
    }

    private void showNewPasswordDialog() {
        Dialog<String> newPassDialog = new Dialog<>();
        newPassDialog.setTitle("Set New Password");
        newPassDialog.setHeaderText("Create a new password");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        newPassDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm Password");

        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPass, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPass, 1, 1);

        newPassDialog.getDialogPane().setContent(grid);

        // Validation for empty or mismatch
        final Button saveBtn = (Button) newPassDialog.getDialogPane().lookupButton(saveButtonType);
        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (newPass.getText().isEmpty() || newPass.getText().length() < 8) {
                showError("Invalid Password", "Password must be at least 8 characters.");
                event.consume();
            } else if (!newPass.getText().equals(confirmPass.getText())) {
                showError("Mismatch", "Passwords do not match.");
                event.consume();
            }
        });

        newPassDialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) return newPass.getText();
            return null;
        });

        Optional<String> newPasswordResult = newPassDialog.showAndWait();
        newPasswordResult.ifPresent(newPassword -> {
            boolean chngpwdSuccess = AuthService.changePassword(currentUser.getUserId(), newPassword);
            if (!chngpwdSuccess) {
                showError("Error", "Failed to change password. Please try again.");
                return;
            }
            else{
                showInfo("Success", "Password changed successfully!");
                //logout
                redirectToLandingPage();
            }
            //System.out.println("Simulating Password Update...");
            //showInfo("Success", "Password changed successfully!");

        });
    }


    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action is permanent and cannot be undone. All your data will be lost.");

        ButtonType buttonTypeDelete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonTypeDelete) {
            boolean deleteSuccess = UserRepository.deleteUser(currentUser.getUserId());
            if (!deleteSuccess) {
                showError("Error", "Failed to delete account. Please try again.");
                return;
            }
            else{
                showInfo("Account Deleted", "Your account has been deleted successfully.");
                redirectToLandingPage();
            }
        }
    }

    private void redirectToLandingPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/landingPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sidebarContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Philox");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}