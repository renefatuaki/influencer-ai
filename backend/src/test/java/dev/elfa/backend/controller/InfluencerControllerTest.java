package dev.elfa.backend.controller;

import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.repository.InfluencerRepo;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class InfluencerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InfluencerRepo mockInfluencerRepo;

    private static final MockWebServer mockWebServer = new MockWebServer();

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer.start();
    }

    @AfterAll
    static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("X_URL", () -> mockWebServer.url("/").toString());
    }

    @Test
    void getInfluencer_ValidId_ReturnsOkStatus() throws Exception {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1010", twitter, null, null, null, null)));

        mvc.perform(MockMvcRequestBuilders.delete("/api/influencer/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateInfluencerPersonality_ValidRequest_ReturnsAcceptedStatus() throws Exception {
        // Initialize Auth and Twitter objects
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);

        // Mock the repository to return a valid Influencer object
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1", twitter, null, null, null, null)));

        // Mock the save method to return a non-null value
        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(new Influencer("1", twitter, null, null, null, null));

        // Perform the PUT request and check the response
        mvc.perform(MockMvcRequestBuilders.put("/api/influencer/1/personality")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tone": [ "FRIENDLY" ],
                                  "interest": [ "CULTURE", "ART" ]
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "tone": [ "FRIENDLY" ],
                          "interest": [ "CULTURE", "ART" ]
                        }
                        """));

        // Verify interactions with the mock repository
        verify(mockInfluencerRepo, times(1)).findById(anyString());
        verify(mockInfluencerRepo, times(1)).save(any(Influencer.class));
    }

    @Test
    void updateInfluencerPersonality_InvalidId_ReturnsConflictStatus() throws Exception {
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.put("/api/influencer/1/personality")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tone": [ "FRIENDLY" ],
                                  "interest": [ "CULTURE", "ART" ]
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        verify(mockInfluencerRepo, times(1)).findById(anyString());
    }

    @Test
    void updateInfluencerAppearance_InvalidId_ReturnsConflictStatus() throws Exception {
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.put("/api/influencer/1/appearance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bodyBuild": "ATHLETIC",
                                  "eyeColor": "GREEN",
                                  "eyeShape": "ALMOND",
                                  "faceFeatures": [ "BEARD", "MOLE" ],
                                  "faceShape": "OVAL",
                                  "hairColor": "BLACK",
                                  "hairLength": "MEDIUM",
                                  "height": "AVERAGE",
                                  "skinTone": "LIGHT",
                                  "style": "BUSINESS"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        verify(mockInfluencerRepo, times(1)).findById(anyString());
    }
}