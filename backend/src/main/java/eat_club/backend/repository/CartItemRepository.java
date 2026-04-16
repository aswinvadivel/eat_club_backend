package eat_club.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCartCartId(String cartId);
    Optional<CartItem> findByCartCartIdAndItemItemId(String cartId, String itemId);
    void deleteByCartCartId(String cartId);
}
