package dev.elfa.backend.dto;

import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.personality.Personality;

public record InfluencerResponseDto(
        String id,
        TwitterDto twitter,
        Personality personality,
        Appearance appearance
) {
}
