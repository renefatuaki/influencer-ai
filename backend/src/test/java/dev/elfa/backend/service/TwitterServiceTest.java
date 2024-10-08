package dev.elfa.backend.service;

import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Tweet;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.model.personality.Interest;
import dev.elfa.backend.model.personality.Personality;
import dev.elfa.backend.model.personality.Tone;
import dev.elfa.backend.repository.InfluencerRepo;
import dev.elfa.backend.repository.TweetsRepo;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TwitterServiceTest {

    @Autowired
    private TwitterService twitterService;

    @MockBean
    private OllamaService mockOllamaService;

    @MockBean
    private InfluencerRepo mockInfluencerRepo;

    @MockBean
    private TweetsRepo mockTweetsRepo;

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

        when(mockInfluencerRepo.save(any(Influencer.class))).thenReturn(influencer);

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

    @Test
    void tweetText_ValidId_ReturnsTweet() {
        Twitter twitter = new Twitter("1000", "name", "username", new Auth(true, "accessToken", "refreshToken", LocalDateTime.now().plusHours(1)));
        Personality personality = new Personality(Set.of(Tone.FRIENDLY), Set.of(Interest.CULTURE, Interest.ART));
        Influencer influencer = new Influencer("1000", twitter, personality, null, null, null);
        when(mockOllamaService.createTweet(influencer.getPersonality())).thenReturn("Are you excited for the weekend?");

        Tweet tweet = new Tweet("100200300400500", "Are you excited for the weekend?", "https://x.com/username/status/100200300400500", "5050", "abc100", "9999", LocalDateTime.now(), true);
        when(mockTweetsRepo.save(tweet)).thenReturn(tweet);

        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "data": {
                            "id": "100200300400500",
                            "text": "Are you excited for the weekend?"
                          }
                        }
                        """)
        );

        Optional<Tweet> tweetResponse = twitterService.tweetText(influencer);

        assertTrue(tweetResponse.isPresent());
        assertEquals("100200300400500", tweetResponse.get().getId());
        assertEquals("Are you excited for the weekend?", tweetResponse.get().getText());
    }

    @Test
    void getTweets_List_ReturnsListOfTweet() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Tweet tweet1 = new Tweet("1000", "Hello World", "https://x.com/1000/status/1010", "5050", "abc100", "9999", localDateTime, true);
        Tweet tweet2 = new Tweet("1001", "Hello World", "https://x.com/1001/status/1010", "5050", "abc100", "9999", localDateTime, true);

        List<Tweet> expectedTweets = List.of(tweet1, tweet2);
        when(mockTweetsRepo.findAllByApprovedEquals(true)).thenReturn(expectedTweets);

        List<Tweet> tweets = mockTweetsRepo.findAllByApprovedEquals(true);

        assertEquals(expectedTweets, tweets);
    }

    @Test
    void getTweets_EmptyList_ReturnsEmptyList() {
        List<Tweet> expectedTweets = List.of();
        when(mockTweetsRepo.findAll()).thenReturn(expectedTweets);

        List<Tweet> tweets = twitterService.getTweets();

        assertEquals(expectedTweets, tweets);
    }
}