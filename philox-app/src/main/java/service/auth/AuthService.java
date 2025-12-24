package service.auth;

import db.DataMapper;
import db.DBConnection;

import db.repository.UserRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import utils.PasswordUtil;

import entity.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public static User login(String email, String password) {
        String sql = "SELECT password, type, status FROM USERS WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    int type = rs.getInt("type"); // 1 = Organisation, 2 = Volunteer, 3 = Admin
                    int status = rs.getInt("status");

                    // Check if password is correct first
                    if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                        // Then check if user is active (status = 1)
                        if (status == 1) {
                            if (type == 2) {
                                System.out.println("Fetching volunteer by email: " + email);
                                return DataMapper.getVolunteerByEmail(email);
                            } else if (type == 1) {
                                System.out.println("Fetching org by email: " + email);
                                return DataMapper.getOrganisationByEmail(email);
                            }
                            else if (type == 3){
                                System.out.println("Fetching admin by email: " + email);
                                return DataMapper.getAdminByEmail(email);
                            }
                        } else {
                            // Account exists but not approved
                            String statusMessage;
                            if (status == 0) {
                                statusMessage = "Your account is pending approval. Please wait for admin approval before logging in.";
                            } else if (status == -1) {
                                statusMessage = "Your account has been rejected or suspended. Please contact support.";
                            } else {
                                statusMessage = "Your account status is invalid. Please contact support.";
                            }

                            showError("Account Not Approved", statusMessage);
                            return null;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean logout(Stage currentStage) {
        // Show confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to log out?");
        confirm.setContentText("Click Yes to return to the landing page.");

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yes, cancel);

        // Wait for user response
        return confirm.showAndWait()
                .filter(response -> response == yes)
                .map(response -> {
                    try {
                        // Load landing page
                        FXMLLoader loader = new FXMLLoader(AuthService.class.getResource("/com/example/philoxapp/landingPage.fxml"));
                        Parent landingRoot = loader.load();
                        Scene landingScene = new Scene(landingRoot);

                        // Navigate to landing page
                        currentStage.setScene(landingScene);
                        currentStage.setTitle("Philox - Welcome");

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Logout Failed", "Unable to load landing page: " + e.getMessage());
                        return false;
                    }
                })
                .orElse(false);
    }
    private static void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean verifyPasswordBeforeChange(int userId, String oldPassword) {
        String passwordInDb = UserRepository.getPasswordByUserId(userId);
        //return PasswordUtil.verifyPassword(oldPassword, passwordInDb); //uncomment for release
        return oldPassword.equals(passwordInDb);
    }

    public static boolean changePassword(int userId, String newPassword) {
        //hash the new password before saving
        //String hashedPwd = BCrypt.hashpw(password, BCrypt.gensalt()); //uncomment for release
        String hashedPassword = newPassword;
        return UserRepository.changePassword(userId, hashedPassword);
    }

    public static boolean deleteAccount(int userId) {
        return UserRepository.deleteUser(userId);
    }

}
