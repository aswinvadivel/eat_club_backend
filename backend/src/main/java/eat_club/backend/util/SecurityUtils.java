package eat_club.backend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eat_club.backend.exception.UnauthorizedException;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new UnauthorizedException("Authentication required");
        }
        return authentication.getName();
    }
}