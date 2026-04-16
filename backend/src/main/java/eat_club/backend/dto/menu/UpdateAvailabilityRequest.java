package eat_club.backend.dto.menu;

import jakarta.validation.constraints.NotNull;

public record UpdateAvailabilityRequest(
    @NotNull Boolean isAvailable
) {
}
