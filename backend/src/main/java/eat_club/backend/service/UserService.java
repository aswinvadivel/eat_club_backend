package eat_club.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.dto.user.UpdateUserProfileRequest;
import eat_club.backend.dto.user.UserProfileResponse;
import eat_club.backend.entity.User;
import eat_club.backend.exception.ResourceNotFoundException;
import eat_club.backend.mapper.UserMapper;
import eat_club.backend.repository.UserRepository;
import eat_club.backend.util.SecurityUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {
        User user = getCurrentUser();
        user.setName(request.name());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());
        user.setUpdatedAt(System.currentTimeMillis());
        return userMapper.toProfileResponse(userRepository.save(user));
    }

    public User getCurrentUser() {
        String userId = SecurityUtils.currentUserId();
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
