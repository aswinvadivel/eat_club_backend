package eat_club.backend.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    String cartId,
    String userId,
    String restaurantId,
    List<CartItemResponse> cartItems,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal deliveryCharges,
    BigDecimal total,
    long lastUpdated
) {
}
