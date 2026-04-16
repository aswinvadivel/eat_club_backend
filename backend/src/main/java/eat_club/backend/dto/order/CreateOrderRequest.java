package eat_club.backend.dto.order;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
    @NotBlank String deliveryAddress,
    @NotBlank String phoneNumber,
    @NotBlank String paymentMethod,
    String specialInstructions
) {
}
