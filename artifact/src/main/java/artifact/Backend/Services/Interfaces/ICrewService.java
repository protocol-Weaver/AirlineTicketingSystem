package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.DTO.CrewRequest;
import artifact.Backend.Models.ServiceResult;

public interface ICrewService {
    /**
     * Validates and adds a new crew member.
     * Returns a ServiceResult containing success status or validation errors.
     */
    ServiceResult addCrew(CrewRequest request);
}