package dev.elfa.backend.service;

import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.repository.InfluencerRepo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class StabilityServiceTest {

    @Autowired
    private StabilityService stabilityService;

    @MockBean
    private InfluencerRepo mockInfluencerRepo;

    private static final MockWebServer mockWebServer = new MockWebServer();

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("STABILITY_AI_URL", () -> mockWebServer.url("/").toString());
        registry.add("STABILITY_AI_API_KEY", () -> "your-api-key-here"); // Add this line
    }

    @Test
    void createImage_ValidId_ReturnsImage() throws IOException {
        byte[] image = new byte[]{1, 2, 3};

        try (Buffer buffer = new Buffer().write(image)) {
            mockWebServer.enqueue(new MockResponse()
                    .addHeader("Content-Type", "image/webp")
                    .setBody(buffer)
            );
        } catch (Exception e) {
            fail(e);
        }


        Appearance appearance = new Appearance(null, null, null, Set.of(), null, null, null, null, null, null, null);
        Influencer influencer = new Influencer("1000", null, null, appearance, null, null);

        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(influencer));

        Optional<Resource> result = stabilityService.createImage("1000");

        assertTrue(result.isPresent());
        assertArrayEquals(image, result.get().getContentAsByteArray());
    }

    @Test
    void createImage_ApiResponseIsNull_ReturnsEmpty() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "image/webp")
        );

        Appearance appearance = new Appearance(null, null, null, Set.of(), null, null, null, null, null, null, null);
        Influencer influencer = new Influencer("1000", null, null, appearance, null, null);

        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(influencer));

        Optional<Resource> result = stabilityService.createImage("1000");

        assertFalse(result.isPresent());
    }
}