package eat_club.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import eat_club.backend.dto.order.OrderItemResponse;
import eat_club.backend.dto.order.OrderResponse;
import eat_club.backend.dto.order.OrderSummaryResponse;
import eat_club.backend.entity.OrderEntity;
import eat_club.backend.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getItem().getItemId(),
            item.getItemName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice(),
            item.getSpecialInstructions()
        );
    }

    public OrderSummaryResponse toSummary(OrderEntity order) {
        return new OrderSummaryResponse(
            order.getOrderId(),
            order.getRestaurant().getRestaurantName(),
            order.getTotal(),
            order.getOrderStatus(),
            order.getCreatedAt(),
            order.getDeliveredAt()
        );
    }

    public OrderResponse toResponse(OrderEntity order, List<OrderItemResponse> items) {
        return new OrderResponse(
            order.getOrderId(),
            order.getUser().getUserId(),
            order.getRestaurant().getRestaurantId(),
            order.getRestaurant().getRestaurantName(),
            items,
            order.getDeliveryAddress(),
            order.getSubtotal(),
            order.getTax(),
            order.getDeliveryCharges(),
            order.getTotal(),
            order.getOrderStatus(),
            order.getPaymentStatus(),
            order.getPaymentMethod(),
            order.getEstimatedDeliveryTime(),
            order.getCreatedAt(),
            order.getDeliveredAt()
        );
    }
}