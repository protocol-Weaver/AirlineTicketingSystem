package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Crew;
import artifact.Backend.Repositories.Interfaces.ICrewRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class CrewRepository extends BaseJsonRepository<Crew> implements ICrewRepository {

    public CrewRepository() {
        super("crews.json", new TypeToken<ArrayList<Crew>>(){}.getType(), "/crew", Crew::id);
    }

    @Override
    protected void seedData() {
        System.out.println("Seeding Crew...");
        dataList.add(new Crew(1, "Alpha Team", "Capt. Rogers"));
        dataList.add(new Crew(2, "Bravo Team", "Capt. Marvel"));
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