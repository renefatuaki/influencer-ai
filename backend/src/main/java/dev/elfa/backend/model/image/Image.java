package dev.elfa.backend.model.image;

import lombok.With;

import java.util.List;

@With
public record Image(
        String baseImage,
        List<String> images
) {
}
