
package com.example.philoxapp.organisationScreens;

import entity.Opportunity;
import entity.Organisation;
import com.example.philoxapp.component.sidepanel.SidePanel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.OpportunityService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // <-- IMPORT ADDED
import java.util.Objects;

public class PostOpportunityController {

    // --- (All your FXML fields are correct) ---
    //@FXML private ImageView logoImage;
    //@FXML private ToggleButton dashboardButton, postOpportunityButton, manageButton, applicationsButton, profileButton, settingsButton;
    //@FXML private Button logoutButton;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField locationField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField durationField;
    @FXML private DatePicker closingDatePicker;
    @FXML private TextField capacityField;
    @FXML private Label titleError;
    @FXML private Label categoryError;
    @FXML private Label descriptionError;
    @FXML private Label locationError;
    @FXML private Label startDateError;
    @FXML private Label endDateError;
    @FXML private Label startTimeError;
    @FXML private Label durationError;
    @FXML private Label closingDateError;
    @FXML private Label capacityError;
    @FXML private Button submitButton;
    @FXML private Button saveDraftButton;
    @FXML private Label previewTitle;
    @FXML private Label previewCategory;
    @FXML private HBox previewLocationBox;
    @FXML private Label previewLocation;
    @FXML private HBox previewDateBox;
    @FXML private Label previewDate;
    @FXML private HBox previewCapacityBox;
    @FXML private Label previewCapacity;
    @FXML private Label previewDescription;

    @FXML private VBox sidebarContainer;

    private final DateTimeFormatter previewDateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private Organisation currentOrganisation;

    public void setOrganisation(Organisation organisation) {
        this.currentOrganisation = organisation;

        // Setup sidebar with new SidePanel system
        SidePanel sidePanel = new SidePanel(organisation, "Post Opportunity");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());


    }

    @FXML
    public void initialize() {

        categoryComboBox.getItems().addAll(
                "Environmental", "Education", "Healthcare",
                "Community Support", "Animal Welfare", "Arts & Culture"
        );
        setupPreviewListeners();
        titleField.setOnKeyReleased(e -> validateTitle());
        categoryComboBox.setOnAction(e -> validateCategory());
        descriptionArea.setOnKeyReleased(e -> validateDescription());
        locationField.setOnKeyReleased(e -> validateLocation());
        startTimeField.setOnKeyReleased(e -> validateTime());
        durationField.setOnKeyReleased(e -> validateDuration());
        capacityField.setOnKeyReleased(e -> validateCapacity());
        startDatePicker.setOnAction(e -> validateDates());
        endDatePicker.setOnAction(e -> validateDates());
        closingDatePicker.setOnAction(e -> validateDates());
    }

    private void setupPreviewListeners() {
        // (This method is unchanged and correct)
        titleField.textProperty().addListener((obs, oldV, newV) -> {
            previewTitle.setText(newV.isEmpty() ? "Opportunity Title" : newV);
        });
        categoryComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                previewCategory.setVisible(false);
                previewCategory.setManaged(false);
            } else {
                previewCategory.setText(newV);
                previewCategory.setVisible(true);
                previewCategory.setManaged(true);
            }
        });
        locationField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty()) {
                previewLocationBox.setVisible(false);
                previewLocationBox.setManaged(false);
            } else {
                previewLocation.setText(newV);
                previewLocationBox.setVisible(true);
                previewLocationBox.setManaged(true);
            }
        });
        descriptionArea.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty()) {
                previewDescription.setText("Description of the opportunity...");
                previewDescription.setVisible(false);
                previewDescription.setManaged(false);
            } else {
                previewDescription.setText(newV);
                previewDescription.setVisible(true);
                previewDescription.setManaged(true);
            }
        });
        capacityField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty() || !newV.matches("\\d+")) {
                previewCapacityBox.setVisible(false);
                previewCapacityBox.setManaged(false);
            } else {
                previewCapacity.setText(newV + " volunteers needed");
                previewCapacityBox.setVisible(true);
                previewCapacityBox.setManaged(true);
            }
        });
        startDatePicker.valueProperty().addListener((obs, oldV, newV) -> updatePreviewDate());
        endDatePicker.valueProperty().addListener((obs, oldV, newV) -> updatePreviewDate());
    }

    private void updatePreviewDate() {
        // (This method is unchanged and correct)
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start == null && end == null) {
            previewDateBox.setVisible(false);
            previewDateBox.setManaged(false);
            return;
        }
        String startStr = (start != null) ? start.format(previewDateFormatter) : "N/A";
        String endStr = (end != null) ? end.format(previewDateFormatter) : "N/A";
        if (Objects.equals(startStr, endStr)) {
            previewDate.setText(startStr);
        } else {
            previewDate.setText(startStr + " to " + endStr);
        }
        previewDateBox.setVisible(true);
        previewDateBox.setManaged(true);
    }


    // --- Form Submission ---

    @FXML
    private void handleSubmitForApproval() {
        if (!validateForm()) {
            return;
        }

        try {
            Opportunity opp = new Opportunity();
            opp.setTitle(titleField.getText());
            opp.setCategory(categoryComboBox.getValue());
            opp.setDescription(descriptionArea.getText());
            opp.setLocation(locationField.getText());
            opp.setStartDate(startDatePicker.getValue());
            opp.setEndDate(endDatePicker.getValue());

            if (!startTimeField.getText().isEmpty()) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                LocalTime startTime = LocalTime.parse(startTimeField.getText(), timeFormatter);
                opp.setStartTime(startTime);
            }

            opp.setDuration(Integer.parseInt(durationField.getText()));
            opp.setCapacity(Integer.parseInt(capacityField.getText()));
            opp.setCloseDate(closingDatePicker.getValue());

            if (currentOrganisation != null) {
                opp.setOrganisationId(currentOrganisation.getOrganisationId());
            }
            opp.setAsOpen();

            boolean success = OpportunityService.postOpportunity(opp);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            if (success) {
                alert.setTitle("Submission Successful");
                alert.setHeaderText("Opportunity Submitted for Approval");
                alert.setContentText("Your new opportunity has been submitted and is pending admin approval.");
                alert.showAndWait();
            }else{
                alert.setTitle("Submission Failed");
                alert.setHeaderText("Error Submitting Opportunity");
                alert.setContentText("There was an error submitting your opportunity. Please try again later.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            // This will catch the parsing error if the time format is wrong
            e.printStackTrace();
            showError("Error submitting opportunity: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveDraft() {
        // (This method is unchanged and correct)
        String title = titleField.getText();
        if (title.isEmpty()) {
            setError(titleError, "A title is required to save a draft.");
            return;
        }
        System.out.println("--- Saving as Draft ---");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draft Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your opportunity '" + title + "' has been saved as a draft.");
        alert.showAndWait();
    }

    // --- Validation Logic (All correct and unchanged) ---

    private boolean validateForm() {
        boolean titleValid = validateTitle();
        boolean categoryValid = validateCategory();
        boolean descriptionValid = validateDescription();
        boolean locationValid = validateLocation();
        boolean datesValid = validateDates();
        boolean timeValid = validateTime();
        boolean durationValid = validateDuration();
        boolean capacityValid = validateCapacity();
        return titleValid && categoryValid && descriptionValid && locationValid &&
                datesValid && timeValid && durationValid && capacityValid;
    }

    private boolean validateTitle() {
        if (titleField.getText().isEmpty()) {
            setError(titleError, "Title is required.");
            return false;
        }
        setError(titleError, null);
        return true;
    }

    private boolean validateCategory() {
        if (categoryComboBox.getValue() == null) {
            setError(categoryError, "Please select a category.");
            return false;
        }
        setError(categoryError, null);
        return true;
    }

    private boolean validateDescription() {
        if (descriptionArea.getText().length() < 50) {
            setError(descriptionError, "Description must be at least 50 characters.");
            return false;
        }
        setError(descriptionError, null);
        return true;
    }

    private boolean validateLocation() {
        if (locationField.getText().isEmpty()) {
            setError(locationError, "Location is required.");
            return false;
        }
        setError(locationError, null);
        return true;
    }

    private boolean validateTime() {
        if (startTimeField.getText().isEmpty()) {
            setError(startTimeError, "Start time is required.");
            return false;
        } else if (!startTimeField.getText().matches("^(0?[1-9]|1[0-2]):[0-5][0-9] (AM|PM)$")) {
            setError(startTimeError, "Invalid format. Use 'HH:MM AM/PM'.");
            return false;
        }
        setError(startTimeError, null);
        return true;
    }

    private boolean validateDuration() {
        try {
            int duration = Integer.parseInt(durationField.getText());
            if (duration <= 0) {
                setError(durationError, "Must be greater than 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            setError(durationError, "Must be a valid number (in hours).");
            return false;
        }
        setError(durationError, null);
        return true;
    }

    private boolean validateCapacity() {
        try {
            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                setError(capacityError, "Must be at least 1.");
                return false;
            }
        } catch (NumberFormatException e) {
            setError(capacityError, "Must be a valid number.");
            return false;
        }
        setError(capacityError, null);
        return true;
    }

    private boolean validateDates() {
        boolean isValid = true;
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        LocalDate closing = closingDatePicker.getValue();

        if (start == null) {
            setError(startDateError, "Start date is required.");
            isValid = false;
        } else if (start.isBefore(LocalDate.now())) {
            setError(startDateError, "Start date cannot be in the past.");
            isValid = false;
        } else {
            setError(startDateError, null);
        }
        if (end == null) {
            setError(endDateError, "End date is required.");
            isValid = false;
        } else {
            setError(endDateError, null);
        }
        if (closing == null) {
            setError(closingDateError, "Closing date is required.");
            isValid = false;
        } else {
            setError(closingDateError, null);
        }

        if (start != null && end != null && end.isBefore(start)) {
            setError(endDateError, "End date cannot be before the start date.");
            isValid = false;
        }

        if (start != null && closing != null && closing.isAfter(start)) {
            setError(closingDateError, "Closing date must be on or before the start date.");
            isValid = false;
        }
        return isValid;
    }

    private void setError(Label label, String message) {
        if (message == null || message.isEmpty()) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        } else {
            label.setText(message);
            label.setVisible(true);
            label.setManaged(true);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
