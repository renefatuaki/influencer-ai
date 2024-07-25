package dev.elfa.backend.controller;

import dev.elfa.backend.dto.InfluencerResponseDto;
import dev.elfa.backend.service.InfluencerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/influencer")
@RequiredArgsConstructor
public class InfluencerController {
    private final InfluencerService influencerService;

    @GetMapping("/{id}")
    public ResponseEntity<InfluencerResponseDto> getInfluencer(@PathVariable String id) {
        Optional<InfluencerResponseDto> influencerResponseDto = influencerService.getInfluencerDto(id);

        return influencerResponseDto
                .map(influencer -> ResponseEntity.status(HttpStatus.OK).body(influencer))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<Page<InfluencerResponseDto>> getInfluencers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        Page<InfluencerResponseDto> influencers = influencerService.getInfluencers(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(influencers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfluencer(@PathVariable String id) {
        boolean isDeleted = influencerService.deleteInfluencer(id);

        return isDeleted
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
