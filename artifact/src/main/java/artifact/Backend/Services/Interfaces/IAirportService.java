package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.DTO.AirportRequest;

public interface IAirportService {
    ServiceResult addAirport(AirportRequest request);
}