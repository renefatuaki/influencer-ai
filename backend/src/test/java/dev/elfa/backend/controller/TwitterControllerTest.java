package dev.elfa.backend.controller;

import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.repository.InfluencerRepo;
import okhttp3.mockwebserver.MockResponse;
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
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TwitterControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InfluencerRepo mockInfluencerRepo;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
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
    void addTwitter_ValidRequest_ReturnsCreatedStatus() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "token_type": "bearer",
                            "expires_in": 7200,
                            "access_token": "mockAccessToken",
                            "scope": "read write",
                            "refresh_token": "mockRefreshToken"
                        }
                        """)
        );

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "data": {
                                "id": "1000",
                                "name": "name",
                                "username": "username"
                            }
                        }
                        """)
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/twitter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "state": "mockState",
                                    "code": "mockCode"
                                }
                                """)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateTwitter_ValidRequest_ReturnsAcceptedStatus() throws Exception {
        Auth auth = new Auth(true, "mockAccessToken", "mockRefreshToken", LocalDateTime.now().plusHours(1));
        Twitter twitter = new Twitter("1000", "name", "username", auth);
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.of(new Influencer("1000", twitter, null, null)));

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "data": {
                                "id": "1000",
                                "name": "name",
                                "username": "username"
                            }
                        }
                        """)
        );

        mvc.perform(MockMvcRequestBuilders.put("/api/twitter/1000"))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    void updateTwitter_NotFound_ReturnsConflictStatus() throws Exception {
        when(mockInfluencerRepo.findById(anyString())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.put("/api/twitter/1000"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}