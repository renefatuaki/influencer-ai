package dev.elfa.backend.dto;

public record TwitterDto(
        String id,
        String name,
        String username,
        AuthDto auth
) {
}
