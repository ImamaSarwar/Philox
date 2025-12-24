package com.example.philoxapp.dashboard;

import com.example.philoxapp.component.sidepanel.SidePanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import entity.Volunteer;
import java.io.InputStream;
import java.util.Map;

public class VolunteerDashboardController extends UserDashboardController {

    @FXML private VBox sidebarContainer;

    @FXML private Button profileButton;

    @FXML private ImageView totalApplicationsImage;
    @FXML private ImageView completedImage;
    @FXML private ImageView pendingImage;

    @FXML private Label totalCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label welcomeLabel; //to be used for backend
    //private String username = "Imama (dummy)"; //to be used for backend

    // carousel nodes
    @FXML private StackPane carouselClip;
    @FXML private HBox carouselTrack;
    @FXML private HBox indicatorsBox;

    private int slideCount = 3;
    private int currentIndex = 0;
    private Timeline autoTimeline;
    private TranslateTransition slideTransition;

    public void setVolunteer(Volunteer volunteer) {
        setUser(volunteer);

        //set up welcome msg
        applyUsername(volunteer.getName());

        // load summary
        loadSummaryData();

        // Setup sidebar with new SidePanel system
        SidePanel sidePanel = new SidePanel(volunteer, "Dashboard");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());
    }

    @FXML
    private void initialize() {
        setCounts(10, 7, 3);

        Platform.runLater(() -> {
            setupClip();
            setupIndicators();
            showSlide(0, false); // show first slide by default
            startAutoSliding();
        });

        loadImageInto("/assets/totalApplications.png", totalApplicationsImage);
        loadImageInto("/assets/badge.png", completedImage);
        loadImageInto("/assets/pendingApplications.png", pendingImage);
    }

    public void loadSummaryData() {
        int total=0,completed=0,pending=0;
        Map<String,Integer> stats = service.ProfileService.getVolunteerStats(((Volunteer)getUser()).getVolunteerId());
        if(stats!=null){
            total = stats.getOrDefault("total",0);
            completed = stats.getOrDefault("completed",0);
            pending = stats.getOrDefault("pending",0);
        }
        setCounts(total, completed, pending);
    }

    private void applyUsername(String uname) {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + uname + "!");
        }
    }

    private void setupClip() {
        // clip the carousel area so slides don't overflow visually
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(carouselClip.widthProperty());
        clip.heightProperty().bind(carouselClip.heightProperty());
        carouselClip.setClip(clip);

        // avoid the very wide carouselTrack from affecting parent layout
        carouselTrack.setManaged(false);

        // bind each slide's preferred size to the visible clip size so layout stays stable
        carouselTrack.getChildren().forEach(node -> {
            if (node instanceof Region) {
                Region r = (Region) node;
                r.prefWidthProperty().bind(carouselClip.widthProperty());
                r.prefHeightProperty().bind(carouselClip.heightProperty());
                r.setMinWidth(Region.USE_PREF_SIZE);
                r.setMaxWidth(Region.USE_PREF_SIZE);
                r.setMinHeight(Region.USE_PREF_SIZE);
                r.setMaxHeight(Region.USE_PREF_SIZE);
            }
        });
    }

    private void setupIndicators() {
        // ensure the indicators box is placed at the bottom center of the carousel clip
        StackPane.setAlignment(indicatorsBox, Pos.BOTTOM_CENTER);
        indicatorsBox.setPadding(new Insets(0, 0, 12, 0));
        indicatorsBox.getChildren().clear();

        for (int i = 0; i < slideCount; i++) {
            Region dot = new Region();
            dot.setStyle(inactiveDotStyle());
            dot.setPrefWidth(8);
            dot.setPrefHeight(8);
            // lock dot to pref size so it cannot stretch vertically
            dot.setMinWidth(Region.USE_PREF_SIZE);
            dot.setMinHeight(Region.USE_PREF_SIZE);
            dot.setMaxWidth(Region.USE_PREF_SIZE);
            dot.setMaxHeight(Region.USE_PREF_SIZE);
            indicatorsBox.getChildren().add(dot);
        }

        updateIndicators(0);
    }

    private void updateIndicators(int activeIndex) {
        for (int i = 0; i < indicatorsBox.getChildren().size(); i++) {
            Region r = (Region) indicatorsBox.getChildren().get(i);
            if (i == activeIndex) {
                r.setPrefWidth(36);
                r.setPrefHeight(8);
                r.setStyle(activeDotStyle());
            } else {
                r.setPrefWidth(8);
                r.setPrefHeight(8);
                r.setStyle(inactiveDotStyle());
            }
            // keep min/max locked to pref so indicators never stretch
            r.setMinWidth(Region.USE_PREF_SIZE);
            r.setMinHeight(Region.USE_PREF_SIZE);
            r.setMaxWidth(Region.USE_PREF_SIZE);
            r.setMaxHeight(Region.USE_PREF_SIZE);
        }
    }


    private String inactiveDotStyle() {
        return "-fx-background-color: rgba(120,130,150,0.35); -fx-background-radius: 8;";
    }

    private String activeDotStyle() {
        return "-fx-background-color: rgba(120,130,150,0.7); -fx-background-radius: 8;";
    }

    private void showSlide(int index, boolean animate) {
        if (carouselClip == null || carouselTrack == null) return;
        double width = carouselClip.getWidth();
        double targetX = -index * width;

        if (animate) {
            if (slideTransition != null) slideTransition.stop();
            slideTransition = new TranslateTransition(Duration.millis(600), carouselTrack);
            slideTransition.setFromX(carouselTrack.getTranslateX());
            slideTransition.setToX(targetX);
            slideTransition.play();
        } else {
            carouselTrack.setTranslateX(targetX);
        }

        updateIndicators(index);
        currentIndex = index;
    }

    private void startAutoSliding() {
        if (autoTimeline != null) autoTimeline.stop();
        autoTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            int next = (currentIndex + 1) % slideCount;
            showSlide(next, true);
        }));
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.play();
    }

    private void setCounts(int total, int completed, int pending) {
        if (totalCountLabel != null) totalCountLabel.setText(String.valueOf(total));
        if (completedCountLabel != null) completedCountLabel.setText(String.valueOf(completed));
        if (pendingCountLabel != null) pendingCountLabel.setText(String.valueOf(pending));
    }

    /*@FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerProfile.fxml"));
            Parent root = loader.load();
            VolunteerProfileController controller = loader.getController();
            controller.setVolunteer((Volunteer) getUser());
            // Get current stage from event
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Volunteer Profile");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplyOpportunity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/TestApplication.fxml"));
            Parent root = loader.load();
            TestApplicationController controller = loader.getController();
            controller.setVolunteer((Volunteer) getUser());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Apply for Opportunity");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    private void loadImageInto(String resourcePath, ImageView iv) {
        if (iv == null) return;
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                Image img = new Image(is);
                iv.setImage(img);
            }
        } catch (Exception ignored) { }
    }
}
