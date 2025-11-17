package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.Controller.UserBookingHomeController;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.DTO.FlightSearchRequest;
import artifact.Backend.Models.ServiceResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the multi-step wizard view for booking a flight.
 * This component handles the UI flow for selecting departure/arrival airports,
 * traveler details, and flight dates. It utilizes a {@link StackPane} to overlay
 * the modal on top of the parent view.
 */
public class BookingWizardView extends StackPane {

    private final UserBookingHomeController controller;
    private final Runnable onCloseAction;
    
    // State Variables
    private int currentStep = 1;
    private Airport selectedFrom;
    private Airport selectedTo;
    private LocalDate selectedDate;
    private String selectedCabin = "Economy";
    private int guestCount = 1;
    
    // UI Components
    private final BorderPane modal = new BorderPane();
    private final VBox contentArea = new VBox(20);
    private final Label step1Label = new Label("Flying From");
    private final Label step2Label = new Label("Flying To");
    private final Label step3Label = new Label("Who's Travelling");
    private final Label step4Label = new Label("Travelling When");
    private final Button nextButton = new Button("Next");
    private final Button closeButton = new Button("Cancel");
    
    // Step Containers
    private boolean isDepartureStep = true;
    private final VBox locationStepContainer;
    private final VBox travellerStepContainer;
    private final VBox dateStepContainer;
    
    // Internal UI Logic helpers
    private final ListView<Airport> airportList = new ListView<>();
    private final TextField searchField = new TextField();
    private final Label guestCountLabel = new Label("1 Guest");
    private final ComboBox<String> cabinBox = new ComboBox<>();
    private final TextField promoCode = new TextField();
    private final GridPane calendarGrid = new GridPane();
    private YearMonth currentMonth = YearMonth.now();
    private final Label monthLabel = new Label();
    
    // Data Caching
    private Set<LocalDate> availableDatesInMonth = new HashSet<>();
    private boolean isProgrammaticSelection = false;

    // Palette
    private static final String PRIMARY_COLOR = "#00A4BF";
    private static final String TEXT_HEADER = "#1A202C";
    private static final String TEXT_BODY = "#4A5568";
    private static final String BORDER_COLOR = "#E2E8F0";

    /**
     * Constructs the BookingWizardView.
     *
     * @param controller    The controller responsible for handling business logic and data retrieval.
     * @param onCloseAction A Runnable to execute when the wizard is closed or cancelled.
     */
    public BookingWizardView(UserBookingHomeController controller, Runnable onCloseAction) {
        this.controller = controller;
        this.onCloseAction = onCloseAction;
        
        // Inject custom CSS styles
        getStylesheets().add(getInlineStyleSheet());
        
        // Configure semi-transparent background overlay
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        
        // Configure the main modal window style
        modal.setMaxSize(800, 600);
        modal.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 4);");
        
        // Assemble Layout
        modal.setTop(createStatusPanel());
        
        contentArea.setPadding(new Insets(32));
        contentArea.setAlignment(Pos.TOP_CENTER);
        modal.setCenter(contentArea);
        
        modal.setBottom(createBottomBar());
        
        getChildren().add(modal);
        
        // Initialize Step Content Builders
        this.locationStepContainer = createLocationStep();
        this.travellerStepContainer = createTravellerStep();
        this.dateStepContainer = createDateStep();
        
        setupSharedListeners();
        showStep(1);
    }
    
    /**
     * Sets up event listeners for shared UI components such as the cabin selection,
     * airport list selection, and the search filter field.
     */
    private void setupSharedListeners() {
        cabinBox.getItems().setAll("Economy", "Business", "First Class");
        cabinBox.setValue(selectedCabin);
        cabinBox.setOnAction(e -> selectedCabin = cabinBox.getValue());
        
        airportList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isProgrammaticSelection || newVal == null) return;
            
            if (isDepartureStep) {
                selectedFrom = newVal;
                // Reset destination if it matches the new source
                if (selectedTo != null && selectedTo.id() == selectedFrom.id()) {
                    selectedTo = null; 
                }
            } else { 
                selectedTo = newVal;
            }
            validateCurrentStep();
        });
        
        // Filter airport list based on text input
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            isProgrammaticSelection = true;
            List<Airport> allAirports = controller.getAllAirports();
            if (newVal == null || newVal.isEmpty()) {
                airportList.getItems().setAll(allAirports);
            } else {
                List<Airport> filtered = allAirports.stream()
                    .filter(a -> a.name().toLowerCase().contains(newVal.toLowerCase()) || 
                                 a.location().toLowerCase().contains(newVal.toLowerCase()))
                    .collect(Collectors.toList());
                airportList.getItems().setAll(filtered);
            }
            Airport target = isDepartureStep ? selectedFrom : selectedTo;
            if (target != null) airportList.getSelectionModel().select(target);
            isProgrammaticSelection = false;
        });
    }

    /**
     * Creates the top status panel displaying the progress of the booking steps.
     *
     * @return The HBox containing the step indicators.
     */
    private HBox createStatusPanel() {
        HBox box = new HBox(0); 
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24, 32, 24, 32));
        box.setStyle("-fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
        
        box.getChildren().addAll(
            createStepLabel(step1Label, 1), createSeparator(),
            createStepLabel(step2Label, 2), createSeparator(),
            createStepLabel(step3Label, 3), createSeparator(),
            createStepLabel(step4Label, 4)
        );
        return box;
    }

    /**
     * Creates a clickable label for a specific step.
     *
     * @param label   The label component to configure.
     * @param stepNum The integer index of the step.
     * @return The configured Label.
     */
    private Label createStepLabel(Label label, int stepNum) {
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        label.setPadding(new Insets(8, 12, 8, 12));
        label.setCursor(Cursor.HAND);
        label.setStyle("-fx-background-radius: 20px;");
        label.setOnMouseClicked(e -> {
            // Allow navigating backwards only
            if (stepNum < currentStep) showStep(stepNum);
        });
        return label;
    }

    private Region createSeparator() {
        Region r = new Region();
        r.setMaxHeight(1);
        r.setStyle("-fx-background-color: " + BORDER_COLOR + ";");
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /**
     * Creates the bottom navigation bar containing the Next and Cancel buttons.
     *
     * @return The HBox containing navigation buttons.
     */
    private HBox createBottomBar() {
        HBox box = new HBox(16);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(24, 32, 24, 32));
        box.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 0 0 16px 16px;");
        
        closeButton.setOnAction(e -> onCloseAction.run());
        styleButton(closeButton, false);
        
        nextButton.setOnAction(e -> handleNext());
        styleButton(nextButton, true);
        
        box.getChildren().addAll(closeButton, nextButton);
        return box;
    }

    /**
     * Applies styling to standard buttons.
     *
     * @param b       The button to style.
     * @param primary True if this is the primary action button, false otherwise.
     */
    private void styleButton(Button b, boolean primary) {
        b.setPrefWidth(120);
        b.setPrefHeight(40);
        b.setCursor(Cursor.HAND);
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        b.getStyleClass().removeAll("primary-button", "secondary-button");
        b.getStyleClass().add(primary ? "primary-button" : "secondary-button");
    }

    // --- Step Logic ---

    /**
     * Switches the view to the specified step.
     *
     * @param step The step number (1 to 4).
     */
    private void showStep(int step) {
        currentStep = step;
        contentArea.getChildren().clear();
        updateStatusLabels();
        
        switch (step) {
            case 1:
                isDepartureStep = true;
                contentArea.getChildren().add(locationStepContainer);
                ((Label)locationStepContainer.getChildren().get(0)).setText("Where are you flying from?");
                prepareAirportList(selectedFrom);
                break;
            case 2:
                isDepartureStep = false;
                contentArea.getChildren().add(locationStepContainer);
                ((Label)locationStepContainer.getChildren().get(0)).setText("Where are you flying to?");
                prepareAirportList(selectedTo);
                break;
            case 3:
                contentArea.getChildren().add(travellerStepContainer);
                break;
            case 4:
                contentArea.getChildren().add(dateStepContainer);
                if (selectedFrom == null || selectedTo == null) {
                    AlertUtils.errorBox("Please select 'From' and 'To' locations first.", "Error");
                    showStep(1); 
                    return;
                }
                this.availableDatesInMonth = controller.getAvailableDatesForRoute(selectedFrom, selectedTo, currentMonth);
                populateCalendarGrid();
                break;
        }
        validateCurrentStep();
    }
    
    /**
     * Refreshes the airport list and handles selection state during step transitions.
     *
     * @param selected The airport that should be currently selected, if any.
     */
    private void prepareAirportList(Airport selected) {
        searchField.clear();
        isProgrammaticSelection = true;
        airportList.getItems().setAll(controller.getAllAirports());
        if (selected != null) airportList.getSelectionModel().select(selected);
        isProgrammaticSelection = false;
    }
    
    /**
     * Validates input for the current step and enables/disables the Next button.
     */
    private void validateCurrentStep() {
        boolean valid = false;
        switch (currentStep) {
            case 1: valid = selectedFrom != null; break;
            case 2: 
                valid = selectedTo != null;
                // Prevent selecting the same airport for departure and arrival
                if (selectedFrom != null && selectedTo != null && selectedFrom.id() == selectedTo.id()) valid = false;
                break;
            case 3: valid = true; break; 
            case 4: valid = selectedDate != null; break;
        }
        nextButton.setDisable(!valid);
    }

    /**
     * Updates the text and styling of the status labels based on the current step.
     */
    private void updateStatusLabels() {
        updateSingleLabel(step1Label, 1, selectedFrom != null ? selectedFrom.location() : "Flying From");
        updateSingleLabel(step2Label, 2, selectedTo != null ? selectedTo.location() : "Flying To");
        updateSingleLabel(step3Label, 3, guestCount + " Guest(s), " + selectedCabin);
        updateSingleLabel(step4Label, 4, selectedDate != null ? selectedDate.toString() : "Date");
    }

    private void updateSingleLabel(Label label, int stepNum, String text) {
        label.setText(text);
        // Reset basic style (keep padding/radius)
        String baseStyle = "-fx-background-radius: 20px; -fx-padding: 8 16 8 16;";
        
        if (stepNum == currentStep) {
            label.setTextFill(Color.web(PRIMARY_COLOR)); 
            label.setStyle(baseStyle + "-fx-background-color: #E0F7FA;"); 
        } else if (stepNum < currentStep) {
            label.setTextFill(Color.web(TEXT_HEADER)); 
            label.setStyle(baseStyle + "-fx-background-color: transparent;");
        } else {
            label.setTextFill(Color.web("#CBD5E1")); 
            label.setStyle(baseStyle + "-fx-background-color: transparent;");
        }
    }

    // --- UI Factory Methods ---

    /**
     * Creates the UI layout for the location selection steps (Step 1 and 2).
     *
     * @return The VBox container for the location step.
     */
    private VBox createLocationStep() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label();
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(TEXT_HEADER));
        
        searchField.setPromptText("Search countries or airports...");
        searchField.setPrefHeight(45);
        searchField.getStyleClass().add("modern-text-field");
        
        airportList.setPrefHeight(300);
        
        // Configure Custom Cell Factory for the Airport list
        airportList.setCellFactory(lv -> new ListCell<Airport>() {
            @Override
            protected void updateItem(Airport item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    // Reset styling for empty cells
                    setStyle("-fx-background-color: white; -fx-border-color: white;"); 
                } else {
                    // Clear inline styles so CSS can work for selection state
                    setStyle(null); 
                    
                    // Layout: [ CODE_ICON ]  [ Location \n Name ]
                    HBox root = new HBox(12);
                    root.setAlignment(Pos.CENTER_LEFT);
                    
                    // 1. The Code Icon (e.g., LHR)
                    Label codeLabel = new Label(item.name());
                    codeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    codeLabel.setTextFill(Color.WHITE);
                    codeLabel.setAlignment(Pos.CENTER);
                    
                    StackPane codeIcon = new StackPane(codeLabel);
                    codeIcon.setPrefSize(50, 28);
                    codeIcon.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-background-radius: 6px;");
                    
                    // 2. The Text Details
                    VBox textContainer = new VBox(2);
                    textContainer.setAlignment(Pos.CENTER_LEFT);
                    
                    Label locationLabel = new Label(item.location());
                    locationLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    locationLabel.setTextFill(Color.web(TEXT_HEADER));
                    
                    Label nameLabel = new Label(item.name());
                    nameLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                    nameLabel.setTextFill(Color.web(TEXT_BODY));
                    
                    textContainer.getChildren().addAll(locationLabel, nameLabel);
                    
                    root.getChildren().addAll(codeIcon, textContainer);
                    setGraphic(root);
                    setText(null);
                }
            }
        });
        
        container.getChildren().addAll(titleLabel, searchField, airportList);
        return container;
    }

    /**
     * Creates the UI layout for the traveler configuration step (Step 3).
     *
     * @return The VBox container for the traveler step.
     */
    private VBox createTravellerStep() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Who is travelling?");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(TEXT_HEADER));
        
        HBox counterBox = new HBox(16);
        counterBox.setAlignment(Pos.CENTER);
        
        Button minusBtn = new Button("-");
        minusBtn.setOnAction(e -> { if (guestCount > 1) { guestCount--; updateGuestLabel(); } });
        
        Button plusBtn = new Button("+");
        plusBtn.setOnAction(e -> { guestCount++; updateGuestLabel(); });
        
        minusBtn.getStyleClass().add("circle-button");
        plusBtn.getStyleClass().add("circle-button");
        
        updateGuestLabel();
        guestCountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        guestCountLabel.setTextFill(Color.web(TEXT_HEADER));
        
        counterBox.getChildren().addAll(new Label("Guests: "), minusBtn, guestCountLabel, plusBtn);
        
        VBox cabinBoxContainer = new VBox(8);
        Label cabinLbl = new Label("Cabin Class");
        cabinLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        cabinLbl.setTextFill(Color.web(TEXT_BODY));
        cabinBoxContainer.getChildren().addAll(cabinLbl, cabinBox);
        
        VBox promoBox = new VBox(8);
        Label promoLbl = new Label("Reward / Promo Code");
        promoLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        promoLbl.setTextFill(Color.web(TEXT_BODY));
        
        promoCode.setPromptText("Enter code");
        promoCode.setPrefHeight(40);
        promoCode.getStyleClass().add("modern-text-field");
        promoBox.getChildren().addAll(promoLbl, promoCode);
        
        container.getChildren().addAll(title, counterBox, new Separator(), cabinBoxContainer, new Separator(), promoBox);
        return container;
    }

    private void updateGuestLabel() {
        guestCountLabel.setText(guestCount + (guestCount == 1 ? " Guest" : " Guests"));
    }

    /**
     * Creates the UI layout for the date selection step (Step 4).
     *
     * @return The VBox container for the date step.
     */
    private VBox createDateStep() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("When are you travelling?");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web(TEXT_HEADER));

        // Month Navigation
        HBox nav = new HBox(10);
        nav.setAlignment(Pos.CENTER);
        
        Button prev = new Button("❮");
        prev.getStyleClass().add("icon-button");
        prev.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendarData();
        });
        
        Button next = new Button("❯");
        next.getStyleClass().add("icon-button");
        next.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendarData();
        });
        
        monthLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        monthLabel.setTextFill(Color.web(PRIMARY_COLOR));
        
        Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        nav.getChildren().addAll(prev, spacer1, monthLabel, spacer2, next);
        
        // Headers
        GridPane headerGrid = new GridPane();
        headerGrid.setHgap(8);
        headerGrid.setAlignment(Pos.CENTER);
        
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            Label day = new Label(days[i]);
            day.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            day.setTextFill(Color.web("#94A3B8"));
            day.setPrefWidth(60);
            day.setAlignment(Pos.CENTER);
            headerGrid.add(day, i, 0);
        }

        calendarGrid.setHgap(8);
        calendarGrid.setVgap(8);
        calendarGrid.setAlignment(Pos.CENTER);
        
        container.getChildren().addAll(title, nav, headerGrid, calendarGrid);
        return container;
    }
    
    /**
     * Reloads the available flight dates for the currently selected month and route.
     */
    private void refreshCalendarData() {
        this.availableDatesInMonth = controller.getAvailableDatesForRoute(selectedFrom, selectedTo, currentMonth);
        populateCalendarGrid();
    }

    /**
     * Builds the calendar grid UI based on the current month and available flight dates.
     * Handles visual states for enabled, disabled, and selected dates.
     */
    private void populateCalendarGrid() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());
        
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); 
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= currentMonth.lengthOfMonth(); i++) {
            LocalDate date = currentMonth.atDay(i);
            int row = (i + dayOfWeek - 2) / 7;
            int col = (i + dayOfWeek - 2) % 7;

            Button dateCell = new Button(String.valueOf(i));
            dateCell.setPrefSize(60, 60);
            dateCell.setCursor(Cursor.HAND);
            dateCell.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            if (date.isBefore(today)) {
                dateCell.setDisable(true);
                dateCell.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1;");
            } else if (!availableDatesInMonth.contains(date)) {
                dateCell.setDisable(true);
                dateCell.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #CBD5E1; -fx-background-radius: 8px;");
            } else if (date.equals(selectedDate)) {
                dateCell.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,164,191,0.3), 5, 0, 0, 2);");
            } else {
                dateCell.setStyle("-fx-background-color: #E0F7FA; -fx-text-fill: #006064; -fx-background-radius: 8px;");
                dateCell.setOnMouseEntered(e -> dateCell.setStyle("-fx-background-color: #B2EBF2; -fx-text-fill: #006064; -fx-background-radius: 8px;"));
                dateCell.setOnMouseExited(e -> {
                      if(!date.equals(selectedDate)) dateCell.setStyle("-fx-background-color: #E0F7FA; -fx-text-fill: #006064; -fx-background-radius: 8px;");
                });
            }
            
            if (!dateCell.isDisabled()) {
                dateCell.setOnAction(e -> {
                    selectedDate = date;
                    validateCurrentStep();
                    populateCalendarGrid();
                });
            }
            
            calendarGrid.add(dateCell, col, row);
        }
    }

    // --- FINAL ACTION LOGIC ---

    /**
     * Handles the "Next" button click. 
     * Advances to the next step if not on the final step, otherwise initiates the search request.
     */
    private void handleNext() {
        if (currentStep < 4) {
            showStep(currentStep + 1);
        } else {
            FlightSearchRequest request = new FlightSearchRequest(
                selectedFrom,
                selectedTo,
                selectedDate,
                guestCount,
                selectedCabin
            );

            ServiceResult result = controller.performSearch(request);

            if (result.isSuccess()) {
                onCloseAction.run();
            } else {
                AlertUtils.errorBox(result.getGlobalError(), "Search Error");
            }
        }
    }
    
    // --- Modern CSS ---

    /**
     * Generates a Base64 encoded data URI string for the inline CSS.
     * This avoids the need for an external CSS file for this component.
     *
     * @return The data URI string containing the CSS.
     */
    private String getInlineStyleSheet() {
        String css = 
            // Buttons
            ".primary-button {" +
            "    -fx-background-color: " + PRIMARY_COLOR + ";" +
            "    -fx-text-fill: white;" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-font-size: 14px;" +
            "    -fx-background-radius: 20px;" +
            "    -fx-padding: 8 24;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".primary-button:hover {" +
            "    -fx-background-color: #008C9E;" +
            "}" +
            ".primary-button:disabled {" +
            "    -fx-background-color: #CBD5E1;" +
            "}" +
            ".secondary-button {" +
            "    -fx-background-color: transparent;" +
            "    -fx-text-fill: #555;" +
            "    -fx-border-color: #ccc;" +
            "    -fx-border-radius: 20px;" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-font-size: 14px;" +
            "    -fx-background-radius: 20px;" +
            "    -fx-padding: 8 24;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".secondary-button:hover {" +
            "    -fx-background-color: #f0f0f0;" +
            "    -fx-text-fill: #333;" +
            "}" +
            ".circle-button {" +
            "    -fx-background-color: white;" +
            "    -fx-border-color: #E2E8F0;" +
            "    -fx-border-radius: 50%;" +
            "    -fx-background-radius: 50%;" +
            "    -fx-min-width: 32px; -fx-min-height: 32px;" +
            "    -fx-font-weight: bold;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".circle-button:hover {" +
            "    -fx-border-color: " + PRIMARY_COLOR + ";" +
            "    -fx-text-fill: " + PRIMARY_COLOR + ";" +
            "}" +
            ".icon-button {" +
            "    -fx-background-color: transparent;" +
            "    -fx-font-size: 16px;" +
            "    -fx-text-fill: #64748B;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".icon-button:hover {" +
            "    -fx-text-fill: " + PRIMARY_COLOR + ";" +
            "}" +
            
            // Inputs
            ".modern-text-field {" +
            "    -fx-background-color: white;" +
            "    -fx-text-fill: " + TEXT_HEADER + ";" +
            "    -fx-prompt-text-fill: #94A3B8;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-radius: 8px;" +
            "    -fx-background-radius: 8px;" +
            "    -fx-padding: 12;" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-size: 14px;" +
            "}" +
            ".modern-text-field:focused {" +
            "    -fx-border-color: " + PRIMARY_COLOR + ";" +
            "    -fx-background-color: #F8FDFF;" +
            "}" +
            
            // List View
            ".list-view {" +
            "    -fx-background-color: white;" +
            "    -fx-border-color: " + BORDER_COLOR + ";" +
            "    -fx-border-radius: 8px;" +
            "    -fx-background-radius: 8px;" +
            "}" +
            ".list-cell {" +
            "    -fx-padding: 8 16;" +
            "    -fx-background-color: transparent;" + 
            "}" +
            // FIXED: Selection colors
            ".list-cell:filled:selected {" +
            "    -fx-background-color: #E0F7FA;" + 
            "    -fx-text-fill: #006064;" +
            "}" +
            ".list-cell:filled:hover {" +
            "    -fx-background-color: #FAFAFA;" +
            "}";

        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes());
    }
}