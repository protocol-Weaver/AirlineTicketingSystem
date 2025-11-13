package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Aircraft;
import artifact.Backend.Repositories.Interfaces.IAircraftRepository;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class AircraftRepository extends BaseJsonRepository<Aircraft> implements IAircraftRepository {

    public AircraftRepository() {
        super("aircrafts.json", new TypeToken<ArrayList<Aircraft>>(){}.getType(), "/aircraft", Aircraft::id);
    }

    @Override
    protected void seedData() {
        System.out.println("Seeding Aircraft...");
        dataList.add(new Aircraft(1, "Boeing 747", 416));
        dataList.add(new Aircraft(2, "Airbus A320", 180));
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