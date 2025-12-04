package artifact.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import artifact.Backend.Models.FlightSearchResult;

/**
 * A modern, data-focused card for flight results.
 * Highlights Departure/Arrival times and Airport codes using REAL data.
 */
public class FlightResultCardView extends HBox {

    // Formatters for display
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public FlightResultCardView(FlightSearchResult result, Consumer<FlightSearchResult> onBookAction) {
        // --- Card Container Styling ---
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(20));
        this.setSpacing(25);
        this.setMaxWidth(900);
        this.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);"
        );

        // --- SECTION 1: Flight Route & Times (Left) ---
        VBox flightInfoSection = new VBox(12);
        flightInfoSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(flightInfoSection, Priority.ALWAYS);

        // 1.1 Header: Date & Aircraft (Top Left)
        // Using real departure date
        String dateString = result.flight().departureTime().format(DATE_FORMATTER);
        Label dateLabel = new Label(dateString);
        Label aircraftLabel = new Label(" • " + result.aircraft().type());
        
        dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        aircraftLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        HBox metaInfo = new HBox(dateLabel, aircraftLabel);

        // 1.2 Main Data Row: Time - Graphic - Time
        HBox routeData = new HBox(30);
        routeData.setAlignment(Pos.CENTER_LEFT);

        // Departure Data Block (Real Data)
        VBox depBlock = createDataBlock("DEPARTURE", 
            result.flight().departureTime().format(TIME_FORMATTER), 
            result.departureAirport().name(), 
            result.departureAirport().location(), 
            Pos.CENTER_LEFT);

        // Flight Path Graphic (Visual Connector)
        StackPane flightPath = createFlightGraphic();

        // Arrival Data Block (Real Data)
        VBox arrBlock = createDataBlock("ARRIVAL", 
            result.flight().arrivalTime().format(TIME_FORMATTER), 
            result.arrivalAirport().name(), 
            result.arrivalAirport().location(), 
            Pos.CENTER_RIGHT);

        routeData.getChildren().addAll(depBlock, flightPath, arrBlock);
        flightInfoSection.getChildren().addAll(metaInfo, routeData);

        // --- SECTION 2: Vertical Divider ---
        Separator verticalSeparator = new Separator();
        verticalSeparator.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // --- SECTION 3: Price & Book Action (Right) ---
        VBox actionSection = new VBox(5);
        actionSection.setAlignment(Pos.CENTER_RIGHT);
        actionSection.setMinWidth(160);

        // Price
        Label priceLabel = new Label("$295.50");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        priceLabel.setTextFill(Color.web("#2c3e50"));

        // Seats Left
        Label seatsLabel = new Label(result.flight().availableSeats() + " seats left");
        seatsLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 12px;");

        // Book Button
        Button bookButton = new Button("Book Flight");
        bookButton.setPrefWidth(140);
        bookButton.setPrefHeight(40);
        bookButton.setCursor(Cursor.HAND);
        bookButton.setStyle(
            "-fx-background-color: #00a4bf;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 6;"
        );
        bookButton.setOnAction(e -> onBookAction.accept(result));

        Region spacer = new Region();
        spacer.setPrefHeight(10);

        actionSection.getChildren().addAll(priceLabel, seatsLabel, spacer, bookButton);

        // --- Add all to Root ---
        this.getChildren().addAll(flightInfoSection, verticalSeparator, actionSection);
    }

    /**
     * Creates a vertical block of data: Label, Time, Airport Code, City
     */
    private VBox createDataBlock(String header, String time, String code, String city, Pos alignment) {
        VBox box = new VBox(2);
        box.setAlignment(alignment);

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e; -fx-font-weight: bold;");

        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(headerLabel, timeLabel, codeLabel, cityLabel);
        return box;
    }

    /**
     * Creates a simple, non-intrusive flight path graphic
     */
    private StackPane createFlightGraphic() {
        StackPane pane = new StackPane();
        pane.setMinWidth(100);
        pane.setAlignment(Pos.CENTER);

        // The line
        Region line = new Region();
        line.setMaxHeight(1);
        line.setStyle("-fx-background-color: #bdc3c7;");
        
        // The plane icon
        SVGPath plane = new SVGPath();
        plane.setContent("M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z");
        plane.setFill(Color.web("#bdc3c7"));
        plane.setRotate(90);
        plane.setScaleX(0.8);
        plane.setScaleY(0.8);

        pane.getChildren().addAll(line, plane);
        return pane;
    }
}