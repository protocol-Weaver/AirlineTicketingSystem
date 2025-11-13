package artifact.Backend.Services.Interfaces;
import artifact.Backend.Models.DTO.AircraftRequest;
import artifact.Backend.Models.ServiceResult;

public interface IAircraftService {
    /**
     * Validates input and adds a new aircraft.
     * Returns a ServiceResult (Success/Failure data) instead of manipulating UI.
     */
    ServiceResult addAircraft(AircraftRequest request);
}