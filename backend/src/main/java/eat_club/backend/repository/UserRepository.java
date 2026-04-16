package eat_club.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eat_club.backend.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
