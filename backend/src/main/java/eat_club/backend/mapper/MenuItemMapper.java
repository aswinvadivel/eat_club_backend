package eat_club.backend.mapper;

import org.springframework.stereotype.Component;

import eat_club.backend.dto.menu.MenuItemResponse;
import eat_club.backend.entity.MenuItem;

@Component
public class MenuItemMapper {

    public MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
            item.getItemId(),
            item.getRestaurant().getRestaurantId(),
            item.getItemName(),
            item.getDescription(),
            item.getPrice(),
            item.getCategory(),
            item.getIsVegetarian(),
            item.getIsSpicy(),
            item.getPreparationTime(),
            item.getImageUrl(),
            item.getIsAvailable(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}
