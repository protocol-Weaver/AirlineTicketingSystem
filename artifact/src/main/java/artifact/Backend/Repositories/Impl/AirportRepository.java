package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Airport;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class AirportRepository extends BaseJsonRepository<Airport> implements IAirportRepository {

    public AirportRepository() {
        super("airports.json", new TypeToken<ArrayList<Airport>>(){}.getType(), "/airports", Airport::id);
    }

    @Override
    protected void seedData() {
        System.out.println("Seeding Airports...");
        dataList.add(new Airport(1, "JFK", "New York"));
        dataList.add(new Airport(2, "LAX", "Los Angeles"));
        dataList.add(new Airport(3, "LHE", "Lahore"));
        dataList.add(new Airport(4, "DXB", "Dubai"));
        save();
    }


    @Override
    public void delete(long id) {
        // 1. Delete the User (This triggers Supabase DELETE)
        // Supabase will automatically delete the user AND cascading tickets/reservations
        super.delete(id); 

        // 2. Wait a brief moment for Supabase to finish the Cascade? 
        // Usually it's instant, but safe to fetch immediately.

        // 3. Tell related repositories to refresh themselves
        RepositoryProvider.getFlightRepository().refreshFromCloud();
        RepositoryProvider.getReservationRepository().refreshFromCloud();
        RepositoryProvider.getTicketRepository().refreshFromCloud();
        
        System.out.println("Cascading delete handled by Cloud. Local files updated.");
    }
}