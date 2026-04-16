package eat_club.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.OrderEntity;
import eat_club.backend.entity.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByUserUserIdOrderByCreatedAtDesc(String userId);
    List<OrderEntity> findByUserUserIdAndOrderStatusOrderByCreatedAtDesc(String userId, OrderStatus status);
}
