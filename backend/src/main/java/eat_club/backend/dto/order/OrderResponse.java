package eat_club.backend.dto.order;

import java.math.BigDecimal;
import java.util.List;

import eat_club.backend.entity.enums.OrderStatus;
import eat_club.backend.entity.enums.PaymentStatus;

public record OrderResponse(
    String orderId,
    String userId,
    String restaurantId,
    String restaurantName,
    List<OrderItemResponse> orderItems,
    String deliveryAddress,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal deliveryCharges,
    BigDecimal total,
    OrderStatus orderStatus,
    PaymentStatus paymentStatus,
    String paymentMethod,
    Long estimatedDeliveryTime,
    Long createdAt,
    Long deliveredAt
) {
}
