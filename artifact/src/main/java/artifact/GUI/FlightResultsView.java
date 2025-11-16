package artifact.GUI;

import artifact.Backend.View;
import artifact.Backend.Controller.FlightResultsController;
import artifact.Backend.Models.FlightSearchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class FlightResultsView extends BorderPane {

    private final FlightResultsController controller;
    private final VBox resultsContainer = new VBox(20.0);

    public FlightResultsView() {
        this.controller = new FlightResultsController();
        setStyle("-fx-background-color: #f4f8fb;");

        // --- Navigation Bar ---
        setTop(new UserNavbarView(controller, View.FLIGHT_RESULTS));
        
        // --- Center Content ---
        VBox centerContent = new VBox(24.0);
        centerContent.setPadding(new Insets(32.0));
        centerContent.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Available Flights");
        title.setFont(Font.font("System", FontWeight.BOLD, 32.0));
        title.setTextFill(Color.web("#080c53"));
        
        // --- Results ---
        resultsContainer.setAlignment(Pos.TOP_CENTER);
        
        ScrollPane scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        centerContent.getChildren().addAll(title, scrollPane);
        setCenter(centerContent);
        
        // --- Populate UI (Logic moved from Controller to View) ---
        populateResults();
    }

    private void populateResults() {
        List<FlightSearchResult> results = controller.getSearchResults();

        if (results.isEmpty()) {
            Label noResults = new Label("No flights found for this route or date.");
            noResults.setFont(Font.font(18.0));
            resultsContainer.setAlignment(Pos.CENTER);
            resultsContainer.getChildren().add(noResults);
        } else {
            resultsContainer.setAlignment(Pos.TOP_CENTER);
            for (FlightSearchResult result : results) {
                // The View creates the sub-view (Card) and passes the Controller's action
                FlightResultCardView card = new FlightResultCardView(result, controller::handleBookNow);
                resultsContainer.getChildren().add(card);
            }
        }
    }
}