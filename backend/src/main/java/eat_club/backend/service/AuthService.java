package eat_club.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.config.JwtService;
import eat_club.backend.dto.auth.AuthResponse;
import eat_club.backend.dto.auth.LoginRequest;
import eat_club.backend.dto.auth.SignupRequest;
import eat_club.backend.dto.auth.SignupResponse;
import eat_club.backend.entity.User;
import eat_club.backend.exception.BadRequestException;
import eat_club.backend.exception.UnauthorizedException;
import eat_club.backend.mapper.UserMapper;
import eat_club.backend.repository.UserRepository;
import eat_club.backend.util.IdUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        long now = System.currentTimeMillis();
        User user = new User();
        user.setUserId(IdUtils.uuid());
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setIsActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userMapper.toSignupResponse(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        user.setLastLoginAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }
}
