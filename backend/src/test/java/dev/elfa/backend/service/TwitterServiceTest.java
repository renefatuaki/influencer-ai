package dev.elfa.backend.service;

import dev.elfa.backend.dto.auth.TwitterAccountData;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TwitterServiceTest {

    @Autowired
    private TwitterService twitterService;

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
    void getAuthToken_ValidCode_ReturnsAuthToken() {
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

        Auth auth = twitterService.getAuthToken("mockCode");

        assertTrue(auth.isAuthorized());
        assertEquals("mockAccessToken", auth.getAccessToken());
        assertEquals("mockRefreshToken", auth.getRefreshToken());
        assertNotNull(auth.getExpiresAt());
        assertTrue(auth.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void getAccountData_ValidToken_ReturnsAccountData() {
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

        Optional<TwitterAccountData> accountData = twitterService.getAccountData("mockAccessToken");

        assertTrue(accountData.isPresent());
        assertEquals("1000", accountData.get().id());
        assertEquals("name", accountData.get().name());
        assertEquals("username", accountData.get().username());
    }

    @Test
    void updateAccount_ValidData_UpdatesAccount() {
        Twitter twitter = new Twitter("1000", "oldName", "oldUsername", new Auth(true, "accessToken", "refreshToken", LocalDateTime.now().minusHours(1)));
        Influencer influencer = new Influencer(twitter);

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
                                "name": "newName",
                                "username": "newUsername"
                            }
                        }
                        """)
        );

        Optional<Influencer> influencerOptional = twitterService.updateAccount(influencer);

        assertTrue(influencerOptional.isPresent());
        assertEquals("newName", influencerOptional.get().getTwitter().name());
        assertEquals("newUsername", influencerOptional.get().getTwitter().username());
    }
}