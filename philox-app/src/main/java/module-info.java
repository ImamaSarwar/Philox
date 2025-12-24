module com.example.philoxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jbcrypt;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.philoxapp to javafx.fxml;
    exports com.example.philoxapp;

    // Add this line if you have a "dashboard" package
    exports com.example.philoxapp.dashboard;
    opens com.example.philoxapp.dashboard to javafx.fxml;
    exports com.example.philoxapp.organisationSignup;
    opens com.example.philoxapp.organisationSignup to javafx.fxml;
    exports com.example.philoxapp.component.card to javafx.fxml;
    opens com.example.philoxapp.component.card to javafx.fxml;

    exports entity;
    exports model;
    exports com.example.philoxapp.component.modal;
    opens com.example.philoxapp.component.modal to javafx.fxml;
    exports com.example.philoxapp.organisationScreens;
    opens com.example.philoxapp.organisationScreens to javafx.fxml;
    exports com.example.philoxapp.adminScreens;
    opens com.example.philoxapp.adminScreens to javafx.fxml;
    exports com.example.philoxapp.volunteerScreens;
    opens com.example.philoxapp.volunteerScreens to javafx.fxml;
    exports com.example.philoxapp.popUps;
    opens com.example.philoxapp.popUps to javafx.fxml;
    exports com.example.philoxapp.settings;
    opens com.example.philoxapp.settings to javafx.fxml;
    exports com.example.philoxapp.initialScreens;
    opens com.example.philoxapp.initialScreens to javafx.fxml;
}