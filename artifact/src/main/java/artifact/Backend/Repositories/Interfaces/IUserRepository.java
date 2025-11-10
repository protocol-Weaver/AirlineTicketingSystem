package artifact.Backend.Repositories.Interfaces;
import artifact.Backend.Models.User;

public interface IUserRepository {
    User findByEmail(String email);
    void addUser(String name, String email, String password);
}