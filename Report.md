# Airline Ticketing System

## 1. Introduction

This report provides a high-level analysis of the `AirlineTicketingSystem` Java application. The focus is on its software architecture, prevalent design patterns, and a qualitative assessment of its object-oriented design using Chidamber & Kemerer (CK) metrics. The analysis is based on the project's source code structure and component organization, providing a logical overview rather than a deep-dive into implementation specifics.

---

## 2. Architectural Overview

The application follows a **classic Layered Architecture**, which is a proven design for creating a clear separation of concerns. This structure makes the system easier to develop, test, and maintain. The primary layers identified are:

-   **Models:** Core domain objects that represent the data and business entities (e.g., `Flight`, `User`, `Reservation`).
-   **Repositories:** A data access layer responsible for abstracting the persistence mechanism. This layer handles all interactions with the database.
-   **Services:** The business logic layer that orchestrates operations by using repositories and other services. It encapsulates the application's primary functionality.
-   **Controllers:** The presentation-facing layer that handles incoming requests (likely from the GUI) and delegates them to the appropriate services.

A key architectural strength is the adherence to the **Dependency Inversion Principle**. The project heavily utilizes interfaces for its services and repositories (`Interfaces` and `Impl` sub-packages). This means that higher-level components (like services) depend on abstractions (interfaces) rather than concrete implementations, which significantly reduces coupling and improves modularity.

Furthermore, the presence of a `Supabase` package suggests the use of a **Backend-as-a-Service (BaaS)** model, where services like database and authentication are offloaded to a third-party provider, accelerating development.

---

## 3. Design Pattern Analysis

Several well-known design patterns have been identified, contributing to the system's robustness and scalability.

### Repository Pattern
This pattern is clearly implemented to decouple the business logic from the data access layer.
-   **How it's used:** The `Repositories/Interfaces` directory defines contracts like `IRepository` and `IUserRepository`. The corresponding `Impl` directory provides the concrete logic for data operations, likely communicating with Supabase. This abstracts away the specifics of the database, allowing it to be swapped with minimal impact on the business logic.

### Singleton Pattern
This pattern ensures that a class has only one instance and provides a global point of access to it.
-   **How it's used:** The `UserSession.java` class is a prime candidate for a Singleton. It likely holds the state of the currently logged-in user, and making it a Singleton ensures that all parts of the application access the same session instance.

### Observer Pattern
This pattern establishes a one-to-many dependency between objects, where a change in one object (the subject) notifies all its dependents (the observers) automatically.
-   **How it's used:** The `INotificationObserver` interface strongly indicates the use of this pattern. It is likely used to create a notification system where different parts of the application can subscribe to events (e.g., a successful booking) and react accordingly without being tightly coupled to the event source.

### Facade Pattern
This pattern provides a simplified, unified interface to a more complex set of subsystems.
-   **How it's used:** The `BookingService` likely acts as a Facade. The process of booking a flight is complex, involving reservations, ticketing, and payments. The `BookingService` simplifies this for the client (the controller) by coordinating calls to `ReservationService`, `TicketService`, and the external `StripeService`.

### Strategy Pattern
This pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable.
-   **How it's used:** The `IOAuthService` interface, with concrete implementations like `GoogleOAuthService`, is a classic example. It allows the application to support different authentication strategies (e.g., Google, Facebook) without changing the client code that uses the service. The client simply works with the `IOAuthService` interface.

---

## 4. Qualitative CK Metrics Analysis

CK metrics help evaluate the complexity, coupling, and cohesion of an object-oriented design. Here is a qualitative assessment based on the architectural patterns observed.

-   **WMC (Weighted Methods per Class):**
    -   **Models** are expected to have a very low WMC, as they are primarily data holders with simple getters and setters.
    -   **Service** and **Controller** classes, especially orchestrators like `BookingService`, will likely have a higher WMC, indicating greater complexity, which is natural for their role.

-   **DIT (Depth of Inheritance Tree) & NOC (Number of Children):**
    -   The design appears to **favor composition over inheritance**, which is a modern best practice. This results in a low DIT and NOC across the system. This makes the design more flexible than a deep, rigid inheritance hierarchy.

-   **CBO (Coupling Between Objects):**
    -   Coupling is **well-managed** due to the extensive use of interfaces (Dependency Inversion). Classes are coupled to abstractions, not to other concrete classes. While the `Services` layer is naturally coupled to the `Repositories` layer, this coupling is loose and managed through contracts (interfaces).

-   **RFC (Response for a Class):**
    -   Facade-like classes such as `BookingService` will have a high RFC by design, as they invoke methods on many other objects (`ReservationRepository`, `StripeService`, etc.). This is acceptable as their primary role is coordination.
    -   Other classes, like models or specific repositories, should have a low RFC.

-   **LCOM (Lack of Cohesion in Methods):**
    -   Cohesion is expected to be **high** (meaning LCOM is low). The package and class naming conventions suggest that each class has a single, well-defined responsibility (e.g., `AircraftService` deals only with aircraft). This is a hallmark of a clean, maintainable design.

---

## 5. Conclusion

The `AirlineTicketingSystem` demonstrates a mature and robust software architecture. The deliberate use of a layered design, dependency inversion, and standard design patterns (Repository, Observer, Facade, etc.) results in a system that is:

-   **Maintainable:** Separation of concerns makes it easy to locate and modify code.
-   **Testable:** Loose coupling and interfaces allow for effective unit testing.
-   **Scalable:** The modular design allows for new features or strategies (like a new payment provider) to be added with minimal disruption.

The qualitative CK metrics analysis further supports this conclusion, indicating a design that effectively manages complexity, coupling, and cohesion.