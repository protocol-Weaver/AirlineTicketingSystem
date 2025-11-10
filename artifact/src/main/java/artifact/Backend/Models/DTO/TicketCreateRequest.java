package artifact.Backend.Models.DTO;

public record TicketCreateRequest(
    String subject,
    String description
) {}