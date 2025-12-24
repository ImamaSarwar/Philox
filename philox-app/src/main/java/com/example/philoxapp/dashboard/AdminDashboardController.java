package com.example.philoxapp.dashboard;
import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import service.AdministrativeService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class AdminDashboardController extends UserDashboardController {

    @FXML private VBox sidebarContainer;

    @FXML private ImageView adminD1Icon;
    @FXML private ImageView adminD2Icon;
    @FXML private ImageView adminD3Icon;
    @FXML private ImageView adminShieldIcon;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label totalBadgesLabel;
    @FXML private Label activeUsersLabel;
    @FXML private BorderPane rootPane;                // fx:id on BorderPane
    //@FXML private VBox sidePanelPlaceholder;          // fx:id on left placeholder
    @FXML private VBox contentVBox;

    public void setAdmin(Admin admin){
        setUser(admin);

        SidePanel sidePanel = new SidePanel(admin, "Dashboard");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());
    }

    @FXML
    public void initialize() {
        // available width = root width - sidebar pref width - horizontal margins (adjust margin value as needed)
        DoubleBinding available = rootPane.widthProperty()
                .subtract(sidebarContainer.prefWidthProperty())
                .subtract(80);

        // content max width = min(available, 1100)
        contentVBox.maxWidthProperty().bind(
                Bindings.when(available.greaterThan(1100.0)).then(1100.0).otherwise(available)
        );
        // make children layout predictably by setting prefWidth = maxWidth
        contentVBox.prefWidthProperty().bind(contentVBox.maxWidthProperty());

        // demo defaults - backend should call the setters below
        setPendingApprovals(AdministrativeService.getPendingOrganisationApprovalsCount());
        setTotalBadges(AdministrativeService.getTotalBadgesCount());
        setActiveUsers(AdministrativeService.getTotalActiveUsersCount());

        // load icons from resources using your helper
        loadIcon(adminD1Icon, "/assets/adminD1card.png");
        loadIcon(adminD2Icon, "/assets/adminD2card.png");
        loadIcon(adminD3Icon, "/assets/adminD3card.png");
        loadIcon(adminShieldIcon, "/assets/adminDshield.png");

    }

    // Backend API: call these to update counts dynamically
    public void setPendingApprovals(int value) {
        if (pendingApprovalsLabel != null) pendingApprovalsLabel.setText(String.valueOf(value));
    }

    public void setTotalBadges(int value) {
        if (totalBadgesLabel != null) totalBadgesLabel.setText(String.valueOf(value));
    }

    public void setActiveUsers(int value) {
        if (activeUsersLabel != null) activeUsersLabel.setText(String.valueOf(value));
    }

    // your loadIcon helper
    private void loadIcon(ImageView view, String path) {
        if (view == null) return;
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            view.setImage(img);
        } catch (Exception e) {
            System.err.println("Error loading icon " + path + ": " + e.getMessage());
        }
    }
}
