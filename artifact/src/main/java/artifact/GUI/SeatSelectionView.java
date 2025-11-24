package artifact.GUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.Set;

import artifact.Backend.Controller.FlightSearchState;
import artifact.Backend.Controller.SeatSelectionController; 

/**
 * UPDATED:
 * - Fetches real "taken seats" from the controller.
 * - Disables seats that are already PENDING or CONFIRMED.
 */
public class SeatSelectionView extends BorderPane {

    private final SeatSelectionController controller;
    private final FlightSearchState state;
    private final VBox seatMapContainer = new VBox(20);
    private final ToggleGroup seatGroup = new ToggleGroup();
    private final Button continueButton = new Button("Continue to Payment");
    
    // --- NEW: This set will hold the taken seats ---
    private final Set<String> takenSeats;

    public SeatSelectionView() {
        this.controller = new SeatSelectionController();
        this.state = FlightSearchState.getInstance();
        
        // --- NEW: Fetch taken seats BEFORE building the map ---
        this.takenSeats = controller.getTakenSeatsForCurrentFlight();
        // --- END NEW ---
        
        setStyle("-fx-background-color: #f4f8fb;");

        setTop(new UserNavbarView(controller, null)); // No active view

        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(32.0));
        
        // ... (Top Content: Title & Legend)
        Label title = new Label("Select Your Seat");
        title.setFont(Font.font("System", FontWeight.BOLD, 32.0));
        title.setTextFill(Color.web("#080c53"));
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
            createLegendItem("#b3e0ff", state.getSelectedCabin()),
            createLegendItem("#ccc", "Unavailable"),
            createLegendItem("#8d006c", "Selected")
        );
        VBox topBox = new VBox(24.0, title, legend);
        topBox.setAlignment(Pos.TOP_CENTER);
        contentPane.setTop(topBox);

        // ... (Center Content: Scrolling Seat Map)
        seatMapContainer.setAlignment(Pos.CENTER);
        seatMapContainer.setStyle("-fx-background-color: white; -fx-padding: 24px; -fx-background-radius: 12px; -fx-border-color: #eee; -fx-border-radius: 12px;");
        
        // --- This call will now use the "this.takenSeats" field ---
        buildSeatMap(); 
        
        ScrollPane scrollableSeatMap = new ScrollPane(seatMapContainer);
        scrollableSeatMap.setFitToWidth(true);
        scrollableSeatMap.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableSeatMap.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        BorderPane.setMargin(scrollableSeatMap, new Insets(24, 0, 24, 0));
        contentPane.setCenter(scrollableSeatMap);
        
        // ... (Bottom Content: Button)
        continueButton.setPrefHeight(45.0);
        continueButton.setMaxWidth(300.0);
        continueButton.setFont(Font.font("System", FontWeight.BOLD, 16.0));
        continueButton.setTextFill(Color.WHITE);
        continueButton.setStyle("-fx-background-color: #00a4bf; -fx-background-radius: 8px;");
        continueButton.setCursor(Cursor.HAND);
        continueButton.setDisable(true);
        continueButton.setOnAction(e -> controller.handleContinue());
        StackPane buttonPane = new StackPane(continueButton);
        buttonPane.setPadding(new Insets(16, 0, 0, 0));
        contentPane.setBottom(buttonPane);

        setCenter(contentPane);
    }
    
    /**
     * UPDATED: This method now uses the real 'takenSeats' set.
     */
    private void buildSeatMap() {
        String selectedCabin = state.getSelectedCabin();
        
        int rows = 20;
        String[] seatLetters = {"A", "B", "C", "", "D", "E", "F"};
        
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        
        for (int r = 1; r <= rows; r++) {
            for (int c = 0; c < seatLetters.length; c++) {
                if (seatLetters[c].isEmpty()) {
                    continue; // Aisle
                }
                
                String seatId = r + seatLetters[c];
                ToggleButton seat = new ToggleButton(seatId);
                seat.setToggleGroup(seatGroup);
                seat.setPrefSize(40, 40);
                seat.setCursor(Cursor.HAND);
                
                String cabin;
                if (r <= 4) cabin = "First Class";
                else if (r <= 8) cabin = "Business";
                else cabin = "Economy";
                
                // --- THIS IS THE FIX ---
                // Replaced hard-coded "isTaken" with a check against the real data
                boolean isTaken = this.takenSeats.contains(seatId);
                // --- END FIX ---
                
                if (isTaken) {
                    seat.setDisable(true);
                    seat.setStyle("-fx-background-color: #ccc; -fx-background-radius: 5px; -fx-font-size: 8px;");
                } else if (cabin.equals(selectedCabin)) {
                    // Available in user's class
                    seat.setDisable(false);
                    seat.setStyle("-fx-background-color: #b3e0ff; -fx-background-radius: 5px; -fx-font-size: 8px;");
                } else {
                    // Available, but wrong class
                    seat.setDisable(true);
                    seat.setStyle("-fx-background-color: #eee; -fx-background-radius: 5px; -fx-font-size: 8px;");
                }
                
                // Style for when selected
                seat.selectedProperty().addListener((o, old, isSelected) -> {
                    if (isSelected) {
                        seat.setStyle("-fx-background-color: #8d006c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-font-size: 8px;");
                        controller.handleSeatSelected(seatId);
                        continueButton.setDisable(false);
                    } else {
                        // Reset to available style
                        if (cabin.equals(selectedCabin) && !isTaken) {
                            seat.setStyle("-fx-background-color: #b3e0ff; -fx-background-radius: 5px; -fx-font-size: 8px;");
                        }
                    }
                });
                
                grid.add(seat, c, r);
            }
        }
        
        Label front = new Label("Front of Cabin");
        seatMapContainer.getChildren().addAll(front, grid);
    }
    
    private HBox createLegendItem(String color, String text) {
// ... (existing code) ...
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        Circle circle = new Circle(8, Color.web(color));
        box.getChildren().addAll(circle, new Text(text));
        return box;
    }
}