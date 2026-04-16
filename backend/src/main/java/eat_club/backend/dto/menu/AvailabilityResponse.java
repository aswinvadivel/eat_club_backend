package eat_club.backend.dto.menu;

public record AvailabilityResponse(
    String itemId,
    Boolean isAvailable,
    long updatedAt
) {
}
