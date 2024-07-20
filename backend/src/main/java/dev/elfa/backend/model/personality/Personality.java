package dev.elfa.backend.model.personality;

import lombok.With;

import java.util.Set;

@With
public record Personality(
        Set<Tone> tone,
        Set<Interest> interest
) {
}
