package eat_club.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserUserId(String userId);
}
