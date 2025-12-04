package artifact.Backend.Controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import artifact.Backend.Models.FlightSearchResult;

/**
 * UPDATED: Stores a Set<String> of seats to support multi-guest booking.
 */
public class FlightSearchState {
    
    private static FlightSearchState instance;
    
    private List<FlightSearchResult> searchResults;
    private FlightSearchResult selectedFlight;
    
    private String selectedCabin;
    private int guestCount;
    
    private Set<String> selectedSeats;

    private FlightSearchState() {
        this.searchResults = Collections.emptyList();
        this.selectedCabin = "Economy";
        this.guestCount = 1;
        this.selectedSeats = new HashSet<>();
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

    // --- UPDATED: Seat Management ---
    public Set<String> getSelectedSeats() { return selectedSeats; }
    
    public void addSeat(String seat) {
        this.selectedSeats.add(seat);
    }
    
    public void removeSeat(String seat) {
        this.selectedSeats.remove(seat);
    }
    
    public boolean isSelectionComplete() {
        return selectedSeats.size() == guestCount;
    }
    
    public void clearState() {
        searchResults = Collections.emptyList();
        selectedFlight = null;
        selectedCabin = "Economy";
        guestCount = 1;
        selectedSeats.clear();
    }
}