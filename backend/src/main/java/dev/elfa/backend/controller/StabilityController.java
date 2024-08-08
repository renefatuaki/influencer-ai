package dev.elfa.backend.controller;

import dev.elfa.backend.service.StabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/stability")
@RequiredArgsConstructor
public class StabilityController {
    private final StabilityService stabilityService;

    @PostMapping("/influencer/{id}")
    ResponseEntity<byte[]> createInfluencerImage(@PathVariable String id) {
        Optional<byte[]> image = stabilityService.createInfluencerImage(id);

        return image.map(bytes -> ResponseEntity
                        .ok()
                        .contentType(new MediaType("image", "webp"))
                        .body(bytes))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }
}
