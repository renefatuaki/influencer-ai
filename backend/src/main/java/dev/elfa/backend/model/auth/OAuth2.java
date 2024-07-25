package dev.elfa.backend.model.auth;

public record OAuth2(
        boolean isAuthorized,
        String accessToken,
        String refreshToken
) {
}
