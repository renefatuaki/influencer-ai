package dev.elfa.backend.service;

import dev.elfa.backend.model.appearance.Appearance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StabilityService {
    private final RestClient restClient;
    private final String apiKey;
    private final MediaType webp = new MediaType("image", "webp");

    public StabilityService(
            @Value("${STABILITY_AI_API_KEY}") String apiKey,
            @Value("${STABILITY_AI_URL}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public static String getAppearancePrompt(Appearance appearance) {
        String faceFeatures = appearance.faceFeatures().stream().map(Enum::name).collect(Collectors.joining(", ")).toLowerCase();

        return String.format("""
                        Create a realistic image of a %s person with an %s body build, %s %s eyes, a %s face shape with %s, %s skin tone, %s %s and %s height.
                        The person should be dressed in a %s.
                        """,
                appearance.gender(),
                appearance.bodyBuild(),
                appearance.eyeColor(),
                appearance.eyeShape(),
                appearance.faceShape(),
                faceFeatures,
                appearance.skinTone(),
                appearance.hairLength(),
                appearance.hairColor(),
                appearance.height(),
                appearance.style()
        );
    }

    public static String getTweetPrompt(String tweet) {
        return String.format("Create a realistic image for this twitter post: %s. Do not include any person in the image.", tweet);
    }

    public Optional<Resource> createImage(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setAccept(List.of(webp));
        headers.set("Accept", "image/*");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("prompt", prompt);
        requestBody.add("style_preset", "photographic");
        requestBody.add("output_format", "webp");
        requestBody.add("aspect_ratio", "1:1");

        ResponseEntity<Resource> response = restClient.post()
                .uri("/stable-image/generate/core")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .toEntity(Resource.class);

        return Optional.ofNullable(response.getBody());
    }
}
