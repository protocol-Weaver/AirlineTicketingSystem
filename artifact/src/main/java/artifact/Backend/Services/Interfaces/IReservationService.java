package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.Flight;
import artifact.Backend.Models.ServiceResult;
import java.time.LocalDate;

public interface IReservationService {
    /**
     * Validates input, checks availability, and creates a reservation + ticket.
     * Also updates UI error labels if validation fails.
     *
     * @param custName Customer Name
     * @param custPhone Customer Phone
     * @param flight Selected Flight object
     * @param seatNum Seat Number string
     * @param resDate Date of reservation
     * @param priceStr Price as a string (needs parsing)
     * @param isPaid Payment status flag
     * @param nameErr UI Label for name errors
     * @param phoneErr UI Label for phone errors
     * @param flightErr UI Label for flight selection errors
     * @param seatErr UI Label for seat errors
     * @param dateErr UI Label for date errors
     * @param priceErr UI Label for price errors
     * @return true if reservation was successful, false otherwise.
     */
    ServiceResult addReservation(String custName, String custPhone, Flight flight, String seatNum,
                           LocalDate resDate, String priceStr, boolean isPaid);
}