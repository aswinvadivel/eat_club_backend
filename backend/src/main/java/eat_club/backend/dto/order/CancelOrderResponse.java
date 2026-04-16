package eat_club.backend.dto.order;

import eat_club.backend.entity.enums.OrderStatus;

public record CancelOrderResponse(
    String orderId,
    OrderStatus orderStatus,
    String reason,
    String refundStatus,
    long cancelledAt
) {
}
