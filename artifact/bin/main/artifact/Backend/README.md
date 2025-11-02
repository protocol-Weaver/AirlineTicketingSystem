# Airline Management System

## Project Overview

This project is a desktop application for managing a small airline. It provides functionalities for both administrators and customers. Administrators can manage flights, aircraft, airports, and crews. Customers can search for flights, book tickets, and manage their bookings. The application is built using Java and JavaFX for the graphical user interface.

## Project Structure

The project is organized into two main packages: `Backend` and `GUI`.

### `Backend`

The `Backend` package contains the core business logic of the application. It is further divided into the following sub-packages:

*   **`Config`**: Contains configuration classes, such as `GsonProvider` for JSON serialization.
*   **`Controller`**: Contains the controller classes that handle user input from the GUI and interact with the services.
*   **`Models`**: Contains the data model classes (records) and Data Transfer Objects (DTOs).
*   **`Notification`**: Contains classes for sending notifications, such as emails.
*   **`Repositories`**: Contains the repository classes that handle data persistence. The application uses a JSON-based data store.
*   **`Services`**: Contains the service classes that implement the business logic of the application.
*   **`Supabase`**: Contains classes for synchronizing data with a Supabase backend.
*   **`Tags`**: Contains enum types used throughout the application.

### `GUI`

The `GUI` package contains the classes related to the graphical user interface. It contains the view classes that define the different screens of the application. The views are built programmatically using JavaFX.

## Backend Architecture

The backend follows a layered architecture, with a clear separation of concerns between the controllers, services, and repositories.

*   **Repository Pattern**: The application uses the repository pattern to abstract the data layer. The `BaseJsonRepository` class provides a generic implementation for storing data in JSON files. The `RepositoryProvider` class ensures that there is only one instance of each repository.
*   **Service Layer**: The service layer contains the core business logic. The services are responsible for validating user input, interacting with the repositories, and returning the results to the controllers. The `ServiceResult` class is used to return the outcome of an operation, including any validation errors.
*   **Data Persistence**: The application stores its data in JSON files in the `src/main/resources/data` directory. It also has the capability to synchronize data with a Supabase backend.

## GUI Architecture

The GUI is built using JavaFX. The views are created programmatically, without using FXML. Each view has a corresponding controller in the `Backend.Controller` package that handles user interactions. The `SceneManager` class is used to manage the different scenes of the application, and the `NavigationService` is used to navigate between them.

## How to Run the App

### Prerequisites

*   Java 21
*   Gradle

### Building and Running

1.  **Build the project:**

    ```bash
    gradlew build
    ```

2.  **Run the application:**

    ```bash
    gradlew run
    ```

    This will start the application and open the login window.

3.  **Create a distributable package:**

    ```bash
    gradlew jpackage
    ```

    This will create an MSI installer in the `build/jpackage` directory.

