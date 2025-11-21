package artifact.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.Models;

/**
 * NEW: A reusable, custom-designed card to display one flight result.
 * This is the "cool design" part.
 */
public class FlightResultCardView extends AnchorPane {

    public FlightResultCardView(FlightSearchResult result, Consumer<FlightSearchResult> onBookAction) {
        setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4);");
        setPadding(new Insets(20.0));
        setMaxWidth(800.0);

        HBox mainLayout = new HBox(24.0);
        mainLayout.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setTopAnchor(mainLayout, 20.0);
        AnchorPane.setBottomAnchor(mainLayout, 20.0);
        AnchorPane.setLeftAnchor(mainLayout, 20.0);
        AnchorPane.setRightAnchor(mainLayout, 20.0);

        // --- Left Side: Flight Info ---
        VBox flightInfo = new VBox(12.0);
        
        // Times and Airports
        HBox routeBox = new HBox(16.0);
        routeBox.setAlignment(Pos.CENTER_LEFT);
        Label time = new Label("10:00 AM"); // Mock time
        time.setFont(Font.font("System", FontWeight.BOLD, 18.0));
        
        VBox depBox = createAirportLabel(
        result.departureAirport().name(),
        result.departureAirport().location()
        );

        Label departure = (Label) depBox.getChildren().get(0);
        VBox arrBox = createAirportLabel(result.arrivalAirport().name(), result.arrivalAirport().location());
        Label arrival = (Label) arrBox.getChildren().get(0);
        
        SVGPath arrow = new SVGPath();
        arrow.setContent("M17.25 8.25L21 12m0 0l-3.75 3.75M21 12H3");
        arrow.setStyle("-fx-stroke: #00a4bf; -fx-stroke-width: 2.5;");
        
        routeBox.getChildren().addAll(time, depBox, arrow, arrBox);

        // Date and Aircraft
        Label date = new Label(result.flight().departureTime().format(Models.DATE_FORMATTER));
        date.setFont(Font.font(14.0));
        date.setTextFill(Color.web("#555"));
        Label aircraft = new Label(result.aircraft().type());
        aircraft.setFont(Font.font(14.0));
        aircraft.setTextFill(Color.web("#555"));
        
        HBox detailsBox = new HBox(16.0, date, new Label("•"), aircraft);

        flightInfo.getChildren().addAll(routeBox, detailsBox);

        // --- Right Side: Price and Button ---
        VBox priceSection = new VBox(8.0);
        priceSection.setAlignment(Pos.CENTER_RIGHT);
        
        Label price = new Label("$295.50"); // Mock price
        price.setFont(Font.font("System", FontWeight.BOLD, 26.0));
        price.setTextFill(Color.web("#080c53"));
        
        Label seats = new Label(result.flight().availableSeats() + " seats left");
        seats.setFont(Font.font(14.0));
        seats.setTextFill(Color.ORANGE.darker());
        
        priceSection.getChildren().addAll(price, seats);

        Button bookButton = new Button("Book Now");
        bookButton.setPrefHeight(40.0);
        bookButton.setPrefWidth(120.0);
        bookButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        bookButton.setTextFill(Color.WHITE);
        bookButton.setStyle("-fx-background-color: #00a4bf; -fx-background-radius: 8px;");
        bookButton.setCursor(Cursor.HAND);
        
        // --- Action ---
        bookButton.setOnAction(e -> onBookAction.accept(result));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        mainLayout.getChildren().addAll(flightInfo, spacer, priceSection, new Separator(javafx.geometry.Orientation.VERTICAL), bookButton);
        getChildren().add(mainLayout);
    }

    private VBox createAirportLabel(String code, String location) {
        VBox box = new VBox(-2.0); // Tight spacing
        Label codeLabel = new Label(code);
        codeLabel.setFont(Font.font("System", FontWeight.BOLD, 22.0));
        codeLabel.setTextFill(Color.web("#080c53"));
        Label locLabel = new Label(location);
        locLabel.setFont(Font.font(14.0));
        locLabel.setTextFill(Color.web("#555"));
        box.getChildren().addAll(codeLabel, locLabel);
        return box;
    }
}