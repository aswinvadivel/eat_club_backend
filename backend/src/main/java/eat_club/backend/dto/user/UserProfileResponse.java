package eat_club.backend.dto.user;

import eat_club.backend.entity.enums.UserRole;

public record UserProfileResponse(
    String userId,
    String name,
    String email,
    UserRole role,
    String phoneNumber,
    String address,
    long createdAt,
    long updatedAt
) {
}
