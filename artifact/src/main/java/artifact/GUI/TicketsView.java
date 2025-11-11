package artifact.GUI;

import artifact.Backend.View;
import artifact.Backend.Controller.TicketsController;
import artifact.Backend.Models.Ticket;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Tickets View - Read Only for Admins
 */
public class TicketsView extends AnchorPane {

    private final TicketsController controller;
    private final TableView<Ticket> ticketsTable = new TableView<>();

    public TicketsView() {
        this.controller = new TicketsController();

        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.TICKETS);

        // --- Main Content ---
        AnchorPane mainContent = new AnchorPane();
        mainContent.setPrefHeight(560.0);
        mainContent.setPrefWidth(550.0);
        AnchorPane.setTopAnchor(mainContent, 0.0);
        AnchorPane.setLeftAnchor(mainContent, 200.0);

        // --- Table View ---
        Label tableTitle = new Label("Tickets");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 32.0));
        AnchorPane.setTopAnchor(tableTitle, 15.0);
        AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(ticketsTable, 69.0);
        AnchorPane.setLeftAnchor(ticketsTable, 14.0);
        AnchorPane.setRightAnchor(ticketsTable, 15.0);
        AnchorPane.setBottomAnchor(ticketsTable, 15.0);
        
        mainContent.getChildren().addAll(tableTitle, ticketsTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Initialize Controller ---
        controller.initialize(ticketsTable);
    }
}