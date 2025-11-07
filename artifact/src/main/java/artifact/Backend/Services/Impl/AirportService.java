package artifact.Backend.Services.Impl;

import artifact.Backend.Models.Airport;
import artifact.Backend.Models.DTO.AirportRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;
import artifact.Backend.Services.Interfaces.IAirportService;

/**
 * Service implementation for managing Airports.
 * Handles validation and persistence of new Airport entities.
 */
public class AirportService implements IAirportService {

    private final IAirportRepository airportRepository;

    public AirportService(IAirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    /**
     * Adds a new airport to the system.
     *
     * @param request DTO containing airport name and location code.
     * @return ServiceResult indicating validation status or success.
     */
    @Override
    public ServiceResult addAirport(AirportRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validation: Ensure required fields are not empty
        if (request.name() == null || request.name().trim().isEmpty()) {
            result.addError("name", "Airport name is required*");
        }
        if (request.location() == null || request.location().trim().isEmpty()) {
            result.addError("location", "Airport location is required*");
        }

        // Return early if validation failed
        if (!result.isSuccess()) return result;

        // 2. Business Logic: Persist data (ID managed by Repo)
        airportRepository.add(new Airport(0, request.name(), request.location()));

        return result;
    }
}