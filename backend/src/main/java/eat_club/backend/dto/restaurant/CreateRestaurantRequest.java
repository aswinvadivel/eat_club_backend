package eat_club.backend.dto.restaurant;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantRequest(
    @NotBlank String restaurantName,
    String description,
    @NotBlank String address,
    String phoneNumber,
    String cuisineType,
    Boolean isActive
) {
}
