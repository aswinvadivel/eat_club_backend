package eat_club.backend.dto.auth;

import eat_club.backend.entity.enums.UserRole;

public record AuthResponse(
    String token,
    String userId,
    String name,
    String email,
    UserRole role
) {
}
