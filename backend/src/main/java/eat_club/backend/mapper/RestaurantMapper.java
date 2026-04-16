package eat_club.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import eat_club.backend.dto.menu.MenuItemResponse;
import eat_club.backend.dto.restaurant.RestaurantResponse;
import eat_club.backend.dto.restaurant.RestaurantSummaryResponse;
import eat_club.backend.entity.Restaurant;

@Component
public class RestaurantMapper {

    public RestaurantSummaryResponse toSummaryResponse(Restaurant restaurant) {
        return new RestaurantSummaryResponse(
            restaurant.getRestaurantId(),
            restaurant.getRestaurantName(),
            restaurant.getDescription(),
            restaurant.getCuisineType(),
            restaurant.getAddress(),
            restaurant.getIsActive(),
            0.0,
            0L
        );
    }

    public RestaurantResponse toResponse(Restaurant restaurant, List<MenuItemResponse> menuItems) {
        return new RestaurantResponse(
            restaurant.getRestaurantId(),
            restaurant.getAdmin().getUserId(),
            restaurant.getRestaurantName(),
            restaurant.getDescription(),
            restaurant.getAddress(),
            restaurant.getPhoneNumber(),
            restaurant.getCuisineType(),
            restaurant.getIsActive(),
            menuItems,
            restaurant.getCreatedAt(),
            restaurant.getUpdatedAt()
        );
    }
}
