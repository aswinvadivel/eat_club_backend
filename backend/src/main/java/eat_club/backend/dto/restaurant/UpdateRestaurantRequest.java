package eat_club.backend.dto.restaurant;

public record UpdateRestaurantRequest(
    String restaurantName,
    String description,
    String address,
    String phoneNumber,
    String cuisineType,
    Boolean isActive
) {
}
