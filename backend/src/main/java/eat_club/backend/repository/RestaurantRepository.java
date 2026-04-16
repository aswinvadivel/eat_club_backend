package eat_club.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    List<Restaurant> findByCuisineTypeIgnoreCase(String cuisineType);
    List<Restaurant> findByAdminUserId(String adminId);
}
