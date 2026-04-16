package eat_club.backend.dto.menu;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(
    String itemName,
    String description,
    BigDecimal price,
    String category,
    Boolean isVegetarian,
    Boolean isSpicy,
    Integer preparationTime,
    String imageUrl,
    Boolean isAvailable
) {
}
