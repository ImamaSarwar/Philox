package com.example.philoxapp.dashboard;

import entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {
    @FXML protected Label nameLabel;
    @FXML protected Label emailLabel;

    protected User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }


}

