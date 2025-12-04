# Airline Ticketing System

This document provides a comprehensive overview of the classes and their functionalities within the Airline Ticketing System application.

## GUI Layer

The GUI layer is responsible for presenting the user interface and handling user interactions. It is built using JavaFX.

### `AircraftsView.java`

**Purpose:** This class represents the view for managing aircraft. It allows administrative users to add new aircraft and view a list of existing ones.

**Key Components & Methods:**

*   **UI Components:**
    *   `typeField`: A `TextField` for entering the aircraft type.
    *   `capacityField`: A `TextField` for entering the aircraft's seating capacity.
    *   `airCraftsTable`: A `TableView` to display the list of all aircraft.
    *   `addButton`: A `Button` to trigger the addition of a new aircraft.
*   **`AircraftsView()` (Constructor):**
    *   Initializes the UI components and sets up the layout of the view.
    *   Creates an instance of `AircraftsController` to handle the business logic.
    *   Sets up an event handler for the "Add" button.
    *   Initializes the `TableView` with data from the controller.
*   **`handleAddAction()`:**
    *   This private method is called when the "Add" button is clicked.
    *   It retrieves the data from the form fields, creates an `AircraftRequest` DTO, and sends it to the `AircraftsController`.
    *   It handles the `ServiceResult` returned by the controller, displaying either a success message or error messages for the corresponding fields.

### `AirportsView.java`

**Purpose:** This class provides the user interface for managing airports. It allows users to add new airports and view a list of existing ones in a table.

**Key Components & Methods:**

*   **UI Components:**
    *   `airPortNameField`: A `TextField` for the airport's name.
    *   `airPortLocationField`: A `TextField` for the airport's location.
    *   `airportsTable`: A `TableView` to display the list of airports.
    *   `addButton`: A `Button` to add a new airport.
*   **`AirportsView()` (Constructor):**
    *   Initializes all UI components and arranges them on the screen.
    *   Instantiates the `AirportsController` to manage the logic.
    *   Sets the action for the `addButton` to call `handleAddAction`.
    *   Calls the controller to initialize the `airportsTable`.
*   **`handleAddAction()`:**
    *   Gathers the input from the text fields.
    *   Calls the `AirportsController` to add the new airport.
    *   Displays success or error alerts based on the result from the controller.

### `BookingWizardView.java`

**Purpose:** This class implements a multi-step booking wizard for users to search for flights. It's a modal window that guides the user through selecting departure and arrival locations, the number of travelers, and the travel date.

**Key Components & Methods:**

*   **State Management:**
    *   `currentStep`: Tracks the current step of the wizard (1-4).
    *   `selectedFrom`, `selectedTo`: Stores the selected departure and arrival `Airport` objects.
    *   `selectedDate`: Stores the chosen `LocalDate`.
*   **`BookingWizardView(UserBookingHomeController, Runnable)` (Constructor):**
    *   Takes a controller and a callback for when the wizard is closed.
    *   Builds the main modal structure with a status panel, content area, and navigation buttons.
*   **`showStep(int step)`:**
    *   Dynamically switches the content of the wizard based on the current step. It shows the appropriate view for location selection, traveler details, or date selection.
*   **UI Creation Methods (`createLocationStep`, `createTravellerStep`, `createDateStep`):**
    *   These methods build the specific UI for each step of the wizard. For example, `createLocationStep` includes a searchable list of airports, and `createDateStep` displays a calendar view.
*   **`populateCalendarGrid()`:**
    *   Renders the calendar for the date selection step. It highlights available flight dates, disables past dates, and shows the currently selected date.
*   **`handleNext()`:**
    *   This method is called when the "Next" button is clicked. It either advances to the next step or, on the final step, it packages the user's selections into a `FlightSearchRequest` and sends it to the controller to perform the search.

### `CrewsView.java`

**Purpose:** This class is responsible for the user interface for managing flight crews. It provides a form to add new crews and a table to display existing crews.

**Key Components & Methods:**

*   **UI Components:**
    *   `crewNameField`: `TextField` for the crew's name.
    *   `captainNameField`: `TextField` for the captain's name.
    *   `crewTable`: A `TableView` to show the list of crews.
*   **`CrewsView()` (Constructor):**
    *   Sets up the view's layout and initializes the UI components.
    *   Creates a `CrewsController`.
    *   Initializes the `crewTable` and sets the action for the "Add" button.
*   **`handleAddAction()`:**
    *   Retrieves the crew and captain names from the input fields.
    *   Uses the `CrewsController` to add the new crew.
    *   Provides feedback to the user with success or error messages.

### `FlightResultCardView.java`

**Purpose:** This class is a reusable UI component that displays a single flight search result in a modern card format. It's designed to be data-focused, showing departure/arrival times, airport codes, and price.

**Key Components & Methods:**

*   **`FlightResultCardView(FlightSearchResult, Consumer<FlightSearchResult>)` (Constructor):**
    *   Takes a `FlightSearchResult` object containing all the necessary data for the flight, and a `Consumer` to handle the booking action when the "Book Flight" button is clicked.
    *   The constructor builds the card layout, which is divided into three main sections: flight route/times, a vertical separator, and a price/book action section.
*   **`createDataBlock(...)`:**
    *   A private helper method that creates a consistent vertical block of information (e.g., for departure and arrival) containing a header, time, airport code, and city.
*   **`createFlightGraphic()`:**
    *   A private helper method that creates a simple visual graphic of a plane and a line to represent the flight path between the departure and arrival data blocks.

### `FlightResultsView.java`

**Purpose:** This view displays a list of available flights that match the user's search criteria. It uses `FlightResultCardView` to show each individual flight.

**Key Components & Methods:**

*   **UI Components:**
    *   `resultsContainer`: A `VBox` that holds the `FlightResultCardView` instances.
*   **`FlightResultsView()` (Constructor):**
    *   Sets up the main layout, including a navigation bar and a scrollable area for the results.
    *   Calls `populateResults()` to fill the view with data.
*   **`populateResults()`:**
    *   Retrieves the list of `FlightSearchResult` objects from the `FlightResultsController`.
    *   If no results are found, it displays a "No flights found" message.
    *   Otherwise, it iterates through the results, creates a `FlightResultCardView` for each, and adds it to the `resultsContainer`.

### `FlightsView.java`

**Purpose:** This class provides the administrative interface for managing flights. It includes a form to add new flights and a table to display all existing flights.

**Key Components & Methods:**

*   **UI Components:**
    *   `depAirports`, `arrAirports`: `ComboBox`es for selecting departure and arrival airports.
    *   `airCraftsNames`, `crewNames`: `ComboBox`es for selecting aircraft and crew.
    *   `depTime`, `arrTime`: `DatePicker`s for selecting departure and arrival dates.
    *   `flightsTable`: A `TableView` to display the list of flights.
*   **`FlightsView()` (Constructor):**
    *   Initializes the form and table layout.
    *   Uses `FlightsController` to populate the `ComboBox`es with data.
    *   Sets up an event handler on the `airCraftsNames` `ComboBox` to update the available seats label.
*   **`handleAddAction()`:**
    *   Gathers all the flight details from the form.
    *   Calls the `FlightsController` to add the new flight.
    *   Displays success or error messages based on the outcome.

### `HomeView.java`

**Purpose:** This class represents the main dashboard for administrative users. It displays key statistics about the airline's operations.

**Key Components & Methods:**

*   **`HomeView()` (Constructor):**
    *   Sets up the layout of the home screen, including a sidebar.
    *   It fetches a `DashboardStats` object from the `HomeController`.
    *   It then populates a series of `Label`s with the statistics (e.g., airport count, flight count).
*   **`createStatBox(...)`:**
    *   A private helper method that creates a styled `AnchorPane` to visually represent a single statistic, including a title, the count, and an icon placeholder. This helps maintain a consistent look and feel across the dashboard.

### `Launcher.java`

**Purpose:** This is the main entry point for the application when running from a JAR file.

**Key Components & Methods:**

*   **`main(String[] args)`:**
    *   The standard Java main method. Its only role is to call the `main` method of the `Main` class, which is the actual JavaFX application entry point.

### `LoginView.java`

**Purpose:** This class provides the user interface for authentication. It allows users to log in with their email and password or using their Google account.

**Key Components & Methods:**

*   **UI Components:**
    *   `emailInput`, `passwordInput`: `TextField` and `PasswordField` for user credentials.
    *   `loginButton`: A `Button` to attempt a standard login.
    *   `googleButton`: A `Button` to initiate a Google OAuth login.
    *   `registerLink`: A `Hyperlink` to navigate to the registration view.
*   **`LoginView()` (Constructor):**
    *   Sets up the login form with a background image and logo.
*   **`handleLoginAction()`:**
    *   Gathers the email and password.
    *   Calls the `LoginController` to handle the authentication logic.
    *   Displays error messages if the login fails.
*   **Event Handlers:**
    *   The `googleButton`'s action is bound to the `controller.handleGoogleLogin()` method.
    *   The `registerLink`'s action is bound to the `controller.goRegister()` method.

### `Main.java`

**Purpose:** This is the primary entry point for the JavaFX application.

**Key Components & Methods:**

*   **`start(Stage primaryStage)`:**
    *   This method is called when the application is launched.
    *   It sets up the primary stage (the main window).
    *   It initializes the `NavigationService` which manages switching between different views.
    *   It starts the `SyncScheduler` to periodically synchronize data with the backend.
    *   Finally, it navigates to the `LOGIN` view.
*   **`stop()`:**
    *   This method is called when the application is closed. It ensures that the `SyncScheduler` is stopped gracefully.

### `MyBookingsView.java`

**Purpose:** This view allows users to see a list of their booked tickets and pay for any pending reservations.

**Key Components & Methods:**

*   **UI Components:**
    *   `ticketsTable`: A `TableView` that displays the user's tickets.
*   **`MyBookingsView()` (Constructor):**
    *   Sets up the view with a modern look and feel, including custom styling for the `TableView`.
    *   It calls `createActionColumn()` to add a column to the table with a "Pay Now" button for pending payments.
*   **`createActionColumn()`:**
    *   This method creates a `TableColumn` that contains a "Pay Now" button.
    *   The button is only visible for tickets with a "PENDING" payment status.
*   **`handlePayAction(Ticket ticket)`:**
    *   This method is called when the "Pay Now" button is clicked.
    *   It opens a `StripePaymentWindow` to handle the payment process.
    *   Upon successful payment, it calls the `MyBookingsController` to update the ticket's status.

### `PaymentView.java`

**Purpose:** This class displays the order summary and provides payment options for a selected flight.

**Key Components & Methods:**

*   **UI Components:**
    *   Labels to display flight details (`flightHeader`, `flightRoute`, `flightDate`, etc.).
    *   Labels for price breakdown (`baseFare`, `taxes`, `totalPrice`).
    *   `payWithStripe`: A button to initiate payment with Stripe.
    *   `payLater`: A button to reserve the flight and pay later.
*   **`PaymentView()` (Constructor):**
    *   Sets up the layout of the payment screen.
    *   It calls the `PaymentController` to initialize the view with the correct flight and price information.
    *   It binds the `payWithStripe` and `payLater` buttons to the corresponding methods in the controller.

### `RegisterView.java`

**Purpose:** This class provides the user interface for creating a new account.

**Key Components & Methods:**

*   **UI Components:**
    *   `nameInput`, `emailInput`, `passInput`: `TextField`s for the user's name, email, and password.
    *   `createButton`: A `Button` to create the account.
    *   `googleButton`: A `Button` to sign up using a Google account.
*   **`RegisterView()` (Constructor):**
    *   Sets up the registration form.
*   **`handleRegisterAction()`:**
    *   This method is called when the "Sign Up" button is clicked.
    *   It validates the user's input and calls the `RegisterController` to initiate the registration process.
    *   If the initial validation is successful, it calls `showOtpDialog()` to prompt the user for a verification code.
*   **`showOtpDialog(String email)`:**
    *   Displays a dialog box asking the user to enter the OTP sent to their email.
    *   It then calls the `RegisterController` to finalize the registration with the provided OTP.

### `ReservationsView.java`

**Purpose:** This class provides an administrative interface for managing customer reservations.

**Key Components & Methods:**

*   **UI Components:**
    *   A form with fields for customer name, phone, flight, seat number, reservation date, and price.
    *   `reservationsTable`: A `TableView` to display all reservations.
*   **`ReservationsView()` (Constructor):**
    *   Initializes the form and table.
    *   It uses the `ReservationsController` to populate the `flight` `ComboBox`.
*   **`handleAddAction()`:**
    *   Gathers the reservation details from the form.
    *   Calls the `ReservationsController` to create the new reservation.
    *   Displays feedback to the user.

### `SceneManager.java`

**Purpose:** This class is a utility for managing JavaFX scenes. It simplifies the process of loading FXML files and setting them on the primary stage.

**Key Components & Methods:**

*   **`SceneManager(Stage stage)` (Constructor):**
    *   Takes the primary `Stage` as a parameter.
*   **`loadScene(String fxmlFile, String title)`:**
    *   Loads the specified FXML file.
    *   Creates a new `Scene` and sets it on the stage.
    *   Sets the title of the window.
    *   Returns the controller associated with the FXML file.

### `SeatSelectionView.java`

**Purpose:** This view allows users to select their seats on a flight. It displays a visual map of the aircraft's seating layout.

**Key Components & Methods:**

*   **UI Components:**
    *   `seatMapContainer`: A `VBox` that holds the `GridPane` of seats.
    *   `continueButton`: A button to proceed to payment.
*   **`SeatSelectionView()` (Constructor):**
    *   Sets up the view, including a header with instructions and a legend for seat statuses (available, unavailable, selected).
    *   Calls `buildSeatMap()` to create the seat layout.
*   **`buildSeatMap()`:**
    *   Creates a `GridPane` of `ToggleButton`s, each representing a seat.
    *   It determines the cabin class for each row and disables seats that are not in the user's selected cabin or are already taken.
    *   An event handler is added to each seat to handle selection.
*   **`updateSeatStyle(...)`:**
    *   A helper method to style the seats based on their status (selected, taken, available).
*   **`checkCompletion()`:**
    *   Checks if the user has selected the correct number of seats and enables the `continueButton` accordingly.

### `SidebarView.java`

**Purpose:** This class is a reusable component that creates the main navigation sidebar for the administrative dashboard.

**Key Components & Methods:**

*   **`SidebarView(BaseController controller, View activeView)` (Constructor):**
    *   Takes a `BaseController` to handle navigation and a `View` enum to indicate the currently active view.
    *   It creates a series of navigation buttons using the `createNavButton()` helper method.
    *   It also displays the currently logged-in admin's name and email.
*   **`createNavButton(...)`:**
    *   A private factory method that creates a styled navigation button with an icon and text.
    *   It highlights the button if it corresponds to the `activeView`.
    *   It sets the button's action to call the appropriate navigation method in the controller (e.g., `controller::goHome`).

### `StaffDashboardView.java`

**Purpose:** This view provides a dashboard for airline staff to manage incoming customer support tickets.

**Key Components & Methods:**

*   **UI Components:**
    *   `table`: A `TableView` to display the list of support tickets.
*   **`StaffDashboardView()` (Constructor):**
    *   Sets up the view with a modern "SaaS" look and feel, including a custom-styled `TableView`.
    *   It creates a navigation bar with a "Log Out" button.
    *   It initializes the `TableView` with data from the `StaffSupportController`.
*   **`createNavbar()`:**
    *   A private helper method to create the top navigation bar.
*   **`getInlineStyleSheet()`:**
    *   A private helper method that provides inline CSS to style the `TableView` and other components without needing an external CSS file.

### `StripePaymentWindow.java`

**Purpose:** This class creates a modal window for processing Stripe payments.

**Key Components & Methods:**

*   **`open(double amount, Consumer<String> onPaymentSuccess)`:**
    *   This is the main public method of the class. It opens a new modal window for payment.
    *   It takes the `amount` to be charged and a `Consumer` callback function to be executed upon successful payment.
    *   The window contains fields for credit card details, which are pre-filled with test data for convenience.
*   **Event Handler for "Pay" Button:**
    *   When the "Pay" button is clicked, a background `Task` is created to call the `StripeService` to process the payment. This prevents the UI from freezing.
    *   If the payment is successful, the `onPaymentSuccess` callback is executed, and the window is closed.
    *   If the payment fails, an error message is displayed.

### `TicketConversationView.java`

**Purpose:** This class provides a chat-like interface for viewing and responding to support tickets.

**Key Components & Methods:**

*   **`TicketConversationView(SupportTicket ticket, BaseController controller, Runnable onUpdateCallback)` (Constructor):**
    *   Takes a `SupportTicket`, a `BaseController`, and an `onUpdateCallback` as parameters.
    *   It sets up the view with a header containing ticket information, a scrollable area for messages, and an input area for sending new messages.
*   **`createMessageBubble(SupportMessage msg)`:**
    *   A private helper method that creates a styled "bubble" for a single chat message.
    *   It styles the bubble differently depending on whether the message was sent by the current user or the other party.
*   **`handleSend()`:**
    *   This method is called when the "Send" button is clicked. It sends the new message to the `SupportService` and then refreshes the message display.
*   **`handleResolve()`:**
    *   This method is called by staff users to mark a ticket as resolved.

### `TicketsView.java`

**Purpose:** This class provides a read-only view for administrators to see all support tickets.

**Key Components & Methods:**

*   **UI Components:**
    *   `ticketsTable`: A `TableView` to display all tickets.
*   **`TicketsView()` (Constructor):**
    *   Sets up the view and initializes the `TableView` with data from the `TicketsController`.

### `UserBookingHomeView.java`

**Purpose:** This is the main home screen for logged-in users. It provides a simple interface for starting the flight booking process.

**Key Components & Methods:**

*   **`UserBookingHomeView()` (Constructor):**
    *   Sets up the view with a hero section containing a title and subtitle, and two large buttons for selecting "From" and "To" locations.
    *   Clicking either of these buttons opens the `BookingWizardView`.
*   **`createHeroButton(...)`:**
    *   A private helper method to create the styled "From" and "To" buttons.
*   **`openWizard()` and `closeWizard()`:**
    *   These methods handle the showing and hiding of the `BookingWizardView` as an overlay on top of the main view.

### `UserNavbarView.java`

**Purpose:** This is a reusable navigation bar component for the customer-facing views.

**Key Components & Methods:**

*   **`UserNavbarView(BaseController controller, View activeView)` (Constructor):**
    *   Similar to `SidebarView`, this constructor takes a controller and an active view.
    *   It creates navigation links for "Book Flight", "My Trips", and "Support".
    *   It also displays a "Hello, [User Name]" message and a "Sign Out" button.
*   **`createNavLink(...)`:**
    *   A private helper method to create a styled navigation link. It highlights the link corresponding to the active view.

### `UserSupportView.java`

**Purpose:** This view allows users to create new support tickets and view their existing tickets.

**Key Components & Methods:**

*   **UI Components:**
    *   A segmented control to switch between creating a new ticket and viewing ticket history.
    *   A form for creating a new ticket, with fields for subject and description.
    *   A `TableView` to display the user's support ticket history.
*   **`UserSupportView()` (Constructor):**
    *   Sets up the view with the segmented control and the two sub-views (form and history).
*   **`createSegmentedControl()`:**
    *   A private helper method to create the "New Request" / "My History" toggle switch.
*   **`handleCreateAction()`:**
    *   This method is called when the user submits a new support ticket. It calls the `UserSupportController` to create the ticket.
*   **`openTicketChat(SupportTicket ticket)`:**
    *   This method is called when the user clicks the "View Chat" button in the history table. It opens a `TicketConversationView` in a new modal window.

## Backend Layer

The Backend layer contains the business logic, data models, and services that power the application.

### `AlertUtils.java`

**Purpose:** A simple utility class for displaying `Alert` dialogs.

**Key Components & Methods:**

*   **`infoBox(String, String)`:** Displays an information alert.
*   **`errorBox(String, String)`:** Displays an error alert.

### `UserSession.java`

**Purpose:** A singleton class to manage the currently logged-in user's session.

**Key Components & Methods:**

*   **`getInstance()`:** Returns the singleton instance of the class.
*   **`startSession(User user)`:** Stores the `User` object when a user logs in.
*   **`clearSession()`:** Clears the user session on logout.
*   **`getCurrentUser()`:** Returns the currently logged-in `User`.
*   **`isSessionActive()`:** Checks if a user is currently logged in.

### `View.java`

**Purpose:** An enum that defines all the possible views (scenes) in the application.

**Key Components & Methods:**

*   This enum provides a type-safe way to refer to different views, avoiding the use of error-prone strings. It includes entries for all admin, staff, and customer-facing views.

### Config

#### `GsonProvider.java`

**Purpose:** This class provides a centralized `Gson` instance for JSON serialization and deserialization throughout the application.

**Key Components & Methods:**

*   **`getGson()`:** Returns a pre-configured `Gson` instance.
*   **Custom Type Adapters:**
    *   It includes custom `TypeAdapter`s for `LocalDate` and `LocalDateTime` to ensure consistent date and time formatting in JSON files. This is crucial for interoperability between Java's `java.time` objects and the string representations stored in JSON.

### Controller

The Controller layer acts as an intermediary between the `View` (GUI) and the `Service`/`Repository` layers. Its primary responsibility is to handle user input from the view, delegate business logic to the appropriate services, and return the results to the view.

#### `AircraftsController.java`

**Purpose:** This controller manages the logic for the `AircraftsView`.

**Key Components & Methods:**

*   **`initialize(TableView<Aircraft> airCraftsTable)`:**
    *   Sets up the columns for the aircraft table (`Aircraft ID`, `Aircraft Name`, `Capacity`).
    *   Binds the table to the `ObservableList` of aircraft from the `AircraftRepository`.
*   **`addAircraft(AircraftRequest request)`:**
    *   Delegates the request to add a new aircraft directly to the `AircraftService`.
    *   Returns the `ServiceResult` to the view for user feedback.

#### `AirportsController.java`

**Purpose:** This controller handles the logic for the `AirportsView`.

**Key Components & Methods:**

*   **`initialize(TableView<Airport> airportsTable)`:**
    *   Configures the table columns for displaying airport data.
    *   Populates the table with data from the `AirportRepository`.
*   **`addAirport(AirportRequest request)`:**
    *   Receives a DTO from the view and passes it to the `AirportService` for processing.
    *   Returns the result of the operation to the view.

#### `BaseController.java`

**Purpose:** An abstract base class that all other controllers extend. It centralizes common functionalities and dependencies.

**Key Components & Methods:**

*   **Dependencies:**
    *   Provides `NavigationService` and `UserSession` instances to all child controllers.
*   **Navigation Methods (`goHome`, `goLogin`, etc.):**
    *   Offers a suite of methods for navigating between different views. These methods are called by the UI (e.g., `SidebarView`, `UserNavbarView`) to trigger scene changes, abstracting the navigation logic away from the individual controllers.

#### `CrewsController.java`

**Purpose:** Manages the business logic for the `CrewsView`.

**Key Components & Methods:**

*   **`initialize(TableView<Crew> crewTable)`:**
    *   Sets up the `TableView` columns for displaying crew information.
    *   Loads the crew data from the `CrewRepository`.
*   **`addCrew(CrewRequest request)`:**
    *   Forwards the request to add a new crew to the `CrewService`.
    *   Returns the `ServiceResult` from the service to the view.

#### `FlightResultsController.java`

**Purpose:** This controller manages the state and actions for the flight results and seat selection process.

**Key Components & Methods:**

*   **`getSearchResults()`:**
    *   Retrieves the list of `FlightSearchResult` objects from the singleton `FlightSearchState`. This state is shared across the booking flow.
*   **`handleBookNow(FlightSearchResult selectedFlight)`:**
    *   This method is called when the user clicks "Book Flight" on a `FlightResultCardView`.
    *   It stores the selected flight in the `FlightSearchState` and navigates to the `SEAT_SELECTION` view.

#### `FlightsController.java`

**Purpose:** This controller is responsible for the logic of the `FlightsView`, including populating dropdowns and handling the creation of new flights.

**Key Components & Methods:**

*   **`initialize(...)`:**
    *   Populates the `ComboBox`es for airports, aircraft, and crews with data from their respective repositories.
    *   Sets up custom `StringConverter`s for the `ComboBox`es to display user-friendly text.
    *   Configures and populates the main `TableView` for flights.
*   **`addFlight(FlightRequest request)`:**
    *   Delegates the creation of a new flight to the `FlightService`.
*   **`onAircraftSelected(...)`:**
    *   An event handler that updates the "Available Seats" `Label` in the view when an aircraft is selected from the `ComboBox`.

#### `FlightSearchState.java`

**Purpose:** A singleton class that acts as a temporary data store for the entire flight booking process. This allows data to be passed between different views (`UserBookingHomeView`, `FlightResultsView`, `SeatSelectionView`, `PaymentView`) without tight coupling.

**Key Components & Methods:**

*   **`getInstance()`:** Returns the singleton instance.
*   **Data Storage:**
    *   It stores the `searchResults`, the `selectedFlight`, the `selectedCabin`, the `guestCount`, and a `Set` of `selectedSeats`.
*   **`clearState()`:**
    *   Resets all the stored data, typically after a booking is completed or cancelled.

#### `HomeController.java`

**Purpose:** The controller for the main administrative dashboard (`HomeView`).

**Key Components & Methods:**

*   **`loadStats()`:**
    *   Calls the `DashboardService` to get the latest `DashboardStats`.
    *   Returns the `DashboardStats` DTO to the `HomeView`, which is then responsible for updating its own UI labels.

#### `LoginController.java`

**Purpose:** Manages the user authentication logic for the `LoginView`.

**Key Components & Methods:**

*   **`handleLogin(LoginRequest request)`:**
    *   Takes a `LoginRequest` DTO from the view.
    *   Calls the `AuthService` to perform the login.
    *   If the login is successful, it calls `navigateBasedOnRole()` to direct the user to the correct dashboard.
*   **`handleGoogleLogin()`:**
    *   Initiates the Google OAuth2 flow by starting a local callback server and opening the user's web browser to the Google authentication URL.
*   **`finishGoogleLogin(String authCode)`:**
    *   This method is called by the local server's callback handler.
    *   It exchanges the authorization code for a Google user profile and then logs the user into the application using their Google credentials.
*   **`navigateBasedOnRole(User user)`:**
    *   A private helper method that navigates the user to the appropriate view (`HOME` for admins, `STAFF_DASHBOARD` for staff, `USER_BOOKING_HOME` for customers) based on their `UserRole`.

#### `MyBookingsController.java`

**Purpose:** This controller handles the logic for the `MyBookingsView`.

**Key Components & Methods:**

*   **`initialize(...)`:**
    *   Sets up the `TableView` to display the current user's tickets.
    *   It retrieves the tickets by calling `ticketService.getMyTickets(currentUser)`.
*   **`getReservationForTicket(Ticket ticket)`:**
    *   Retrieves the full `Reservation` object associated with a given `Ticket`. This is needed to get the price for payment.
*   **`processPayment(Ticket ticket, Reservation reservation)`:**
    *   This method is called after a successful Stripe payment.
    *   It delegates to the `TicketService` to update the payment status of the ticket and reservation.

#### `NavigationService.java`

**Purpose:** A singleton service that manages all scene transitions in the application.

**Key Components & Methods:**

*   **`getInstance()`:** Returns the singleton instance.
*   **`setPrimaryStage(Stage primaryStage)`:**
    *   Stores a reference to the main application `Stage`.
*   **`navigateTo(View view)`:**
    *   The core method for changing scenes. It takes a `View` enum constant, creates the corresponding `Scene` object, sets the appropriate title, and displays the new scene on the primary stage.
*   **`createScene(View view)`:**
    *   A private factory method that constructs and returns a new `Scene` based on the requested `View`. It also sets the dimensions of the scene.

#### `PaymentController.java`

**Purpose:** This controller manages the logic for the `PaymentView`, including calculating prices and handling payment actions.

**Key Components & Methods:**

*   **`initialize(...)`:**
    *   Populates the order summary in the `PaymentView` with data from the `FlightSearchState`.
    *   Calculates the total price based on the base fare, taxes, cabin class multiplier, and number of guests.
*   **`handlePayNow()`:**
    *   Opens the `StripePaymentWindow` to process a credit card payment. On success, it calls `performBooking()`.
*   **`handlePayLater()`:**
    *   Shows a confirmation dialog and, if confirmed, calls `performBooking()` with a `PENDING` status.
*   **`performBooking(BookingStatus status)`:**
    *   The core booking logic. It iterates through the `Set` of selected seats and creates a separate `BookingRequest` for each seat.
    *   It calls the `BookingService` for each request. If all bookings are successful, it clears the `FlightSearchState` and navigates to the `MY_BOOKINGS` view.

#### `RegisterController.java`

**Purpose:** Handles the logic for user registration in the `RegisterView`.

**Key Components & Methods:**

*   **`initiateRegister(RegisterRequest request)`:**
    *   Called when the user first submits the registration form. It delegates to the `AuthService` to validate the input and send a verification OTP.
*   **`finalizeRegister(String email, String otp)`:**
    *   Called after the user enters the OTP. It calls the `AuthService` to verify the OTP and create the user account. On success, it navigates to the login view.
*   **`handleGoogleSignup()` and `finishGoogleSignup(String authCode)`:**
    *   These methods manage the Google OAuth2 signup flow, similar to the `LoginController`.

#### `ReservationsController.java`

**Purpose:** Manages the logic for the administrative `ReservationsView`.

**Key Components & Methods:**

*   **`initialize(...)`:**
    *   Populates the `flight` `ComboBox` with data.
    *   Sets up the columns for the `reservationsTable` and loads the data from the `ReservationRepository`.
*   **`addReservation(ReservationRequest request)`:**
    *   Takes a `ReservationRequest` DTO from the view.
    *   Unpacks the DTO and calls the `ReservationService` to create the new reservation.

#### `SeatSelectionController.java`

**Purpose:** This controller handles the logic for the `SeatSelectionView`.

**Key Components & Methods:**

*   **`toggleSeatSelection(String seatId)`:**
    *   Manages the selection and deselection of seats. It ensures that the user cannot select more seats than the number of guests specified.
*   **`isSelectionComplete()`:**
    *   Checks if the number of selected seats matches the number of guests.
*   **`handleContinue()`:**
    *   Called when the "Continue to Payment" button is clicked. It validates that the seat selection is complete and then navigates to the `PAYMENT` view.
*   **`getTakenSeatsForCurrentFlight()`:**
    *   Retrieves the set of already booked seats for the selected flight so they can be disabled in the view.

#### `StaffSupportController.java`

**Purpose:** The controller for the `StaffDashboardView`, which allows staff members to manage support tickets.

**Key Components & Methods:**

*   **`initialize(TableView<SupportTicket> table)`:**
    *   Sets up the `TableView` to display all support tickets.
    *   It adds an "Action" column with an "Open Chat" button for each ticket.
*   **`handleOpenChat(SupportTicket ticket, TableView<SupportTicket> table)`:**
    *   This method is called when a staff member clicks "Open Chat".
    *   It opens a `TicketConversationView` in a new modal window.
    *   It passes a callback function to the `TicketConversationView` so that the main table can be refreshed when the chat window is closed or the ticket is resolved.

#### `TicketsController.java`

**Purpose:** This controller manages the logic for the admin-facing `TicketsView`.

**Key Components & Methods:**

*   **`initialize(TableView<Ticket> ticketsTable)`:**
    *   Configures the `TableView` to display a list of all tickets in the system.
    *   It fetches the data from the `TicketService`.

#### `UserBookingHomeController.java`

**Purpose:** The controller for the main customer booking page, `UserBookingHomeView`.

**Key Components & Methods:**

*   **`getAllAirports()`:**
    *   Provides the list of all airports to the `BookingWizardView`.
*   **`getAvailableFlightDates(...)`:**
    *   Retrieves the set of available flight dates for a specific route and month, used to populate the calendar in the `BookingWizardView`.
*   **`performSearch(FlightSearchRequest request)`:**
    *   This is the primary action method, called by the `BookingWizardView`.
    *   It validates the search request, calls the `FlightService` to find flights, stores the results in the `FlightSearchState`, and then navigates to the `FLIGHT_RESULTS` view.

#### `UserSupportController.java`

**Purpose:** Manages the logic for the `UserSupportView`.

**Key Components & Methods:**

*   **`getMyTickets()`:**
    *   Retrieves the support tickets for the currently logged-in user.
*   **`createTicket(TicketCreateRequest request)`:**
    *   Takes a `TicketCreateRequest` DTO from the view and calls the `SupportService` to create a new support ticket.

### Models

The `Models` package contains the data structures used throughout the application. The primary models are implemented as Java `record` classes, which provide a concise way to create immutable data carriers.

*   **`Aircraft.java`**: Represents an aircraft with an `id`, `type`, and `capacity`.
*   **`Airport.java`**: Represents an airport with an `id`, `name`, and `location`.
*   **`Crew.java`**: Represents a flight crew with an `id`, `crewName`, and `captainName`.
*   **`DashboardStats.java`**: A DTO for holding various statistics for the admin dashboard.
*   **`Flight.java`**: Represents a flight with details like `departureAirportId`, `arrivalAirportId`, `departureTime`, `arrivalTime`, and `availableSeats`.
*   **`FlightSearchResult.java`**: A composite record that combines a `Flight` with its corresponding `Airport` and `Aircraft` objects, used for displaying search results.
*   **`GoogleUser.java`**: A simple record to hold user information (`name`, `email`) retrieved from the Google OAuth API.
*   **`Models.java`**: A class containing shared constants, such as a `DateTimeFormatter` for consistent date formatting.
*   **`Notification.java`**: Represents a notification to be sent, containing a `recipientEmail`, `subject`, `message`, and `NotificationType`.
*   **`Reservation.java`**: Represents a customer reservation, including flight details, customer information, price, and booking status.
*   **`ServiceResult.java`**: A generic class used by services to return the result of an operation. It indicates success or failure and can contain a global error message or a map of field-specific errors.
*   **`SupportMessage.java`**: Represents a single message within a support ticket, including the sender's name, role, the message content, and a timestamp.
*   **`SupportTicket.java`**: Represents a customer support ticket, containing the user's information, subject, status, creation date, and a list of `SupportMessage`s.
*   **`Ticket.java`**: Represents a customer's final ticket, linking to a reservation and including customer and flight information.
*   **`User.java`**: Represents a user of the system, with an `id`, `name`, `email`, `password`, and `UserRole`.

#### DTO (Data Transfer Objects)

DTOs are simple `record` classes used to transfer data between the `View` and `Controller` layers. They encapsulate the data from a form or request into a single object, making the code cleaner and more maintainable.

*   **`AircraftRequest.java`**: Carries the `type` and `capacity` for a new aircraft.
*   **`AirportRequest.java`**: Carries the `name` and `location` for a new airport.
*   **`BookingRequest.java`**: Contains all the information needed to book a single seat on a flight, including the `flightResult`, `customer`, `seatNumber`, `price`, and `status`.
*   **`CrewRequest.java`**: Holds the `crewName` and `captainName` for a new crew.
*   **`FlightRequest.java`**: Used for creating a new flight, containing all the necessary `Airport`, `Aircraft`, `Crew`, and date information.
*   **`FlightSearchRequest.java`**: Encapsulates the user's flight search criteria from the `BookingWizardView`.
*   **`LoginRequest.java`**: Carries the `email` and `password` for a login attempt.
*   **`RegisterRequest.java`**: Holds the `name`, `email`, and `password` for a new user registration.
*   **`ReservationRequest.java`**: Contains all the data from the admin reservation form.
*   **`TicketCreateRequest.java`**: Used for creating a new support ticket, carrying the `subject` and `description`.

### Notification

This package implements the Observer design pattern to handle notifications.

#### `NotificationFactory.java`

**Purpose:** A factory class for creating different types of `Notification` objects.

**Key Components & Methods:**

*   This class has a series of static methods (`createRegistration`, `createBookingConfirmed`, etc.) that construct and return `Notification` objects with pre-defined subjects and bodies. This encapsulates the content of the notifications and makes them easy to create from the service layer.

#### `NotificationManager.java`

**Purpose:** The central hub for managing and dispatching notifications. It follows the Singleton pattern.

**Key Components & Methods:**

*   **`getInstance()`:** Returns the singleton instance.
*   **`subscribe(INotificationObserver observer)`:** Allows different notification services (like `EmailNotificationService`) to register themselves as observers.
*   **`notifyAll(Notification notification)`:** When called, this method iterates through all subscribed observers and calls their `onNotify()` method, passing the `Notification` object. This decouples the sender of the notification from the receivers.

### Repositories

The Repository layer is responsible for all data access logic. It abstracts the data source (in this case, JSON files) from the rest of the application. The use of interfaces and a central `RepositoryProvider` allows for easy swapping of data sources in the future (e.g., to a database) without changing the service or controller layers.

#### Interfaces

*   **`IRepository<T>`**: A generic interface that defines the standard CRUD (Create, Read, Update, Delete) operations for a repository. It also includes `getAll()` to retrieve all items and `refreshFromCloud()` to force a data refresh from the source.
*   **`IAircraftRepository.java`**: Extends `IRepository<Aircraft>`.
*   **`IAirportRepository.java`**: Extends `IRepository<Airport>`.
*   **`ICrewRepository.java`**: Extends `IRepository<Crew>`.
*   **`IDashboardRepository.java`**: A specific interface for retrieving dashboard statistics.
*   **`IFlightRepository.java`**: Extends `IRepository<Flight>` and adds methods for decrementing a seat and finding flights by route and month.
*   **`IReservationRepository.java`**: Extends `IRepository<Reservation>` with methods to add a reservation and update its status.
*   **`ISupportRepository.java`**: Extends `IRepository<SupportTicket>` with methods for finding tickets by user, adding messages, and updating ticket status.
*   **`ITicketRepository.java`**: Extends `IRepository<Ticket>` with methods for finding tickets by customer name and updating ticket status.
*   **`IUserRepository.java`**: A specific interface for user-related data access, including finding a user by email and adding a new user.
