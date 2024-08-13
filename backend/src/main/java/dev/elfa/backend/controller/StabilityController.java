package dev.elfa.backend.controller;

import dev.elfa.backend.model.FileMetadata;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.service.GridFsService;
import dev.elfa.backend.service.InfluencerService;
import dev.elfa.backend.service.StabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

import static dev.elfa.backend.service.StabilityService.getAppearancePrompt;

@RestController
@RequestMapping("/api/stability")
@RequiredArgsConstructor
public class StabilityController {
    private final StabilityService stabilityService;
    private final GridFsService gridFsService;
    private final InfluencerService influencerService;

    @GetMapping("/influencer/{id}/base-image")
    ResponseEntity<InputStreamResource> getBaseImage(@PathVariable String id) throws IOException {
        GridFsResource gridFsResource = gridFsService.getBaseImage(id);

        return ResponseEntity.ok()
                .contentType(new MediaType("image", "webp"))
                .body(new InputStreamResource(gridFsResource.getInputStream()));
    }

    @PutMapping("/influencer/{id}/base-image")
    ResponseEntity<byte[]> createInfluencerImage(@PathVariable String id) {
        Optional<Influencer> optionalInfluencer = influencerService.getInfluencer(id);
        if (optionalInfluencer.isEmpty()) return ResponseEntity.notFound().build();

        Influencer influencer = optionalInfluencer.get();
        String prompt = getAppearancePrompt(influencer.getAppearance());
        Optional<byte[]> optionalImage = stabilityService.createInfluencerImage(prompt);
        if (optionalImage.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        byte[] image = optionalImage.get();
        FileMetadata fileMetadata = gridFsService.saveBaseImage(image, influencer.getId());
        influencerService.saveBaseImage(fileMetadata);

        return ResponseEntity
                .ok()
                .contentType(new MediaType("image", "webp"))
                .body(image);
    }
}
