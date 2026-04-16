package eat_club.backend.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
    @NotBlank String name,
    String phoneNumber,
    String address
) {
}
