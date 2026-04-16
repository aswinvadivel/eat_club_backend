package eat_club.backend.dto.restaurant;

public record RestaurantSummaryResponse(
    String restaurantId,
    String restaurantName,
    String description,
    String cuisineType,
    String address,
    Boolean isActive,
    double averageRating,
    long totalOrders
) {
}
