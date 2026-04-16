package eat_club.backend.dto.order;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequest(
    @NotBlank String reason
) {
}
