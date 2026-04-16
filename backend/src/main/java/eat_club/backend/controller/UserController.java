package eat_club.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eat_club.backend.dto.user.UpdateUserProfileRequest;
import eat_club.backend.dto.user.UserProfileResponse;
import eat_club.backend.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileResponse getUserProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/profile")
    public UserProfileResponse updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        return userService.updateCurrentUserProfile(request);
    }
}
