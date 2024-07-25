package dev.elfa.backend.model;

import dev.elfa.backend.model.auth.OAuth2;
import lombok.With;

@With
public record Twitter(
        String id,
        String name,
        String username,
        OAuth2 oAuth2
) {

}
