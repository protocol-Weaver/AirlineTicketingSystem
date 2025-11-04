package artifact.Backend.Repositories.Interfaces;
import artifact.Backend.Models.Flight;
import java.time.YearMonth;
import java.util.List;

public interface IFlightRepository extends IRepository<Flight> {
    void decrementSeat(long flightId);
    public List<Flight> findFlightsByRouteAndMonth(long fromId, long toId, YearMonth month);
}