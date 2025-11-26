package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Controller.UserSupportController;
import artifact.Backend.Models.DTO.TicketCreateRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.SupportTicket;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.Base64;

public class UserSupportView extends BorderPane {

    private final UserSupportController controller;
    private final TextField subjectField = new TextField();
    private final TextArea descriptionField = new TextArea();
    private final TableView<SupportTicket> table = new TableView<>();

    // Container for switching views
    private final StackPane contentArea = new StackPane();
    private VBox createTicketView;
    private VBox historyView;

    // Color Palette
    private static final String BACKGROUND_COLOR = "#F4F6F8";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String PRIMARY_COLOR = "#00A4BF";
    private static final String PRIMARY_HOVER_COLOR = "#008C9E";
    private static final String TEXT_HEADER_COLOR = "#1A202C";
    private static final String TEXT_BODY_COLOR = "#4A5568";
    private static final String INPUT_BORDER_COLOR = "#E2E8F0";

    public UserSupportView() {
        this.controller = new UserSupportController();
        
        // 1. Global View Styling
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        getStylesheets().add(getInlineStyleSheet());

        setTop(new UserNavbarView(controller, View.USER_SUPPORT));

        // 2. Main Wrapper
        VBox mainContainer = new VBox(25.0);
        mainContainer.setPadding(new Insets(40, 20, 40, 20));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // --- 3. Hero Section ---
        VBox heroSection = new VBox(8);
        heroSection.setAlignment(Pos.CENTER);
        
        Label heroTitle = new Label("How can we help you?");
        heroTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        // FIX: Use setStyle to enforce color, overriding any global CSS (like dark themes)
        heroTitle.setStyle("-fx-text-fill: " + TEXT_HEADER_COLOR + ";");
        
        Text heroSub = new Text("We are here to resolve your issues.");
        heroSub.setFont(Font.font("Segoe UI", 16));
        heroSub.setFill(Color.web(TEXT_BODY_COLOR));
        
        heroSection.getChildren().addAll(heroTitle, heroSub);

        // --- 4. Segmented Toggle Switch (The "Slider") ---
        HBox toggleContainer = createSegmentedControl();

        // --- 5. Content Views ---
        // Initialize the two different views
        createTicketView = createFormView();
        historyView = createHistoryView();
        
        // Add them to the stack pane
        contentArea.getChildren().addAll(createTicketView, historyView);
        contentArea.setMaxWidth(800);
        
        // Default State: Show Create Ticket
        showView(createTicketView);

        // --- 6. Assemble ---
        mainContainer.getChildren().addAll(heroSection, toggleContainer, contentArea);
        
        // Wrap in ScrollPane for safety on small screens
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        
        setCenter(scrollPane);
    }

    /**
     * Creates the modern tab/slider switcher
     */
    private HBox createSegmentedControl() {
        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(4));
        container.setMaxWidth(340);
        container.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 50px;");

        ToggleButton btnNew = createToggleOption("New Request", true);
        ToggleButton btnHistory = createToggleOption("My History", false);

        ToggleGroup group = new ToggleGroup();
        btnNew.setToggleGroup(group);
        btnHistory.setToggleGroup(group);
        btnNew.setSelected(true);

        // Add Listeners to switch views
        btnNew.setOnAction(e -> {
            if (btnNew.isSelected()) showView(createTicketView);
        });
        
        btnHistory.setOnAction(e -> {
            if (btnHistory.isSelected()) showView(historyView);
        });

        // Ensure one is always selected
        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) oldVal.setSelected(true);
        });

        container.getChildren().addAll(btnNew, btnHistory);
        return container;
    }

    private ToggleButton createToggleOption(String text, boolean isFirst) {
        ToggleButton btn = new ToggleButton(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(36);
        btn.setCursor(Cursor.HAND);
        btn.getStyleClass().add("segment-button");
        return btn;
    }

    private void showView(VBox viewToShow) {
        // Hide all
        createTicketView.setVisible(false);
        historyView.setVisible(false);
        
        // Show target
        viewToShow.setVisible(true);
        viewToShow.toFront();
        
        // Simple Fade In
        FadeTransition ft = new FadeTransition(Duration.millis(300), viewToShow);
        ft.setFromValue(0.4);
        ft.setToValue(1.0);
        ft.play();
    }

    // --- Sub-View: Create Ticket Form ---
    private VBox createFormView() {
        VBox card = createCardBase();
        
        // Header
        Label title = new Label("Open New Request");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(TEXT_HEADER_COLOR));

        // Subject
        VBox subjBox = new VBox(8);
        Label subLbl = new Label("Subject");
        subLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        subLbl.setTextFill(Color.web(TEXT_HEADER_COLOR));
        
        subjectField.setPromptText("e.g., Booking Refund Issue");
        subjectField.setPrefHeight(45);
        subjectField.getStyleClass().add("modern-text-field");
        
        subjBox.getChildren().addAll(subLbl, subjectField);
        
        // Description
        VBox descBox = new VBox(8);
        Label descLbl = new Label("Description");
        descLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        descLbl.setTextFill(Color.web(TEXT_HEADER_COLOR));
        
        descriptionField.setPromptText("Please provide as much detail as possible...");
        descriptionField.setPrefHeight(150);
        descriptionField.setWrapText(true);
        descriptionField.getStyleClass().add("modern-text-area");
        
        descBox.getChildren().addAll(descLbl, descriptionField);
        
        // Button
        Button submitBtn = new Button("Submit Request");
        submitBtn.setPrefHeight(45);
        submitBtn.setPrefWidth(200);
        submitBtn.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand;"
        );
        submitBtn.setOnAction(e -> handleCreateAction());
        
        HBox btnBox = new HBox(submitBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        
        card.getChildren().addAll(title, new Separator(), subjBox, descBox, btnBox);
        return card;
    }

    // --- Sub-View: History Table ---
    private VBox createHistoryView() {
        VBox card = createCardBase();
        
        // Header
        Label title = new Label("Your Support History");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(TEXT_HEADER_COLOR));
        
        setupTable();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(50);
        table.setPrefHeight(400);
        
        // Load Data
        table.setItems(controller.getMyTickets());
        
        card.getChildren().addAll(title, new Separator(), table);
        return card;
    }

    private VBox createCardBase() {
        VBox card = new VBox(25);
        card.setPadding(new Insets(35));
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 16px;");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.05));
        shadow.setRadius(20);
        shadow.setOffsetY(8);
        shadow.setBlurType(BlurType.GAUSSIAN);
        card.setEffect(shadow);
        
        return card;
    }

    private void setupTable() {
        // Table columns setup...
        TableColumn<SupportTicket, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<SupportTicket, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subject()));
        
        TableColumn<SupportTicket, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().toString()));
        statusCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<SupportTicket, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().createdAt()));
        dateCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SupportTicket, Void> actionCol = new TableColumn<>("Action");
        actionCol.setStyle("-fx-alignment: CENTER;");
        
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View Chat");
            {
                String defaultStyle = "-fx-background-color: #E0F2F1; -fx-text-fill: #00695C; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-padding: 5 15;";
                String hoverStyle = "-fx-background-color: #B2DFDB; -fx-text-fill: #004D40; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-padding: 5 15;";
                
                viewBtn.setStyle(defaultStyle);
                viewBtn.setCursor(Cursor.HAND);
                viewBtn.setOnMouseEntered(e -> viewBtn.setStyle(hoverStyle));
                viewBtn.setOnMouseExited(e -> viewBtn.setStyle(defaultStyle));
                viewBtn.setOnAction(e -> {
                    SupportTicket ticket = getTableView().getItems().get(getIndex());
                    openTicketChat(ticket);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        table.getColumns().setAll(idCol, subjectCol, statusCol, dateCol, actionCol);
    }

    private void handleCreateAction() {
        TicketCreateRequest request = new TicketCreateRequest(
            subjectField.getText(),
            descriptionField.getText()
        );
        ServiceResult result = controller.createTicket(request);

        if (result.isSuccess()) {
            AlertUtils.infoBox("Support ticket created successfully.", "Success");
            subjectField.clear();
            descriptionField.clear();
            table.setItems(controller.getMyTickets());
        } else {
            String errorMsg = result.getGlobalError();
            if (errorMsg == null) errorMsg = "Please check your input.";
            AlertUtils.errorBox(errorMsg, "Error");
        }
    }

    private void openTicketChat(SupportTicket ticket) {
        Stage chatStage = new Stage();
        chatStage.initModality(Modality.APPLICATION_MODAL);
        chatStage.setTitle("Ticket #" + ticket.id() + " - " + ticket.subject());
        
        TicketConversationView chatView = new TicketConversationView(ticket, controller);
        Scene scene = new Scene(chatView, 600, 500);
        chatStage.setScene(scene);
        chatStage.show();
    }

    private String getInlineStyleSheet() {
        String css = 
            // --- Toggle Button Styling ---
            ".segment-button {" +
            "    -fx-background-color: transparent;" +
            "    -fx-text-fill: #64748B;" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-font-size: 14px;" +
            "    -fx-background-radius: 50px;" +
            "}" +
            ".segment-button:selected {" +
            "    -fx-background-color: white;" +
            "    -fx-text-fill: " + PRIMARY_COLOR + ";" +
            "    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);" +
            "}" +
            
            // --- Table View Styling ---
            ".table-view {" +
            "    -fx-background-color: transparent;" +
            "    -fx-base: transparent;" +
            "    -fx-control-inner-background: transparent;" +
            "    -fx-table-cell-border-color: transparent;" +
            "    -fx-table-header-border-color: transparent;" +
            "    -fx-padding: 0;" +
            "}" +
            ".table-view .column-header-background {" +
            "    -fx-background-color: transparent;" +
            "}" +
            ".table-view .column-header {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-width: 0 0 1 0;" +
            "    -fx-border-color: #E2E8F0;" +
            "    -fx-padding: 10 5 10 5;" +
            "}" +
            ".table-view .column-header .label {" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-text-fill: #64748B;" +
            "    -fx-font-size: 13px;" +
            "}" +
            ".table-row-cell {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-width: 0 0 1 0;" +
            "    -fx-border-color: #F1F5F9;" +
            "}" +
            ".table-row-cell:hover {" +
            "    -fx-background-color: #F8FAFC;" +
            "}" +
            // *** CRITICAL FIX FOR TEXT VISIBILITY ***
            ".table-cell {" +
            "    -fx-text-fill: #334155;" + 
            "    -fx-font-size: 14px;" +
            "    -fx-padding: 0 10 0 10;" +
            "    -fx-alignment: CENTER-LEFT;" +
            "}" +
            
            // --- Input Styling ---
            ".modern-text-field, .modern-text-area {" +
            "    -fx-background-color: white;" +
            "    -fx-text-fill: #334155;" + // Forced dark text
            "    -fx-prompt-text-fill: #94A3B8;" +
            "    -fx-border-color: " + INPUT_BORDER_COLOR + ";" +
            "    -fx-border-radius: 8px;" +
            "    -fx-background-radius: 8px;" +
            "    -fx-padding: 10;" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-size: 14px;" +
            "}" +
            ".modern-text-field:focused, .modern-text-area:focused {" +
            "    -fx-border-color: " + PRIMARY_COLOR + ";" +
            "    -fx-background-color: #F8FDFF;" +
            "}";

        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes());
    }
}