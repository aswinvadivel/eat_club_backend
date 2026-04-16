package eat_club.backend.dto.order;

import java.math.BigDecimal;

import eat_club.backend.entity.enums.OrderStatus;

public record OrderSummaryResponse(
    String orderId,
    String restaurantName,
    BigDecimal total,
    OrderStatus orderStatus,
    Long createdAt,
    Long deliveredAt
) {
}
