package dev.elfa.backend.model;

import lombok.With;

@With
public record Twitter(
        String email,
        String password,
        String userName
) {
}
