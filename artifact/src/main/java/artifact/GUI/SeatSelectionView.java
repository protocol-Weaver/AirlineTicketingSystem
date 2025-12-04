package artifact.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;
import java.util.Set;

import artifact.Backend.Controller.FlightSearchState;
import artifact.Backend.Controller.SeatSelectionController; 

public class SeatSelectionView extends BorderPane {

    private final SeatSelectionController controller;
    private final FlightSearchState state;
    private final VBox seatMapContainer = new VBox(20);
    private final Button continueButton = new Button("Continue to Payment");
    private final Set<String> takenSeats;
    private final Label instructionLabel;

    public SeatSelectionView() {
        this.controller = new SeatSelectionController();
        this.state = FlightSearchState.getInstance();
        this.takenSeats = controller.getTakenSeatsForCurrentFlight();
        
        setStyle("-fx-background-color: #f4f8fb;");

        setTop(new UserNavbarView(controller, null));

        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(32.0));
        
        // --- Header ---
        String titleText = state.getGuestCount() > 1 
            ? "Select " + state.getGuestCount() + " Seats" 
            : "Select Your Seat";
            
        Label title = new Label(titleText);
        title.setFont(Font.font("System", FontWeight.BOLD, 32.0));
        title.setTextFill(Color.web("#080c53"));
        
        instructionLabel = new Label("Please select your seats.");
        instructionLabel.setTextFill(Color.GRAY);

        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
            createLegendItem("#b3e0ff", state.getSelectedCabin()),
            createLegendItem("#ccc", "Unavailable"),
            createLegendItem("#8d006c", "Selected")
        );
        VBox topBox = new VBox(15.0, title, instructionLabel, legend);
        topBox.setAlignment(Pos.TOP_CENTER);
        contentPane.setTop(topBox);

        // --- Seat Map ---
        seatMapContainer.setAlignment(Pos.CENTER);
        seatMapContainer.setStyle("-fx-background-color: white; -fx-padding: 24px; -fx-background-radius: 12px; -fx-border-color: #eee; -fx-border-radius: 12px;");
        
        buildSeatMap(); 
        
        ScrollPane scrollableSeatMap = new ScrollPane(seatMapContainer);
        scrollableSeatMap.setFitToWidth(true);
        scrollableSeatMap.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableSeatMap.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        BorderPane.setMargin(scrollableSeatMap, new Insets(24, 0, 24, 0));
        contentPane.setCenter(scrollableSeatMap);
        
        // --- Bottom Button ---
        continueButton.setPrefHeight(45.0);
        continueButton.setMaxWidth(300.0);
        continueButton.setFont(Font.font("System", FontWeight.BOLD, 16.0));
        continueButton.setTextFill(Color.WHITE);
        continueButton.setStyle("-fx-background-color: #00a4bf; -fx-background-radius: 8px;");
        continueButton.setCursor(Cursor.HAND);
        continueButton.setDisable(true); // Initially disabled
        continueButton.setOnAction(e -> controller.handleContinue());
        StackPane buttonPane = new StackPane(continueButton);
        buttonPane.setPadding(new Insets(16, 0, 0, 0));
        contentPane.setBottom(buttonPane);

        setCenter(contentPane);
    }
    
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
                if (seatLetters[c].isEmpty()) continue;
                
                String seatId = r + seatLetters[c];
                ToggleButton seat = new ToggleButton(seatId);
                // No ToggleGroup here!
                seat.setPrefSize(40, 40);
                seat.setCursor(Cursor.HAND);
                
                String cabin;
                if (r <= 4) cabin = "First Class";
                else if (r <= 8) cabin = "Business";
                else cabin = "Economy";
                
                boolean isTaken = this.takenSeats.contains(seatId);
                
                // Set Initial Style
                updateSeatStyle(seat, isTaken, cabin.equals(selectedCabin), false);
                seat.setDisable(isTaken || !cabin.equals(selectedCabin));
                
                // Handle Click
                seat.setOnAction(e -> {
                    boolean isSelected = controller.toggleSeatSelection(seatId);
                    seat.setSelected(isSelected);
                    updateSeatStyle(seat, isTaken, cabin.equals(selectedCabin), isSelected);
                    checkCompletion();
                });
                
                grid.add(seat, c, r);
            }
        }
        
        Label front = new Label("Front of Cabin");
        seatMapContainer.getChildren().addAll(front, grid);
    }
    
    private void updateSeatStyle(ToggleButton seat, boolean taken, boolean correctCabin, boolean selected) {
        if (selected) {
            seat.setStyle("-fx-background-color: #8d006c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px; -fx-font-size: 8px;");
        } else if (taken) {
            seat.setStyle("-fx-background-color: #ccc; -fx-background-radius: 5px; -fx-font-size: 8px;");
        } else if (correctCabin) {
            seat.setStyle("-fx-background-color: #b3e0ff; -fx-background-radius: 5px; -fx-font-size: 8px;");
        } else {
            seat.setStyle("-fx-background-color: #eee; -fx-background-radius: 5px; -fx-font-size: 8px;");
        }
    }
    
    private void checkCompletion() {
        boolean complete = controller.isSelectionComplete();
        continueButton.setDisable(!complete);
        
        if (complete) {
            instructionLabel.setText("Selection complete!");
            instructionLabel.setTextFill(Color.GREEN);
        } else {
            int cur = state.getSelectedSeats().size();
            int tot = state.getGuestCount();
            instructionLabel.setText("Selected " + cur + " of " + tot + " seats.");
            instructionLabel.setTextFill(Color.GRAY);
        }
    }
    
    private HBox createLegendItem(String color, String text) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        Circle circle = new Circle(8, Color.web(color));
        box.getChildren().addAll(circle, new Text(text));
        return box;
    }
}