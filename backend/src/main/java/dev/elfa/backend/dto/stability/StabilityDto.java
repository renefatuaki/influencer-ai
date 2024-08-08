package dev.elfa.backend.dto.stability;

public record StabilityDto(
        String image,
        String finishReason,
        long seed
) {
}
