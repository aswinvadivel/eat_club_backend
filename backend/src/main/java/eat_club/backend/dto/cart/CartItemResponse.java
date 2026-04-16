package eat_club.backend.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
    String cartItemId,
    String itemId,
    String itemName,
    String restaurantId,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    String specialInstructions,
    long addedAt
) {
}
