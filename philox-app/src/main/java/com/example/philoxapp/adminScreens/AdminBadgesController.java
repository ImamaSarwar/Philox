package com.example.philoxapp.adminScreens;

import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Admin;
import entity.Badge;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.Cursor;
import javafx.stage.*;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.stage.Popup;
import service.AdministrativeService;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AdminBadgesController {

    @FXML private VBox sidebarContainer;

    @FXML private GridPane badgesGrid;
    @FXML private TextField searchField;

    private final List<Badge> allBadges = new ArrayList<>();

    // layout constants
    private static final int COLS = 3;
    private static final double CARD_PREF_WIDTH = 300;
    private static final double IMAGE_SIZE = 60;
    
    Admin admin;

    public void setAdmin(Admin admin){
        this.admin = admin;

        SidePanel sidePanel = new SidePanel(admin, "Badges");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());
    }

    private List<Badge> loadBadgesFromDatabase(){
        return AdministrativeService.getAllBadges();
    }

    private boolean createNewBadge(Badge badge){
        return AdministrativeService.createNewBadge(badge);
    }

    private boolean deleteBadge(int badgeId){
        return AdministrativeService.deleteBadge(badgeId);
    }
    
    @FXML
    public void initialize() {
        allBadges.addAll(loadBadgesFromDatabase());
        populateGrid(allBadges);

        if (searchField != null) {
            // ensure placeholder and typed text are visible on the light gradient background
            // set explicit prompt text and colors so prompt, typed text and caret show up
            searchField.setPromptText("Search Badge Name...");
            searchField.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-border-color: transparent; " +
                            "-fx-prompt-text-fill: #9AA6B2; " +    // visible muted prompt
                            "-fx-text-fill: #0B2030; " +           // typed text color
                            "-fx-caret-color: #0B2030;"            // visible caret
            );

            // Make the field not active by default (no initial focus). Enable focus only after a click.
            searchField.setFocusTraversable(false);
            searchField.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                searchField.setFocusTraversable(true);
                searchField.requestFocus();
            });
            // When user leaves the field, make it inactive again so it won't grab focus automatically later.
            searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    searchField.setFocusTraversable(false);
                }
            });

            // real-time filtering listener (case-insensitive)
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                filterAndPopulate(newText);
            });
        }
    }

    private Window findOwnerWindow() {
        if (searchField != null && searchField.getScene() != null) {
            return searchField.getScene().getWindow();
        }
        if (badgesGrid != null && badgesGrid.getScene() != null) {
            return badgesGrid.getScene().getWindow();
        }
        for (Window w : Window.getWindows()) {
            if (w.isShowing()) return w;
        }
        return null;
    }

    @FXML
    private void onCreateBadgeClicked() {
        Window owner = findOwnerWindow();
        showCreateBadgePopup(owner);
    }

    private void showCreateBadgePopup(Window ownerWindow) {
        // try to find an owner Scene for in-scene overlay
        Scene ownerScene = null;
        if (ownerWindow instanceof Stage) {
            ownerScene = ((Stage) ownerWindow).getScene();
        }
        if (ownerScene == null && searchField != null && searchField.getScene() != null) {
            ownerScene = searchField.getScene();
        }

        // Build card UI (reduced height)
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(600);
        card.setMaxHeight(520); // reduced height per request
        card.setStyle("-fx-background-color: white; -fx-background-radius:12; -fx-effect: dropshadow(gaussian, rgba(16,24,32,0.12), 20, 0.5, 0, 6);");

        // Header row with title and close button
        HBox header = new HBox();
        Label title = new Label("Create Badge");
        title.setStyle("-fx-font-weight: 700; -fx-font-size: 16px; -fx-text-fill:#0B2030;");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 12px;");
        header.getChildren().addAll(title, headerSpacer, closeBtn);

        // Upload tile and upload button (clickable area)
        HBox uploadRow = new HBox(12);
        uploadRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconTile = new StackPane();
        iconTile.setPrefSize(60,72);
        iconTile.setStyle("-fx-background-color: linear-gradient(to bottom right, #00D5BE, #009689); -fx-background-radius:12;");

        Label tileEmoji = new Label("📤");
        tileEmoji.setStyle("-fx-font-size:20px; -fx-text-fill: #105B55;");
        // Make the emoji/tile look and act like a button
        iconTile.setCursor(Cursor.HAND);
        tileEmoji.setCursor(Cursor.HAND);

        ImageView preview = new ImageView();
        preview.setFitWidth(60);
        preview.setFitHeight(60);
        preview.setPreserveRatio(true);

        // Do NOT load fallback image into the popup. Show the emoji by default.
        iconTile.getChildren().add(tileEmoji);

        VBox uploadControls = new VBox(6);
        Button uploadBtn = new Button("📤  Upload PNG");
        uploadBtn.setStyle("-fx-background-radius:16; -fx-border-radius:16; -fx-border-color:#E6EAF0; -fx-background-color: white;");
        uploadBtn.setPrefHeight(34);

        Label smallHint = new Label("Recommended: 60 x 60px PNG");
        smallHint.setStyle("-fx-text-fill: #62748E; -fx-font-size:12px;");

        uploadControls.getChildren().addAll(uploadBtn, smallHint);

        uploadRow.getChildren().addAll(iconTile, uploadControls);

        // Name field (required)
        VBox nameBox = new VBox(4);
        Label nameLabel = new Label("Badge Name *");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0A0A0A");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter badge name");
        nameField.setPrefWidth(520);
        nameField.setStyle("-fx-background-color: white; -fx-border-color: #E6EAF0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        Label nameError = new Label("");
        nameError.setStyle("-fx-text-fill: #E24560; -fx-font-size: 12px;");
        nameError.setVisible(false);
        nameBox.getChildren().addAll(nameLabel, nameField, nameError);

        // Description box (max 100 characters excluding whitespace)
        VBox descBox = new VBox(4);
        Label descLabel = new Label("Badge Description");
        descLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0A0A0A");
        TextArea descField = new TextArea();
        descField.setPromptText("Describe what this badge represents");
        descField.setPrefRowCount(4);
        descField.setPrefHeight(90);
        descField.setWrapText(true);
        descField.setStyle("-fx-background-color: white; -fx-border-color: #E6EAF0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        Label descCharCount = new Label("0/100");
        descCharCount.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        descBox.getChildren().addAll(descLabel, descField, descCharCount);

        // enforce 100 chars ignoring whitespace/newlines
        descField.textProperty().addListener((obs, oldT, newT) -> {
            String filtered = newT.replaceAll("[\\s\\n\\r]", "");
            if (filtered.length() > 100) {
                // Revert to old text (so we keep original whitespace behavior)
                descField.setText(oldT);
                filtered = oldT.replaceAll("[\\s\\n\\r]", "");
            }
            descCharCount.setText(filtered.length() + "/100");
        });

        // Criteria row: vertically stacked inputs
        VBox criteriaBox = new VBox(8);
        Label criteriaLabel = new Label("Badge Criteria");
        criteriaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0A0A0A");

        HBox criteriaInputs = new HBox(12);
        criteriaInputs.setAlignment(Pos.CENTER_LEFT);

        // Participation count - integer spinner
        VBox pBox = new VBox(4);
        Label pLabel = new Label("Participation Count");
        Spinner<Integer> pSpinner = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
        pSpinner.setEditable(true);
        pSpinner.setPrefWidth(120);
        // Ensure the spinner has a valid initial value
        pSpinner.getValueFactory().setValue(0);
        pBox.getChildren().addAll(pLabel, pSpinner);

        // Rating threshold - double spinner with step 0.1 max 5.0
        VBox rBox = new VBox(4);
        Label rLabel = new Label("Rating Threshold");
        Spinner<Double> rSpinner = new Spinner<>(0.0, 5.0, 0.0, 0.1);
        rSpinner.setEditable(true);
        rSpinner.setPrefWidth(120);
        // Ensure the spinner has a valid initial value
        rSpinner.getValueFactory().setValue(0.0);
        rBox.getChildren().addAll(rLabel, rSpinner);

        // Application count - integer spinner
        VBox aBox = new VBox(4);
        Label aLabel = new Label("Application Count");
        Spinner<Integer> aSpinner = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
        aSpinner.setEditable(true);
        aSpinner.setPrefWidth(120);
        // Ensure the spinner has a valid initial value
        aSpinner.getValueFactory().setValue(0);
        aBox.getChildren().addAll(aLabel, aSpinner);

        criteriaInputs.getChildren().addAll(pBox, rBox, aBox);
        criteriaBox.getChildren().addAll(criteriaLabel, criteriaInputs);

        // criteria error label (visible like nameError)
        Label criteriaError = new Label("");
        criteriaError.setStyle("-fx-text-fill: #E24560; -fx-font-size: 12px;");
        criteriaError.setVisible(false);
        criteriaBox.getChildren().add(criteriaError);

        // Buttons row
        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #E6EAF0; -fx-border-radius: 20; -fx-padding:8 18;");
        Button createBtn = new Button("Create Badge");
        createBtn.setStyle("-fx-background-color: #009689; -fx-text-fill: white; -fx-background-radius:20; -fx-padding:8 18;");
        buttons.getChildren().addAll(cancelBtn, createBtn);

        card.getChildren().addAll(header, uploadRow, nameBox, descBox, criteriaBox, buttons);
        VBox.setVgrow(descBox, Priority.NEVER);

        // File chooser logic and selected file holder
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Images", "*.png"));
        final File[] selectedFile = new File[1];

        Runnable openChooser = () -> {
            Stage dialogOwner = null;
            if (ownerWindow instanceof Stage) dialogOwner = (Stage) ownerWindow;
            File f = chooser.showOpenDialog(dialogOwner);
            if (f != null && f.exists()) {
                selectedFile[0] = f;
                try {
                    Image img = new Image(f.toURI().toString(), 60, 60, true, true);
                    preview.setImage(img);
                    iconTile.getChildren().clear();
                    iconTile.getChildren().add(preview);
                } catch (Exception ignored) { }
            }
        };
        uploadBtn.setOnAction(e -> openChooser.run());
        iconTile.setOnMouseClicked(e -> openChooser.run());
        tileEmoji.setOnMouseClicked(e -> openChooser.run());

        // ---- Fix: ensure captured variables are final/effectively final ----
        final Scene finalOwnerScene = ownerScene; // final copy for lambdas
        final AtomicBoolean replacedRootFlag = new AtomicBoolean(false);

        // Overlay approach: prefer adding to the existing scene root
        if (finalOwnerScene != null) {
            Parent originalRoot = finalOwnerScene.getRoot();
            StackPane wrapper;
            if (originalRoot instanceof StackPane) {
                wrapper = (StackPane) originalRoot;
            } else {
                wrapper = new StackPane();
                wrapper.getChildren().add(originalRoot);
                finalOwnerScene.setRoot(wrapper);
                replacedRootFlag.set(true);
            }

            // Overlay with semi-transparent background
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
            overlay.setPickOnBounds(true);
            overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // ensure overlay fills wrapper
            overlay.prefWidthProperty().bind(wrapper.widthProperty());
            overlay.prefHeightProperty().bind(wrapper.heightProperty());

            overlay.getChildren().add(card);
            StackPane.setAlignment(card, Pos.CENTER);

            // close/remove overlay function
            Runnable removeOverlay = () -> {
                wrapper.getChildren().remove(overlay);
                if (replacedRootFlag.get()) {
                    // First remove the original root from the wrapper to avoid scene-graph conflicts
                    wrapper.getChildren().remove(originalRoot);
                    // Now restore the original root as scene root
                    finalOwnerScene.setRoot(originalRoot);
                }
            };

            // close handlers
            closeBtn.setOnAction(e -> removeOverlay.run());
            cancelBtn.setOnAction(e -> removeOverlay.run());

            // Create button validation and add badge (overlay path)
            createBtn.setOnAction(e -> {
                nameError.setVisible(false);
                criteriaError.setVisible(false);

                String name = nameField.getText() == null ? "" : nameField.getText().trim();
                if (name.isEmpty()) {
                    nameError.setText("Badge name is required");
                    nameError.setVisible(true);
                    return;
                }

                int pVal = pSpinner.getValue() == null ? 0 : pSpinner.getValue();
                double rVal = rSpinner.getValue() == null ? 0.0 : rSpinner.getValue();
                int aVal = aSpinner.getValue() == null ? 0 : aSpinner.getValue();

                boolean criteriaProvided = (pVal > 0) || (rVal > 0.0) || (aVal > 0);
                if (!criteriaProvided) {
                    criteriaError.setText("At least one badge criterion is required");
                    criteriaError.setVisible(true);
                    return;
                }

                if (rVal > 5.0) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Rating cannot be greater than 5.0", ButtonType.OK);
                    a.initOwner(ownerWindow instanceof Stage ? (Stage) ownerWindow : null);
                    a.showAndWait();
                    return;
                }

                String descText = descField.getText() == null ? "" : descField.getText().trim();
                String imagePath = selectedFile[0] == null ? null : selectedFile[0].toURI().toString();

                Badge newBadge = new Badge(name, descText, imagePath, pVal, aVal, rVal);

                if (createNewBadge(newBadge)) {
                    allBadges.add(0, newBadge);
                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Badge created successfully!", ButtonType.OK);
                    success.initOwner(ownerWindow instanceof Stage ? (Stage) ownerWindow : null);
                    success.showAndWait();
                    populateGrid(allBadges);
                    removeOverlay.run();
                }
                else{
                    Alert failure = new Alert(Alert.AlertType.ERROR, "Failed to create badge. Please try again.", ButtonType.OK);
                    failure.initOwner(ownerWindow instanceof Stage ? (Stage) ownerWindow : null);
                    failure.showAndWait();
                }

            });

            // Add overlay on top
            wrapper.getChildren().add(overlay);
            return;
        }

        // Fallback: if no scene available, use the old stage-based modal (keeps transparent stage but reduced height)
        Stage popupStage = new Stage(StageStyle.TRANSPARENT);
        if (ownerWindow != null) popupStage.initOwner(ownerWindow);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        // Backdrop
        Rectangle backdrop = new Rectangle();
        backdrop.setFill(Color.rgb(0, 0, 0, 0.25));
        javafx.geometry.Rectangle2D vb = javafx.stage.Screen.getPrimary().getVisualBounds();
        backdrop.setWidth(vb.getWidth());
        backdrop.setHeight(vb.getHeight());

        StackPane root = new StackPane(backdrop, card);
        StackPane.setAlignment(card, Pos.CENTER);

        // wire same handlers for fallback stage
        closeBtn.setOnAction(e -> popupStage.close());
        cancelBtn.setOnAction(e -> popupStage.close());

        // Create button validation and add badge (fallback stage path)
        createBtn.setOnAction(e -> {
            nameError.setVisible(false);
            criteriaError.setVisible(false);

            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isEmpty()) {
                nameError.setText("Badge name is required");
                nameError.setVisible(true);
                return;
            }

            int pVal = pSpinner.getValue() == null ? 0 : pSpinner.getValue();
            double rVal = rSpinner.getValue() == null ? 0.0 : rSpinner.getValue();
            int aVal = aSpinner.getValue() == null ? 0 : aSpinner.getValue();

            boolean criteriaProvided = (pVal > 0) || (rVal > 0.0) || (aVal > 0);
            if (!criteriaProvided) {
                criteriaError.setText("At least one badge criterion is required");
                criteriaError.setVisible(true);
                return;
            }

            if (rVal > 5.0) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Rating cannot be greater than 5.0", ButtonType.OK);
                a.initOwner(popupStage);
                a.showAndWait();
                return;
            }

            String descText = descField.getText() == null ? "" : descField.getText().trim();
            String imagePath = selectedFile[0] == null ? null : selectedFile[0].toURI().toString();

            Badge newBadge = new Badge(name, descText, imagePath, pVal, aVal, rVal);
            allBadges.add(0, newBadge);
            populateGrid(allBadges);
            popupStage.close();
        });

        Scene scene = new Scene(root, Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.setWidth(vb.getWidth());
        popupStage.setHeight(vb.getHeight());
        popupStage.showAndWait();
    }

    private void filterAndPopulate(String query) {
        if (query == null || query.trim().isEmpty()) {
            populateGrid(allBadges);
            return;
        }
        String q = query.trim().toLowerCase();
        List<Badge> filtered = allBadges.stream()
                .filter(b -> (b.getBadgeName() != null && b.getBadgeName().toLowerCase().contains(q))
                        || (b.getDescription() != null && b.getDescription().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        populateGrid(filtered);
    }

    // updated populateGrid(...) to work with a StackPane wrapper returned by createBadgeCard(...)
    private void populateGrid(List<Badge> badges) {
        if (badgesGrid == null) return;
        badgesGrid.getChildren().clear();

        // tighten grid spacing and push cards closer to the left edge
        badgesGrid.setPadding(new Insets(8, 8, 8, 4)); // top, right, bottom, left (small left padding)
        badgesGrid.setHgap(8); // less space between columns
        badgesGrid.setVgap(12);
        badgesGrid.setAlignment(Pos.TOP_LEFT);

        // Ensure columns distribute available width evenly and allow children to shrink/grow
        badgesGrid.getColumnConstraints().clear();
        for (int i = 0; i < COLS; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / COLS);
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            cc.setMinWidth(0); // allow column to shrink below card pref if needed
            badgesGrid.getColumnConstraints().add(cc);
        }

        int col = 0, row = 0;
        for (Badge b : badges) {
            StackPane cardWrapper = createBadgeCard(b);

            // Let the column control wrapper width; allow shrinking to prevent overflow
            cardWrapper.setPrefWidth(CARD_PREF_WIDTH);
            cardWrapper.setMinWidth(0);
            cardWrapper.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(cardWrapper, Priority.ALWAYS);

            badgesGrid.add(cardWrapper, col, row);
            // reduce outer spacing between cards
            GridPane.setMargin(cardWrapper, new Insets(8, 6, 8, 6));
            col++;
            if (col >= COLS) {
                col = 0;
                row++;
            }
        }
    }

    private StackPane createBadgeCard(Badge b) {
        // original card content (VBox)
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefWidth(CARD_PREF_WIDTH);
        root.setMinWidth(0);                 // allow grid to shrink card
        root.setMaxWidth(Double.MAX_VALUE);  // allow card to expand to column width
        root.setPadding(new Insets(18));
        root.setStyle(
                "-fx-background-radius:12; " +
                        "-fx-background-color: linear-gradient(to bottom right, white, white);"
        );

        DropShadow smallSharp = new DropShadow();
        smallSharp.setRadius(6);           // tighter blur
        smallSharp.setOffsetY(2);          // small offset
        smallSharp.setColor(Color.rgb(16, 24, 32, 0.06));

        root.setEffect(smallSharp);

        // Hover effect: lift + stronger shadow to create 3D pop
        root.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            DropShadow hoverLarge = new DropShadow();
            hoverLarge.setRadius(30);
            hoverLarge.setOffsetY(10);
            hoverLarge.setColor(Color.rgb(16, 24, 32, 0.18));

            DropShadow hoverSmall = new DropShadow();
            hoverSmall.setRadius(8);
            hoverSmall.setOffsetY(3);
            hoverSmall.setColor(Color.rgb(16, 24, 32, 0.09));

            hoverSmall.setInput(hoverLarge);
            root.setEffect(hoverSmall);

            root.setTranslateY(-6); // subtle lift
        });

        root.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            // restore base effect and position
            root.setEffect(smallSharp);
            root.setTranslateY(0);
        });

        // image
        ImageView iv = new ImageView();
        iv.setFitWidth(IMAGE_SIZE);
        iv.setFitHeight(IMAGE_SIZE);
        iv.setPreserveRatio(true);

        // Try loading provided imagePath; otherwise load fallback asset
        boolean imageLoaded = false;
        if (b.getIconPath() != null) {
            try {
                // Check if it's a runtime-saved badge image (uploads/badges/)
                if (b.getIconPath().startsWith("uploads/badges/")) {
                    // Convert to absolute file path
                    java.nio.file.Path imagePath = java.nio.file.Paths.get(b.getIconPath()).toAbsolutePath();
                    if (java.nio.file.Files.exists(imagePath)) {
                        Image img = new Image(imagePath.toUri().toString(), IMAGE_SIZE, IMAGE_SIZE, true, true);
                        iv.setImage(img);
                        imageLoaded = true;
                    }
                }
                // Check if it's a resource path (starts with /)
                else if (b.getIconPath().startsWith("/")) {
                    try (InputStream is = getClass().getResourceAsStream(b.getIconPath())) {
                        if (is != null) {
                            Image img = new Image(is, IMAGE_SIZE, IMAGE_SIZE, true, true);
                            iv.setImage(img);
                            imageLoaded = true;
                        }
                    } catch (Exception ignored) { }
                }
                // Try as direct URI/URL (for file:// URIs or web URLs)
                if (!imageLoaded) {
                    try {
                        Image img = new Image(b.getIconPath(), IMAGE_SIZE, IMAGE_SIZE, true, true);
                        if (img.getWidth() > 0) {
                            iv.setImage(img);
                            imageLoaded = true;
                        }
                    } catch (Exception ignored) { }
                }
            } catch (Exception ignored) {}
        }

        if (!imageLoaded) {
            try (InputStream fin = getClass().getResourceAsStream("/assets/fallBack.png")) {
                if (fin != null) {
                    Image fb = new Image(fin, IMAGE_SIZE, IMAGE_SIZE, true, true);
                    iv.setImage(fb);
                }
            } catch (Exception ignored) { /* leave iv empty if fallback missing */ }
        }

        // name
        Label name = new Label(b.getBadgeName());
        name.setStyle("-fx-text-fill:#1D293D; -fx-font-size:14px; -fx-font-weight:600;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);

        // description (center aligned)
        Label desc = new Label(b.getDescription());
        desc.setStyle("-fx-text-fill:#62748E; -fx-font-size:12px;");
        desc.setWrapText(true);
        desc.setMaxWidth(CARD_PREF_WIDTH - 36);
        desc.setTextAlignment(TextAlignment.CENTER);
        desc.setAlignment(Pos.CENTER);

        // light grey thin line above stats (replace Separator)
        Pane thinLine = new Pane();
        thinLine.setPrefHeight(1);
        thinLine.setMaxHeight(1);
        thinLine.setStyle("-fx-background-color: #E6EAF0;");
        VBox.setMargin(thinLine, new Insets(8, 0, 8, 0));

        // stats block (bottom criteria) — add internal padding so text isn't flush to card edge
        VBox stats = new VBox(8);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.setPadding(new Insets(8, 12, 12, 12)); // extra padding on left/right/bottom

        stats.getChildren().add(rowStat("Participation Count:", String.valueOf(b.getParticipationCount())));
        stats.getChildren().add(rowStat("Rating Threshold:", String.valueOf(b.getRatingThreshold())));
        stats.getChildren().add(rowStat("Application Count:", String.valueOf(b.getApplicationCount())));

        // assemble card content
        root.getChildren().addAll(iv, name, desc, thinLine, stats);
        VBox.setVgrow(stats, Priority.ALWAYS);

        // Wrapper StackPane: overlay the three-dot button so it doesn't affect layout
        StackPane wrapper = new StackPane();
        wrapper.getChildren().add(root);

        // three vertical dots button (overlay). Bigger, bold and colored #45556C.
        Button menuBtn = new Button("⋮");
        menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #45556C; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 6;");
        menuBtn.setCursor(Cursor.HAND);
        menuBtn.setFocusTraversable(false);

        // place the button on top-right without changing card layout; add a small margin so it sits inside the card padding.
        StackPane.setAlignment(menuBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(menuBtn, new Insets(6, 8, 0, 0)); // small offset from top-right corner

        wrapper.getChildren().add(menuBtn);

        // optional: hover style for the button only (no layout impact)
        menuBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> menuBtn.setStyle("-fx-background-color: rgba(0,0,0,0.04); -fx-text-fill: #45556C; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 6; -fx-background-radius: 6;"));
        menuBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #45556C; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 6;"));

        // --- Popup dropdown with drop shadow and delete action ---
        Popup menuPopup = new Popup();
        menuPopup.setAutoHide(true);

        VBox menuBox = new VBox();
        menuBox.setSpacing(4);
        menuBox.setStyle("-fx-background-color: white; -fx-background-radius:8; -fx-padding: 6 10; -fx-border-color: transparent;");
        menuBox.setEffect(new DropShadow(8, 0, 2, Color.rgb(16,24,32,0.12)));
        menuBox.setPickOnBounds(true);

        HBox deleteRow = new HBox(8);
        deleteRow.setAlignment(Pos.CENTER_LEFT);
        deleteRow.setPadding(new Insets(6, 6, 6, 6));
        deleteRow.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Use simple trash glyph (no variation selector) and make it slightly bolder/larger
        Label delIcon = new Label("🗑");
        delIcon.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #717182; -fx-background-color: transparent;");
        // Avoid forcing any background or shape on the icon label (prevents rectangular artifact)

        Label delText = new Label("Delete Badge");
        delText.setStyle("-fx-text-fill: #E7000B; -fx-font-size: 13px; -fx-font-weight: 600;");

        deleteRow.getChildren().addAll(delIcon, delText);

        // delete handler: remove badge and refresh grid
        deleteRow.setOnMouseClicked(evt -> {
            menuPopup.hide();
            // show confirmation overlay; on confirm remove badge and refresh
            showDeleteConfirmOverlay(b, () -> {
                if (deleteBadge(b.getBadgeId())) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION, "Badge deleted successfully!", ButtonType.OK);
                    success.initOwner(findOwnerWindow() instanceof Stage ? (Stage) findOwnerWindow() : null);
                    success.showAndWait();
                    allBadges.remove(b);
                    populateGrid(allBadges);
                }
                else{
                    Alert failure = new Alert(Alert.AlertType.ERROR, "Failed to delete badge. Please try again.", ButtonType.OK);
                    failure.initOwner(findOwnerWindow() instanceof Stage ? (Stage) findOwnerWindow() : null);
                    failure.showAndWait();
                }
            });
        });

        menuBox.getChildren().add(deleteRow);
        menuPopup.getContent().add(menuBox);

        // show/hide popup aligned near the three-dot button without affecting layout
        menuBtn.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            if (menuPopup.isShowing()) {
                menuPopup.hide();
                return;
            }

            // Ensure CSS is applied and compute a reliable preferred width (works before first show)
            menuBox.applyCss();
            double w = menuBox.prefWidth(-1);
            if (Double.isNaN(w) || w <= 0) {
                // fallback if prefWidth not available yet
                menuBox.layout();
                w = menuBox.getLayoutBounds().getWidth();
            }

            // Use the button's bottom-left screen point so the popup appears below the dots and shifted left
            Point2D btnBottomLeft = menuBtn.localToScreen(0, menuBtn.getBoundsInLocal().getHeight());
            double btnWidth = menuBtn.getBoundsInLocal().getWidth();

            double x = btnBottomLeft.getX() - w + btnWidth - 8; // left-shift so popup sits to the left of dots
            double y = btnBottomLeft.getY() + 6;                 // slight gap below the button

            menuPopup.show(menuBtn, x, y);
        });

        // ensure wrapper behaves like the card for sizing in the grid
        wrapper.setPrefWidth(CARD_PREF_WIDTH);
        wrapper.setMinWidth(0);
        wrapper.setMaxWidth(Double.MAX_VALUE);

        return wrapper;
    }

    private void showDeleteConfirmOverlay(Badge b, Runnable onConfirm) {
        Scene ownerScene = null;
        if (searchField != null && searchField.getScene() != null) ownerScene = searchField.getScene();
        else if (badgesGrid != null && badgesGrid.getScene() != null) ownerScene = badgesGrid.getScene();

        final Scene finalOwnerScene = ownerScene;
        if (finalOwnerScene != null) {
            final Parent originalRoot = finalOwnerScene.getRoot();
            final StackPane wrapper;
            final boolean[] replacedRoot = { false };

            if (originalRoot instanceof StackPane) {
                wrapper = (StackPane) originalRoot;
            } else {
                wrapper = new StackPane();
                // add the existing root into the wrapper
                wrapper.getChildren().add(originalRoot);
                finalOwnerScene.setRoot(wrapper);
                replacedRoot[0] = true;
            }

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
            overlay.setPickOnBounds(true);
            overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            overlay.prefWidthProperty().bind(wrapper.widthProperty());
            overlay.prefHeightProperty().bind(wrapper.heightProperty());

            // Only consume clicks that target the backdrop itself so dialog children still receive events
            overlay.setOnMouseClicked(ev -> {
                if (ev.getTarget() == overlay) {
                    ev.consume();
                }
            });

            VBox dialog = new VBox(12);
            dialog.setAlignment(Pos.CENTER);
            dialog.setPadding(new Insets(20));
            dialog.setPrefWidth(520);
            dialog.setMaxWidth(520);
            dialog.setPrefHeight(200);
            dialog.setMaxHeight(Region.USE_PREF_SIZE);
            dialog.setStyle("-fx-background-color: white; -fx-background-radius:12;");
            dialog.setEffect(new DropShadow(12, 0, 4, Color.rgb(16,24,32,0.14)));

            Label icon = new Label("⚠");
            icon.setStyle("-fx-text-fill: #E7000B; -fx-font-size: 28px; -fx-font-weight: 700;");
            VBox.setMargin(icon, new Insets(0, 0, 4, 0));

            Label title = new Label("Delete Badge");
            title.setStyle("-fx-font-size:16px; -fx-font-weight:700; -fx-text-fill:#0B2030;");

            Label message = new Label("Are you sure you want to delete " + b.getBadgeName() + " ?");
            message.setWrapText(true);
            message.setStyle("-fx-text-fill:#45556C; -fx-font-size:13px;");

            Label subText = new Label("This action cannot be undone.");
            subText.setStyle("-fx-text-fill:#9AA6B2; -fx-font-size:12px;");

            HBox buttons = new HBox(12);
            buttons.setAlignment(Pos.CENTER);

            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #CAD5E2; -fx-border-radius:20; -fx-padding:8 20;");
            cancelBtn.setCursor(Cursor.HAND);

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #E7000B; -fx-text-fill: white; -fx-background-radius:20; -fx-padding:8 20;");
            deleteBtn.setCursor(Cursor.HAND);

            buttons.getChildren().addAll(cancelBtn, deleteBtn);
            dialog.getChildren().addAll(icon, title, message, subText, buttons);

            overlay.getChildren().add(dialog);
            StackPane.setAlignment(dialog, Pos.CENTER);

            // removeOverlay: remove overlay and, if we replaced the scene root, detach originalRoot from wrapper BEFORE restoring it
            Runnable removeOverlay = () -> {
                // remove overlay if present
                if (wrapper.getChildren().contains(overlay)) {
                    wrapper.getChildren().remove(overlay);
                }
                if (replacedRoot[0]) {
                    // detach originalRoot from wrapper to avoid parenting/stylesheet leakage
                    if (wrapper.getChildren().contains(originalRoot)) {
                        wrapper.getChildren().remove(originalRoot);
                    }
                    finalOwnerScene.setRoot(originalRoot);
                }
            };

            cancelBtn.setOnAction(e -> removeOverlay.run());
            deleteBtn.setOnAction(e -> {
                try {
                    onConfirm.run();
                } finally {
                    removeOverlay.run();
                }
            });

            wrapper.getChildren().add(overlay);
            return;
        }

        // Fallback modal Stage path
        Stage popupStage = new Stage(StageStyle.TRANSPARENT);
        Window ownerWindow = findOwnerWindow();
        if (ownerWindow instanceof Stage) popupStage.initOwner((Stage) ownerWindow);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        // Only consume clicks that target the backdrop itself so dialog buttons still work
        root.setOnMouseClicked(ev -> {
            if (ev.getTarget() == root) {
                ev.consume();
            }
        });

        VBox dialog = new VBox(12);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(20));
        dialog.setMaxWidth(520);
        dialog.setPrefWidth(520);
        dialog.setPrefHeight(200);
        dialog.setMaxHeight(Region.USE_PREF_SIZE);
        dialog.setStyle("-fx-background-color: white; -fx-background-radius:12;");
        dialog.setEffect(new DropShadow(12, 0, 4, Color.rgb(16,24,32,0.14)));

        Label icon = new Label("⚠");
        icon.setStyle("-fx-text-fill: #E7000B; -fx-font-size: 28px; -fx-font-weight: 700;");
        VBox.setMargin(icon, new Insets(0, 0, 4, 0));

        Label title = new Label("Delete Badge");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:700; -fx-text-fill:#0B2030;");

        Label message = new Label("Are you sure you want to delete " + b.getBadgeName() + " ?");
        message.setWrapText(true);
        message.setStyle("-fx-text-fill:#45556C; -fx-font-size:13px;");

        Label subText = new Label("This action cannot be undone.");
        subText.setStyle("-fx-text-fill:#9AA6B2; -fx-font-size:12px;");

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #CAD5E2; -fx-border-radius:20; -fx-padding:8 20;");
        cancelBtn.setCursor(Cursor.HAND);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #E7000B; -fx-text-fill: white; -fx-background-radius:20; -fx-padding:8 20;");
        deleteBtn.setCursor(Cursor.HAND);

        buttons.getChildren().addAll(cancelBtn, deleteBtn);

        dialog.getChildren().addAll(icon, title, message, subText, buttons);
        root.getChildren().add(dialog);
        StackPane.setAlignment(dialog, Pos.CENTER);

        cancelBtn.setOnAction(e -> popupStage.close());
        deleteBtn.setOnAction(e -> {
            try {
                onConfirm.run();
            } finally {
                popupStage.close();
            }
        });

        double stageW, stageH;
        if (ownerWindow != null && ownerWindow.getWidth() > 0 && ownerWindow.getHeight() > 0) {
            stageW = ownerWindow.getWidth();
            stageH = ownerWindow.getHeight();
        } else {
            javafx.geometry.Rectangle2D vb = javafx.stage.Screen.getPrimary().getVisualBounds();
            stageW = vb.getWidth();
            stageH = vb.getHeight();
        }

        Scene scene = new Scene(root, stageW, stageH, Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.setWidth(stageW);
        popupStage.setHeight(stageH);
        popupStage.showAndWait();
    }

    // helper for stat row: label on left (muted), value on right (darker)
    private HBox rowStat(String labelText, String valueText) {
        HBox h = new HBox();
        h.setAlignment(Pos.CENTER_LEFT);
        Label left = new Label(labelText);
        left.setStyle("-fx-text-fill:#45556C; -fx-font-size:12px;");
        Label right = new Label(valueText);
        right.setStyle("-fx-text-fill:#1D293D; -fx-font-size:12px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        h.getChildren().addAll(left, spacer, right);
        return h;
    }

    // create six dummy badges (backend will replace this later)
    /*
    private List<Badge> loadDummyBadges() {
        List<Badge> list = new ArrayList<>();
        list.add(new Badge("Dedicated Helper", "Helps others consistently.", "/assets/badge1.png", 10, 4.5, 5));
        list.add(new Badge("Active Contributor", "Regularly contributes to projects.", "/assets/badge2.png", 20, 4.8, 15));
        list.add(new Badge("First Steps", "Completed initial orientation.", "/assets/badge3.png", 1, 4.0, 1));
        list.add(new Badge("Rising Star", "Rapid early contributions.", "/assets/badge4.png", 5, 4.3, 3));
        list.add(new Badge("Team Player", "Works well within teams.", "/assets/badge5.png", 15, 4.6, 10));
        list.add(new Badge("Good Work", "Recognized for excellent tasks.", "/assets/badge6.png", 1, 4.0, 1));
        return list;
    }
     */
}
