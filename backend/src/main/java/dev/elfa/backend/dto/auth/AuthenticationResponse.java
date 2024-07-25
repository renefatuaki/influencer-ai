package dev.elfa.backend.dto.auth;

public record AuthenticationResponse(
        String token_type,
        Long expires_in,
        String access_token,
        String scope,
        String refresh_token
) {
}
