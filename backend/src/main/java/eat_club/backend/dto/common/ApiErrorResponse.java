package eat_club.backend.dto.common;

public record ApiErrorResponse(
    long timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
