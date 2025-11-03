package artifact.Backend.Repositories.Impl;

import artifact.Backend.Repositories.Interfaces.*;

/**
 * Central service locator and provider for all Data Access Objects (Repositories).
 * <p>
 * This class implements the <strong>Registry</strong> and <strong>Singleton</strong> patterns
 * to manage the lifecycle of repository instances. It ensures that data files are loaded
 * only once (Eager Loading) and provides a global access point to the data layer.
 * </p>
 * <p>
 * It also handles manual <strong>Dependency Injection</strong> for repositories that
 * require access to other repositories (e.g., DashboardRepository).
 * </p>
 */
public class RepositoryProvider {

    /**
     * Static instances of Concrete Repositories.
     * These are initialized immediately (Eager Loading) when the class is loaded to ensure
     * data is ready before any service attempts to access it.
     */
    private static final IUserRepository userRepository = new UserRepository();
    private static final IAirportRepository airportRepository = new AirportRepository();
    private static final IAircraftRepository aircraftRepository = new AircraftRepository();
    private static final ICrewRepository crewRepository = new CrewRepository();
    private static final IFlightRepository flightRepository = new FlightRepository();
    private static final IReservationRepository reservationRepository = new ReservationRepository();
    private static final ITicketRepository ticketRepository = new TicketRepository();
    private static final ISupportRepository supportRepository = new SupportRepository();

    /**
     * The Dashboard Repository aggregates data from multiple sources.
     * Here, we perform manual constructor injection to pass the required dependencies.
     */
    private static final IDashboardRepository dashboardRepository = new DashboardRepository(
            airportRepository,
            aircraftRepository,
            crewRepository,
            flightRepository,
            reservationRepository,
            ticketRepository
    );

    // --- Public Accessors ---

    /**
     * Retrieves the singleton instance of the User Repository.
     * @return The IUserRepository interface for user management.
     */
    public static IUserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Retrieves the singleton instance of the Airport Repository.
     * @return The IAirportRepository interface for airport data.
     */
    public static IAirportRepository getAirportRepository() {
        return airportRepository;
    }

    /**
     * Retrieves the singleton instance of the Aircraft Repository.
     * @return The IAircraftRepository interface for fleet management.
     */
    public static IAircraftRepository getAircraftRepository() {
        return aircraftRepository;
    }

    /**
     * Retrieves the singleton instance of the Crew Repository.
     * @return The ICrewRepository interface for crew management.
     */
    public static ICrewRepository getCrewRepository() {
        return crewRepository;
    }

    /**
     * Retrieves the singleton instance of the Flight Repository.
     * @return The IFlightRepository interface for flight scheduling.
     */
    public static IFlightRepository getFlightRepository() {
        return flightRepository;
    }

    /**
     * Retrieves the singleton instance of the Reservation Repository.
     * @return The IReservationRepository interface for booking management.
     */
    public static IReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    /**
     * Retrieves the singleton instance of the Ticket Repository.
     * @return The ITicketRepository interface for ticket generation.
     */
    public static ITicketRepository getTicketRepository() {
        return ticketRepository;
    }
    
    /**
     * Retrieves the singleton instance of the Dashboard Repository.
     * This repository provides read-only aggregated statistics.
     * @return The IDashboardRepository interface.
     */
    public static IDashboardRepository getDashboardRepository() {
        return dashboardRepository;
    }

    /**
     * Retrieves the singleton instance of the Support Repository.
     * @return The ISupportRepository interface for help desk tickets.
     */
    public static ISupportRepository getSupportRepository() {
        return supportRepository;
    }
}