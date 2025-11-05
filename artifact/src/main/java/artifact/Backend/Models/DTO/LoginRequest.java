package artifact.Backend.Models.DTO;

public record LoginRequest(
    String email,
    String password
) {}
