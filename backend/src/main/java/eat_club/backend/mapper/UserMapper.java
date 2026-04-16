package eat_club.backend.mapper;

import org.springframework.stereotype.Component;

import eat_club.backend.dto.auth.SignupResponse;
import eat_club.backend.dto.user.UserProfileResponse;
import eat_club.backend.entity.User;

@Component
public class UserMapper {

    public SignupResponse toSignupResponse(User user) {
        return new SignupResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    public UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
