package eat_club.backend.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
    String itemId,
    String itemName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    String specialInstructions
) {
}
