package com.example.philoxapp.volunteerScreens;

import com.example.philoxapp.component.card.OpportunityCard;
import com.example.philoxapp.component.modal.OpportunityDetailsModalController;
import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Opportunity;
import entity.Volunteer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import service.OpportunityService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class VolunteerOpportunitiesController {

    @FXML private VBox sidebarContainer;

    @FXML private ComboBox<String> locationComboBox;
    @FXML private TextField searchField;
    @FXML private TilePane cardsTilePane;
    @FXML private ImageView selectorlocationIcon, card1locationIcon, card2locationIcon, card3locationIcon, card4locationIcon, card5locationIcon, card6locationIcon;

    // master and filtered
    private final ObservableList<Node> masterCards = FXCollections.observableArrayList();
    private FilteredList<Node> filteredCards;

    // known locations used to detect location text inside cards
    private final List<String> knownLocations = Arrays.asList("karachi", "lahore", "islamabad", "rawalpindi", "multan");

    Volunteer volunteer;

    public void setVolunteer(Volunteer volunteer){
        this.volunteer = volunteer;

        // Setup sidebar with new SidePanel system
        SidePanel sidePanel = new SidePanel(volunteer, "Opportunities");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());
    }

    public void loadOpportunitiesfromDatabase() {
        List<Opportunity> opportunities = OpportunityService.getAllOpenOpportunities();
        cardsTilePane.getChildren().clear();
        for (Opportunity opp : opportunities) {
            Node card = createOpportunityCard(opp);
            cardsTilePane.getChildren().add(card);
        }
    }

    private Node createOpportunityCard(Opportunity opp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/opportunityCard.fxml"));
            AnchorPane card = loader.load();
            OpportunityCard cardController = loader.getController();
            cardController.setOpportunity(opp);

            // Store the Opportunity object in the card's properties for later retrieval
            card.getProperties().put("opportunity", opp);

            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return new Label("Error loading card");
        }
    }

    @FXML
    public void initialize() {
        /*
        loadIcon(card1locationIcon, "/assets/locationIcon.png");
        loadIcon(card2locationIcon, "/assets/locationIcon.png");
        loadIcon(card3locationIcon, "/assets/locationIcon.png");
        loadIcon(card4locationIcon, "/assets/locationIcon.png");
        loadIcon(card5locationIcon, "/assets/locationIcon.png");
        loadIcon(card6locationIcon, "/assets/locationIcon.png");
        loadIcon(selectorlocationIcon, "/assets/locationIcon.png");
        */

        loadOpportunitiesfromDatabase();

        // ComboBox items
        locationComboBox.setItems(FXCollections.observableArrayList(
                "All Locations", "Karachi", "Lahore", "Islamabad", "Rawalpindi", "Multan"
        ));
        locationComboBox.getSelectionModel().selectFirst();

        // simple cell factory (keeps visuals)
        locationComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });

        // run after layout so cardsTilePane children exist
        Platform.runLater(() -> {
            if (cardsTilePane == null) return;

            List<Node> initial = new ArrayList<>(cardsTilePane.getChildren());

            // prepare each card: cache original style/effect/scale and hover props and detect location
            for (Node n : initial) {
                String origStyle = n.getStyle() == null ? "" : n.getStyle();
                n.getProperties().put("origStyle", origStyle);

                Object origEffect = n.getEffect();
                n.getProperties().put("origEffect", origEffect);

                double origScaleX = n.getScaleX();
                double origScaleY = n.getScaleY();
                n.getProperties().put("origScaleX", origScaleX);
                n.getProperties().put("origScaleY", origScaleY);

                // hover style: green border with rounded corners (keeps card look)
                String hoverStyle = "-fx-border-color: #00bfa5; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;";
                n.getProperties().put("hoverStyle", hoverStyle);

                // prepare hover DropShadow (thicker/darker)
                DropShadow hoverShadow = new DropShadow();
                hoverShadow.setRadius(18);
                hoverShadow.setOffsetY(6);
                hoverShadow.setColor(Color.rgb(0, 0, 0, 0.22));
                n.getProperties().put("hoverShadow", hoverShadow);

                // detect location text inside node and cache normalized lowercase in userData
                if (n instanceof Parent) {
                    String loc = findLocationInNode((Parent) n);
                    if (loc != null) {
                        n.setUserData(loc);
                    }
                }

                // add hover listeners
                n.setOnMouseEntered(evt -> {
                    try {
                        Object hs = n.getProperties().get("hoverShadow");
                        if (hs instanceof DropShadow) n.setEffect((DropShadow) hs);

                        String base = (String) n.getProperties().getOrDefault("origStyle", "");
                        String hover = (String) n.getProperties().getOrDefault("hoverStyle", "");
                        if (base == null) base = "";
                        if (hover == null) hover = "";
                        String combined = base.isEmpty() ? hover : (base + (base.endsWith(";") ? "" : ";") + hover);
                        n.setStyle(combined);

                        Object sxObj = n.getProperties().get("origScaleX");
                        Object syObj = n.getProperties().get("origScaleY");
                        double sx = sxObj instanceof Double ? (Double) sxObj : n.getScaleX();
                        double sy = syObj instanceof Double ? (Double) syObj : n.getScaleY();
                        n.setScaleX(sx * 1.03);
                        n.setScaleY(sy * 1.05);
                    } catch (Exception ignored) { }
                });

                n.setOnMouseExited(evt -> {
                    try {
                        Object origEffectObj = n.getProperties().get("origEffect");
                        n.setEffect(origEffectObj instanceof javafx.scene.effect.Effect ? (javafx.scene.effect.Effect) origEffectObj : null);

                        String base = (String) n.getProperties().getOrDefault("origStyle", "");
                        n.setStyle(base == null ? "" : base);

                        Object sxObj = n.getProperties().get("origScaleX");
                        Object syObj = n.getProperties().get("origScaleY");
                        double sx = sxObj instanceof Double ? (Double) sxObj : 1.0;
                        double sy = syObj instanceof Double ? (Double) syObj : 1.0;
                        n.setScaleX(sx);
                        n.setScaleY(sy);
                    } catch (Exception ignored) { }
                });

                // attach "View Details" handler if present (search recursively)
                Button viewBtn = findButtonInNode(n, b -> "View Details".equals(b.getText()));
                if (viewBtn != null) {
                    viewBtn.setOnAction(evt -> {
                        // Retrieve the Opportunity object from the card's properties
                        Object opportunityObj = n.getProperties().get("opportunity");
                        if (opportunityObj instanceof Opportunity) {
                            Opportunity opportunity = (Opportunity) opportunityObj;
                            OpportunityDetailsModalController detailsController = new OpportunityDetailsModalController();
                            Window owner = cardsTilePane != null && cardsTilePane.getScene() != null ?
                                    cardsTilePane.getScene().getWindow() : null;
                            detailsController.showDetailsModal(volunteer, opportunity, owner);
                        }
                    });
                }
            }

            // set up master and filtered list and bind TilePane children to filtered view
            masterCards.setAll(initial);
            filteredCards = new FilteredList<>(masterCards, p -> true);

            filteredCards.addListener((ListChangeListener<Node>) c ->
                    cardsTilePane.getChildren().setAll(filteredCards)
            );

            // initialize TilePane
            cardsTilePane.getChildren().setAll(filteredCards);

            // ensure initial state matches combo selection
            applyLocationFilter(locationComboBox.getSelectionModel().getSelectedItem());
        });

        // listener for ComboBox selection
        locationComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> applyLocationFilter(newVal))
        );

        // optional: when searchField text changes, refine filter (search by title/org inside card)
        searchField.textProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::applyCurrentFilters));

        // --- Ensure searchField is not focused on startup ---
        Platform.runLater(() -> {
            try {
                // prefer focusing the TilePane so searchField is inactive until clicked
                if (cardsTilePane != null) {
                    cardsTilePane.setFocusTraversable(false);
                    cardsTilePane.requestFocus();
                } else if (locationComboBox != null) {
                    locationComboBox.requestFocus();
                } else if (searchField != null && searchField.getParent() != null) {
                    // fallback: move focus to parent container
                    searchField.getParent().requestFocus();
                }
            } catch (Exception ignored) { }
        });

        // java
        Platform.runLater(() -> {
            try {
                if (cardsTilePane == null) return;

                // Prevent the TilePane itself from being focusable and remove its focus styling
                cardsTilePane.setFocusTraversable(false);
                cardsTilePane.setStyle((cardsTilePane.getStyle() == null ? "" : cardsTilePane.getStyle())
                        + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-focus-width: 0;");

                // Find enclosing ScrollPane (if any) and neutralize its focus behavior
                Node n = cardsTilePane; // use javafx.scene.Node (imported)
                while (n != null) {
                    Parent p = n.getParent();
                    if (p == null) break;
                    if (p instanceof ScrollPane) {
                        ScrollPane sp = (ScrollPane) p;
                        sp.setFocusTraversable(false);
                        sp.setStyle((sp.getStyle() == null ? "" : sp.getStyle())
                                + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-focus-width: 0;");
                        break;
                    }
                    // Parent is a subclass of Node, so this assignment is valid
                    n = p;
                }

                // Ensure scene root won't show a focus ring and move focus there
                Scene scene = cardsTilePane.getScene();
                if (scene != null && scene.getRoot() != null) {
                    Parent root = scene.getRoot();
                    root.setStyle((root.getStyle() == null ? "" : root.getStyle())
                            + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-focus-width: 0;");
                    root.setFocusTraversable(true);
                    root.requestFocus();
                    root.setFocusTraversable(false);
                }
            } catch (Exception ignored) { }
        });

    }

    // helper to apply based on current combo + search field
    private void applyCurrentFilters() {
        String sel = locationComboBox.getSelectionModel().getSelectedItem();
        applyLocationFilter(sel);
    }

    private void applyLocationFilter(String location) {
        if (filteredCards == null) return;

        if (location == null || "All Locations".equalsIgnoreCase(location)) {
            filteredCards.setPredicate(n -> {
                // optional search filtering by title text inside node
                return matchesSearch(n);
            });
            return;
        }

        String target = location.trim().toLowerCase(Locale.ROOT);

        filteredCards.setPredicate(n -> {
            // First check cached userData
            Object ud = n.getUserData();
            if (ud != null && ud.toString().trim().toLowerCase(Locale.ROOT).equals(target)) {
                return matchesSearch(n);
            }
            // fallback: search inside node now and cache it
            if (n instanceof Parent) {
                String loc = findLocationInNode((Parent) n);
                if (loc != null) {
                    n.setUserData(loc);
                    if (loc.equals(target)) return matchesSearch(n);
                }
            }
            return false;
        });
    }

    // small search: check if searchField is empty or card contains the search text in any Label
    private boolean matchesSearch(Node n) {
        String q = searchField == null ? "" : searchField.getText();
        if (q == null || q.isBlank()) return true;
        String low = q.trim().toLowerCase(Locale.ROOT);

        if (n instanceof Parent) {
            return findTextInNode((Parent) n, low);
        }
        return false;
    }

    // recursively search for a Label whose text equals a known location (returns normalized lower-case)
    private String findLocationInNode(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Label) {
                String txt = ((Label) child).getText();
                if (txt != null) {
                    String normalized = txt.trim().toLowerCase(Locale.ROOT);
                    if (knownLocations.contains(normalized)) {
                        return normalized;
                    }
                }
            } else if (child instanceof Parent) {
                String found = findLocationInNode((Parent) child);
                if (found != null) return found;
            }
        }
        return null;
    }

    // recursively search for text contained in any Label (for search field)
    private boolean findTextInNode(Parent parent, String needleLower) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof Label) {
                String txt = ((Label) child).getText();
                if (txt != null && txt.toLowerCase(Locale.ROOT).contains(needleLower)) return true;
            } else if (child instanceof Parent) {
                if (findTextInNode((Parent) child, needleLower)) return true;
            }
        }
        return false;
    }

    // find a Button in node tree by predicate
    private Button findButtonInNode(Node node, Predicate<Button> predicate) {
        if (node instanceof Button && predicate.test((Button) node)) return (Button) node;
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                Button found = findButtonInNode(child, predicate);
                if (found != null) return found;
            }
        }
        return null;
    }


}