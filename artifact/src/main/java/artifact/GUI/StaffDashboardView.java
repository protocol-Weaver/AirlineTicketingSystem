package artifact.GUI;

import artifact.Backend.Controller.StaffSupportController;
import artifact.Backend.Models.SupportTicket;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Base64;

public class StaffDashboardView extends BorderPane {

    private final StaffSupportController controller;
    private final TableView<SupportTicket> table = new TableView<>();

    // Modern SaaS Palette
    private static final String BACKGROUND_COLOR = "#F4F6F8";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String PRIMARY_COLOR = "#00A4BF";
    private static final String TEXT_HEADER = "#1A202C";
    private static final String TEXT_BODY = "#4A5568";
    private static final String BORDER_COLOR = "#E2E8F0";

    public StaffDashboardView() {
        this.controller = new StaffSupportController(); // Reuse admin logic
        
        // 1. Global View Styling
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        getStylesheets().add(getInlineStyleSheet());
        
        // 2. Navbar
        setTop(createNavbar());

        // 3. Main Content Wrapper
        VBox centerContent = new VBox(25);
        centerContent.setPadding(new Insets(40, 60, 40, 60));
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setMaxWidth(1200); // Prevent stretching on huge screens
        
        // --- Header Section ---
        VBox headerBox = new VBox(5);
        Label title = new Label("Incoming Support Tickets");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(TEXT_HEADER));
        title.setStyle("-fx-text-fill: " + TEXT_HEADER + ";");
        
        Label subtitle = new Label("Manage and resolve customer inquiries.");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web(TEXT_BODY));
        subtitle.setStyle("-fx-text-fill: " + TEXT_BODY + ";");
            
        headerBox.getChildren().addAll(title, subtitle);
        
        // --- Table Card ---
        StackPane tableCard = new StackPane();
        tableCard.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 12px; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 12px;");
        tableCard.setPadding(new Insets(0)); // Table fills card
        
        // Soft Shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(20);
        shadow.setOffsetY(4);
        shadow.setBlurType(BlurType.GAUSSIAN);
        tableCard.setEffect(shadow);
        
        // Table Configuration
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(52); // Comfortable row height
        table.setPrefHeight(600);
        
        tableCard.getChildren().add(table);
        
        // Assemble
        centerContent.getChildren().addAll(headerBox, tableCard);
        
        // Use a ScrollPane for the whole center area if screen is small
        ScrollPane scrollWrapper = new ScrollPane(centerContent);
        scrollWrapper.setFitToWidth(true);
        scrollWrapper.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        setCenter(scrollWrapper);
        
        // Logic Initialization
        controller.initialize(table);
    }

    private BorderPane createNavbar() {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(16, 40, 16, 40));
        nav.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");
        
        // Brand
        Label brand = new Label("Skyline Staff Panel");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web(PRIMARY_COLOR)); // Teal branding
        
        // Logout Button
        Button logout = new Button("Log Out");
        logout.setCursor(Cursor.HAND);
        logout.getStyleClass().add("logout-button");
        logout.setOnAction(e -> controller.goLogin());
        
        nav.setLeft(brand);
        nav.setRight(logout);
        
        return nav;
    }

    /**
     * Injects CSS for the "Modern SaaS" table look and buttons.
     */
    private String getInlineStyleSheet() {
        String css = 
            // --- Table View Styling ---
            ".table-view {" +
            "    -fx-background-color: transparent;" +
            "    -fx-base: transparent;" +
            "    -fx-control-inner-background: transparent;" +
            "    -fx-table-cell-border-color: transparent;" +
            "    -fx-table-header-border-color: transparent;" +
            "    -fx-padding: 0;" +
            "}" +
            // Header
            ".table-view .column-header-background {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-width: 0 0 1 0;" +
            "}" +
            ".table-view .column-header {" +
            "    -fx-background-color: transparent;" +
            "    -fx-size: 45px;" +
            "    -fx-padding: 0 10 0 15;" + // Left padding alignment
            "}" +
            ".table-view .column-header .label {" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-text-fill: " + TEXT_BODY + ";" +
            "    -fx-font-size: 12px;" +
            "    -fx-alignment: CENTER-LEFT;" +
            "}" +
            // Rows
            ".table-row-cell {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-width: 0 0 1 0;" +
            "}" +
            ".table-row-cell:hover {" +
            "    -fx-background-color: #F8FAFC;" +
            "}" +
            ".table-row-cell:selected {" +
            "    -fx-background-color: #E0F7FA;" + // Very light teal selection
            "}" +
            // Cells
            ".table-cell {" +
            "    -fx-text-fill: " + TEXT_HEADER + ";" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-size: 14px;" +
            "    -fx-alignment: CENTER-LEFT;" +
            "    -fx-padding: 0 10 0 15;" +
            "}" +
            ".table-cell:selected {" +
            "    -fx-text-fill: #006064;" + // Dark teal text on selection
            "}" +
            // Hide ugly empty header column
            ".table-view .filler {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-width: 0 0 1 0;" +
            "}" +
            
            // --- Logout Button ---
            ".logout-button {" +
            "    -fx-background-color: transparent;" +
            "    -fx-text-fill: " + TEXT_BODY + ";" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-font-size: 13px;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-radius: 6px;" +
            "    -fx-background-radius: 6px;" +
            "    -fx-padding: 6 16;" +
            "}" +
            ".logout-button:hover {" +
            "    -fx-background-color: #FEF2F2;" + // Subtle red tint
            "    -fx-text-fill: #EF4444;" + // Red text
            "    -fx-border-color: #FECACA;" +
            "}" +
            
            // ScrollPane
            ".scroll-pane > .viewport {" +
            "    -fx-background-color: transparent;" +
            "}";

        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes());
    }
}