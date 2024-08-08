package dev.elfa.backend.model.appearance;

import lombok.With;

import java.util.Set;

@With
public record Appearance(
        BodyBuild bodyBuild,
        EyeColor eyeColor,
        EyeShape eyeShape,
        Set<FaceFeatures> faceFeatures,
        FaceShape faceShape,
        Gender gender,
        HairColor hairColor,
        HairLength hairLength,
        Height height,
        SkinTone skinTone,
        Style style
) {
}
