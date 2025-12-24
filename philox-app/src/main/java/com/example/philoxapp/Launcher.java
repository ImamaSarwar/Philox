package com.example.philoxapp;


import db.InitDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.sql.Connection;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //initialize database connection
        Connection conn = db.DBConnection.getConnection();
        if (db.DBConnection.getConnection() == null) {
            throw new IllegalStateException("Database connection could not be established!");
        }
        InitDB.createTables(conn);
        if (InitDB.isDatabaseEmpty(conn)){
            InitDB.initializeDatabase(conn);
        }

        // Finalize expired applications on startup
        service.ApplicationService.finalizeExpiredApplications();

        // Close expired opportunities on startup
        service.OpportunityService.closeExpiredOpportunities();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/landingPage.fxml"));

        if (loader.getLocation() == null) {
            throw new IllegalStateException("LandingPage.fxml not found. Check path!");
        }

        Scene scene = new Scene(loader.load());

        // Full screen setup
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.setTitle("Philox - Community for Changemakers");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}