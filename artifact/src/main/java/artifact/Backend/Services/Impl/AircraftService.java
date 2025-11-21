package artifact.Backend.Services.Impl;

import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.DTO.AircraftRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Interfaces.IAircraftRepository;
import artifact.Backend.Services.Interfaces.IAircraftService;

/**
 * Service implementation for managing Aircraft.
 * Handles validation and addition of new aircraft types to the fleet.
 */
public class AircraftService implements IAircraftService {

    private final IAircraftRepository aircraftRepository;

    public AircraftService(IAircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    /**
     * Adds a new aircraft to the repository after validating input.
     *
     * @param request DTO containing aircraft type and capacity string.
     * @return ServiceResult indicating success or containing parsing/validation errors.
     */
    @Override
    public ServiceResult addAircraft(AircraftRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validate Type presence
        if (request.type() == null || request.type().trim().isEmpty()) {
            result.addError("type", "Aircraft type is required*");
        }

        // 2. Validate Capacity (Must be a valid positive integer)
        int capacity = 0;
        if (request.capacity() == null || request.capacity().trim().isEmpty()) {
            result.addError("capacity", "Capacity is required*");
        } else {
            try {
                capacity = Integer.parseInt(request.capacity());
                if (capacity <= 0) {
                    result.addError("capacity", "Invalid capacity*");
                }
            } catch (NumberFormatException e) {
                result.addError("capacity", "Capacity must be a number*");
            }
        }

        // Return early if errors found to prevent invalid data insertion
        if (!result.isSuccess()) return result;

        // 3. Business Logic: Persist the new Aircraft
        aircraftRepository.add(new Aircraft(0, request.type(), capacity));
        
        return result;
    }
}