package eat_club.backend.dto.order;

import eat_club.backend.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus orderStatus
) {
}