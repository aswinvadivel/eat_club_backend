package eat_club.backend.dto.restaurant;

import java.util.List;

import eat_club.backend.dto.menu.MenuItemResponse;

public record RestaurantResponse(
    String restaurantId,
    String adminId,
    String restaurantName,
    String description,
    String address,
    String phoneNumber,
    String cuisineType,
    Boolean isActive,
    List<MenuItemResponse> menuItems,
    long createdAt,
    long updatedAt
) {
}
