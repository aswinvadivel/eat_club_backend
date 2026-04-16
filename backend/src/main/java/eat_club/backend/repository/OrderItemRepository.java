package eat_club.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderOrderId(String orderId);
}
