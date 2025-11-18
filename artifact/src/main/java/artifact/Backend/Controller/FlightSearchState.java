package artifact.Backend.Controller;
import java.util.Collections;
import java.util.List;

import artifact.Backend.Models.FlightSearchResult;

/**
 * UPDATED: Now stores cabin, guest count, and selected seat.
 */
public class FlightSearchState {
    
    private static FlightSearchState instance;
    
    private List<FlightSearchResult> searchResults;
    private FlightSearchResult selectedFlight;
    
    // --- NEW STATE from Wizard ---
    private String selectedCabin;
    private int guestCount;
    // --- NEW STATE from SeatSelectionView ---
    private String selectedSeat;

    private FlightSearchState() {
        this.searchResults = Collections.emptyList();
        this.selectedCabin = "Economy";
        this.guestCount = 1;
    }

    public static FlightSearchState getInstance() {
        if (instance == null) {
            instance = new FlightSearchState();
        }
        return instance;
    }

    // --- Search Results ---
    public List<FlightSearchResult> getResults() { return searchResults; }
    public void setResults(List<FlightSearchResult> searchResults) { this.searchResults = searchResults; }

    // --- Selected Flight ---
    public FlightSearchResult getSelectedFlight() { return selectedFlight; }
    public void setSelectedFlight(FlightSearchResult selectedFlight) { this.selectedFlight = selectedFlight; }
    
    // --- Cabin & Guests ---
    public String getSelectedCabin() { return selectedCabin; }
    public void setSelectedCabin(String cabin) { this.selectedCabin = cabin; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int count) { this.guestCount = count; }

    // --- Selected Seat ---
    public String getSelectedSeat() { return selectedSeat; }
    public void setSelectedSeat(String seat) { this.selectedSeat = seat; }
    
    public void clearState() {
        searchResults = Collections.emptyList();
        selectedFlight = null;
        selectedCabin = "Economy";
        guestCount = 1;
        selectedSeat = null;
    }
}