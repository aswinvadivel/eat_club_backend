package eat_club.backend.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequest(
    @NotBlank String itemId,
    @NotBlank String restaurantId,
    @NotNull @Min(1) Integer quantity,
    String specialInstructions
) {
}
