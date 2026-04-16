package eat_club.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, String> {
    List<MenuItem> findByRestaurantRestaurantId(String restaurantId);
    List<MenuItem> findByRestaurantRestaurantIdAndCategoryIgnoreCase(String restaurantId, String category);
    List<MenuItem> findByRestaurantRestaurantIdAndIsVegetarian(String restaurantId, Boolean isVegetarian);
    Optional<MenuItem> findByItemIdAndRestaurantRestaurantId(String itemId, String restaurantId);
}
