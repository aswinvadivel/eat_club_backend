package eat_club.backend.dto.menu;

import java.math.BigDecimal;

public record MenuItemResponse(
    String itemId,
    String restaurantId,
    String itemName,
    String description,
    BigDecimal price,
    String category,
    Boolean isVegetarian,
    Boolean isSpicy,
    Integer preparationTime,
    String imageUrl,
    Boolean isAvailable,
    long createdAt,
    long updatedAt
) {
}
