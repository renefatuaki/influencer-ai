package dev.elfa.backend.service;

import dev.elfa.backend.dto.auth.AuthenticationResponse;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.dto.auth.TwitterAccountResponse;
import dev.elfa.backend.dto.twitter.TweetRequestBody;
import dev.elfa.backend.dto.twitter.TweetResponse;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Tweet;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.model.image.Image;
import dev.elfa.backend.model.personality.Personality;
import dev.elfa.backend.repository.InfluencerRepo;
import dev.elfa.backend.repository.TweetsRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TwitterService {
    private final RestClient restClient;
    private final String redirectUri;
    private final String clientId;
    private final String clientPassword;
    private final InfluencerRepo influencerRepo;
    private final TweetsRepo tweetsRepo;
    private final OllamaService ollamaService;

    public TwitterService(
            @Value("${X_URL}") String baseUrl,
            @Value("${X_CLIENT_ID}") String clientId,
            @Value("${X_CLIENT_PW}") String clientPassword,
            @Value("${X_REDIRECT_URI}") String redirectUri,
            InfluencerRepo influencerRepo,
            TweetsRepo tweetsRepo,
            OllamaService ollamaService
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.redirectUri = redirectUri;
        this.influencerRepo = influencerRepo;
        this.tweetsRepo = tweetsRepo;
        this.ollamaService = ollamaService;
    }

    public Auth getAuthToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientPassword);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code_verifier", "challenge");

        ResponseEntity<AuthenticationResponse> response = restClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .toEntity(AuthenticationResponse.class);

        if (!response.getStatusCode().isError() && response.getBody() != null) {
            AuthenticationResponse entityBody = response.getBody();
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(entityBody.expires_in());
            return new Auth(true, entityBody.access_token(), entityBody.refresh_token(), expiresAt);
        }

        return new Auth(false, null, null, null);
    }

    private boolean isTokenExpired(Auth auth) {
        return auth.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private void refreshAuthToken(Auth auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientPassword);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", auth.getRefreshToken());
        requestBody.add("client_id", clientId);

        ResponseEntity<AuthenticationResponse> response = restClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .toEntity(AuthenticationResponse.class);

        if (!response.getStatusCode().isError() && response.getBody() != null) {
            AuthenticationResponse entityBody = response.getBody();
            auth.setAccessToken(entityBody.access_token());
            auth.setRefreshToken(entityBody.refresh_token());
            auth.setExpiresAt(LocalDateTime.now().plusSeconds(entityBody.expires_in()));
        }
    }

    public Optional<TwitterAccountData> getAccountData(String token) {
        TwitterAccountResponse response = restClient.get()
                .uri("/users/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .body(TwitterAccountResponse.class);

        return Optional.ofNullable(response).map(TwitterAccountResponse::data);
    }

    public Influencer saveAccount(TwitterAccountData account, Auth auth) {
        Twitter twitter = new Twitter(account.id(), account.name(), account.username(), auth);
        Personality personality = new Personality(Set.of(), Set.of());
        Appearance appearance = new Appearance(null, null, null, Set.of(), null, null, null, null, null, null, null);
        Image image = new Image(null, null);
        Influencer influencer = new Influencer(account.id(), twitter, personality, appearance, image);
        return influencerRepo.save(influencer);
    }

    public Optional<Influencer> updateAccount(Influencer influencer) {
        Auth auth = influencer.getTwitter().auth();

        if (isTokenExpired(auth)) refreshAuthToken(auth);

        return this.getAccountData(auth.getAccessToken()).map(accountData -> {
            Twitter updatedTwitter = new Twitter(accountData.id(), accountData.name(), accountData.username(), auth);
            Influencer updatedInfluencer = new Influencer(influencer.getId(), updatedTwitter, influencer.getPersonality(), influencer.getAppearance(), null);
            influencerRepo.save(updatedInfluencer);

            return updatedInfluencer;
        });
    }

    public Optional<Tweet> tweetText(Influencer influencer) throws HttpClientErrorException {
        String tweetText = ollamaService.createTweet(influencer.getPersonality());
        Auth auth = influencer.getTwitter().auth();

        if (isTokenExpired(auth)) refreshAuthToken(auth);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth.getAccessToken());

        ResponseEntity<TweetResponse> response = restClient.post()
                .uri("/tweets")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(new TweetRequestBody(tweetText))
                .retrieve()
                .toEntity(TweetResponse.class);

        return Optional.ofNullable(response.getBody()).map(tweet -> {
            String link = String.format("https://x.com/%s/status/%s", influencer.getTwitter().id(), tweet.data().id());
            Tweet newTweet = new Tweet(tweet.data().id(), tweet.data().text(), link, LocalDateTime.now());
            tweetsRepo.save(newTweet);
            return newTweet;
        });
    }

    public List<Tweet> getTweets() {
        return tweetsRepo.findAll();
    }
}
