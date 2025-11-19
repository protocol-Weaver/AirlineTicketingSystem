package artifact.Backend.Models.DTO;

public record RegisterRequest(
    String name,
    String email,
    String password
) {}