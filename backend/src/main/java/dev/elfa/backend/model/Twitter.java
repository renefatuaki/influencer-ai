package dev.elfa.backend.model;

import dev.elfa.backend.model.auth.Auth;
import lombok.With;

@With
public record Twitter(
        String id,
        String name,
        String username,
        Auth auth
) {

}
