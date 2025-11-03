package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.DashboardStats;
import artifact.Backend.Repositories.Interfaces.*;

public class DashboardRepository implements IDashboardRepository {
    
    private final IAirportRepository airportRepo;
    private final IAircraftRepository aircraftRepo;
    private final ICrewRepository crewRepo;
    private final IFlightRepository flightRepo;
    private final IReservationRepository reservationRepo;
    private final ITicketRepository ticketRepo; // Added missing dependency

    // Constructor Injection: Pass the loaded repositories here
    public DashboardRepository(
            IAirportRepository airportRepo,
            IAircraftRepository aircraftRepo,
            ICrewRepository crewRepo,
            IFlightRepository flightRepo,
            IReservationRepository reservationRepo,
            ITicketRepository ticketRepo) { 
        this.airportRepo = airportRepo;
        this.aircraftRepo = aircraftRepo;
        this.crewRepo = crewRepo;
        this.flightRepo = flightRepo;
        this.reservationRepo = reservationRepo;
        this.ticketRepo = ticketRepo;
    }

    @Override
    public DashboardStats getDashboardStats() {
        return new DashboardStats(
            airportRepo.getAll().size(),
            aircraftRepo.getAll().size(),
            crewRepo.getAll().size(),
            flightRepo.getAll().size(),
            reservationRepo.getAll().size(),
            ticketRepo.getAll().size() 
        );
    }
}