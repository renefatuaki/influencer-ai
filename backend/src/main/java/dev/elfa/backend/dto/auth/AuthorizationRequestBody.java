package dev.elfa.backend.dto.auth;

public record AuthorizationRequestBody(String state, String code) {
}
