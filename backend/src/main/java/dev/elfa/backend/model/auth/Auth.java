package dev.elfa.backend.model.auth;

public record Auth(
        boolean isAuthorized,
        String accessToken,
        String refreshToken
) {
}
