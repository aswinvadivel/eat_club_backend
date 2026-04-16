package eat_club.backend.dto.menu;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMenuItemRequest(
    @NotBlank String itemName,
    String description,
    @NotNull @DecimalMin("0.0") BigDecimal price,
    String category,
    Boolean isVegetarian,
    Boolean isSpicy,
    Integer preparationTime,
    String imageUrl,
    Boolean isAvailable
) {
}
