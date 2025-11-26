package artifact.Backend.Services.Impl;

import artifact.Backend.Models.Crew;
import artifact.Backend.Models.DTO.CrewRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Interfaces.ICrewRepository;
import artifact.Backend.Services.Interfaces.ICrewService;

/**
 * Service implementation for managing Flight Crew.
 * Handles the validation and addition of new crew teams.
 */
public class CrewService implements ICrewService {

    private final ICrewRepository crewRepository;

    public CrewService(ICrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    /**
     * Adds a new Crew entity to the repository.
     *
     * @param request DTO containing the Crew name and Captain's name.
     * @return ServiceResult indicating success or validation errors.
     */
    @Override
    public ServiceResult addCrew(CrewRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validation: Ensure all fields are present
        if (request.crewName() == null || request.crewName().trim().isEmpty()) {
            result.addError("crewName", "Crew name is required*");
        }
        if (request.captainName() == null || request.captainName().trim().isEmpty()) {
            result.addError("captainName", "Captain name is required*");
        }

        // Return early if validation failed
        if (!result.isSuccess()) return result;

        // 2. Business Logic: Persist data (ID managed by auto-increment in Repo)
        crewRepository.add(new Crew(0, request.crewName(), request.captainName()));
        
        return result;
    }
}