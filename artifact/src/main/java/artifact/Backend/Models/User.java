package artifact.Backend.Models;
import artifact.Backend.Tags.UserRole;

public record User(long id, String name, String email, String password, UserRole role) {}

