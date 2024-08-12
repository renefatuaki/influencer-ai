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
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
        registry.add("X_CLIENT_ID", () -> "mockClientId");
        registry.add("X_CLIENT_PW", () -> "mockClientPw");
        registry.add("X_REDIRECT_URI", () -> "mockRedirectUri");
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/testDatabase");
    }

    @Test
    void getInfluencer_ValidId_ReturnsOkStatus() throws Exception {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1010", twitter, null, null, null)));

        mvc.perform(MockMvcRequestBuilders.get("/api/influencer/1000"))
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "id": "1010",
                            "twitter": {
                                "id": "2020",
                                "name": "name",
                                "username": "username",
                                "auth": {
                                    "isAuthorized": true
                                }
                            }
                        }
                        """))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getInfluencer_InvalidId_ReturnsNotFoundStatus() throws Exception {
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get("/api/influencer/1000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getInfluencers_ValidRequest_ReturnsOkStatus() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

        Auth auth1 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter1 = new Twitter("1", "name1", "username1", auth1);
        Influencer influencer1 = new Influencer("1", twitter1, null, null, null);

        Auth auth2 = new Auth(true, "token", "secret", LocalDateTime.now());
        Twitter twitter2 = new Twitter("2", "name2", "username2", auth2);
        Influencer influencer2 = new Influencer("2", twitter2, null, null, null);

        List<Influencer> influencerList = Arrays.asList(influencer1, influencer2);
        Page<Influencer> influencerPage = new PageImpl<>(influencerList, pageable, influencerList.size());

        when(mockInfluencerRepo.findAll(pageable)).thenReturn(influencerPage);

        mvc.perform(MockMvcRequestBuilders.get("/api/influencer"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "totalPages": 1,
                            "totalElements": 2,
                            "first": true,
                            "last": true,
                            "size": 10,
                            "content": [
                                {
                                    "id": "1",
                                    "twitter": {
                                        "id": "1",
                                        "name": "name1",
                                        "username": "username1",
                                        "auth": {
                                            "isAuthorized": true
                                        }
                                    }
                                },
                                {
                                    "id": "2",
                                    "twitter": {
                                        "id": "2",
                                        "name": "name2",
                                        "username": "username2",
                                        "auth": {
                                            "isAuthorized": true
                                        }
                                    }
                                }
                            ],
                            "number": 0,
                            "sort": {
                                "empty": false,
                                "sorted": true,
                                "unsorted": false
                            },
                            "pageable": {
                                "pageNumber": 0,
                                "pageSize": 10,
                                "sort": {
                                    "empty": false,
                                    "sorted": true,
                                    "unsorted": false
                                },
                                "offset": 0,
                                "paged": true,
                                "unpaged": false
                            },
                            "numberOfElements": 2,
                            "empty": false
                        }
                        """));
    }

    @Test
    void deleteInfluencer_ValidId_ReturnsNoContentStatus() throws Exception {
        doNothing().when(mockInfluencerRepo).deleteById("1");

        mvc.perform(MockMvcRequestBuilders.delete("/api/influencer/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(mockInfluencerRepo, times(1)).deleteById("1");
    }

    @Test
    void deleteInfluencer_InvalidId_ReturnsNotFoundStatus() throws Exception {
        doThrow(new IllegalArgumentException()).when(mockInfluencerRepo).deleteById("1");

        mvc.perform(MockMvcRequestBuilders.delete("/api/influencer/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateInfluencerPersonality_ValidRequest_ReturnsAcceptedStatus() throws Exception {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1", twitter, null, null, null)));

        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(null);

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
    void updateInfluencerAppearance_ValidRequest_ReturnsAcceptedStatus() throws Exception {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("2020", "name", "username", auth);
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1", twitter, null, null, null)));

        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(null);

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
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().json("""
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
                        """));

        verify(mockInfluencerRepo, times(1)).findById(anyString());
        verify(mockInfluencerRepo, times(1)).save(any(Influencer.class));
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