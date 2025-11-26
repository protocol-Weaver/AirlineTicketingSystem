package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.User;
import artifact.Backend.Repositories.Interfaces.IUserRepository;
import artifact.Backend.Tags.UserRole;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class UserRepository extends BaseJsonRepository<User> implements IUserRepository {

    public UserRepository() {
        super("users.json", new TypeToken<ArrayList<User>>(){}.getType(), "/users", User::id);
    }

    @Override
    protected void seedData() {
        System.out.println("Seeding Users...");
        this.dataList.add(new User(1, "John Admin", "admin@airline.com", "admin123", UserRole.ADMIN));
        this.dataList.add(new User(2, "Sam Customer", "customer@airline.com", "pass123", UserRole.CUSTOMER));
        save();
    }

    @Override
    public User findByEmail(String email) {
        return dataList.stream().filter(u -> u.email().equals(email)).findFirst().orElse(null);
    }

    @Override
    public void addUser(String name, String email, String password) {
        long newId = generateNextId();
        User user = new User(newId, name, email, password, UserRole.CUSTOMER);
        add(user);
    }


    @Override
    public void delete(long id) {
        // 1. Delete the User (This triggers Supabase DELETE)
        // Supabase will automatically delete the user AND cascading tickets/reservations
        super.delete(id); 

        // 2. Wait a brief moment for Supabase to finish the Cascade? 
        // Usually it's instant, but safe to fetch immediately.

        // 3. Tell related repositories to refresh themselves
        RepositoryProvider.getSupportRepository().refreshFromCloud();
        RepositoryProvider.getReservationRepository().refreshFromCloud();
        
        System.out.println("Cascading delete handled by Cloud. Local files updated.");
    }
}