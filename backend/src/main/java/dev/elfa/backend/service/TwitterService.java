package dev.elfa.backend.service;

import dev.elfa.backend.dto.auth.AuthenticationResponse;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.dto.auth.TwitterAccountResponse;
import dev.elfa.backend.dto.twitter.Media;
import dev.elfa.backend.dto.twitter.TweetRequestBody;
import dev.elfa.backend.dto.twitter.TweetRequestMediaBody;
import dev.elfa.backend.dto.twitter.TweetResponse;
import dev.elfa.backend.model.*;
import dev.elfa.backend.model.appearance.Appearance;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.model.image.Image;
import dev.elfa.backend.model.personality.Personality;
import dev.elfa.backend.repository.InfluencerRepo;
import dev.elfa.backend.repository.TweetsRepo;
import io.github.renefatuaki.OAuth1AuthorizationHeaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TwitterService {
    private static final String MEDIA_URL = "https://upload.twitter.com/1.1/media/upload.json";

    private final RestClient restClient;
    private final String redirectUri;
    private final String clientId;
    private final String clientPassword;
    private final String accessToken;
    private final String accessTokenSecret;
    private final String consumerKey;
    private final String consumerSecret;
    private final InfluencerRepo influencerRepo;
    private final TweetsRepo tweetsRepo;
    private final OllamaService ollamaService;

    public TwitterService(
        @Value("${X_URL}") String baseUrl,
        @Value("${X_CLIENT_ID}") String clientId,
        @Value("${X_CLIENT_PW}") String clientPassword,
        @Value("${X_ACCESS_TOKEN}") String accessToken,
        @Value("${X_ACCESS_TOKEN_SECRET}") String accessTokenSecret,
        @Value("${X_CONSUMER_KEY}") String consumerKey,
        @Value("${X_CONSUMER_SECRET}") String consumerSecret,
        @Value("${X_REDIRECT_URI}") String redirectUri,
        InfluencerRepo influencerRepo,
        TweetsRepo tweetsRepo,
        OllamaService ollamaService
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
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

        AuthenticationResponse entityBody = response.getBody();
        if (!response.getStatusCode().isError() && entityBody != null) {
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(entityBody.expires_in());
            return new Auth(true, entityBody.access_token(), entityBody.refresh_token(), expiresAt);
        }

        return new Auth(false, null, null, null);
    }

    private boolean isTokenExpired(Auth auth) {
        return auth.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private void refreshAuthToken(Influencer influencer) {
        Auth auth = influencer.getTwitter().auth();

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

        AuthenticationResponse entityBody = response.getBody();
        if (!response.getStatusCode().isError() && entityBody != null) {
            auth.setAccessToken(entityBody.access_token());
            auth.setRefreshToken(entityBody.refresh_token());
            auth.setExpiresAt(LocalDateTime.now().plusSeconds(entityBody.expires_in()));

            influencerRepo.save(influencer);
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
        Scheduler scheduler = new Scheduler(null, Set.of());
        Influencer influencer = new Influencer(account.id(), twitter, personality, appearance, image, scheduler);
        return influencerRepo.save(influencer);
    }

    @Transactional
    public Optional<Influencer> updateAccount(Influencer influencer) {
        Auth auth = influencer.getTwitter().auth();

        if (isTokenExpired(auth)) refreshAuthToken(influencer);

        return this.getAccountData(auth.getAccessToken()).map(accountData -> {
            Twitter updatedTwitter = new Twitter(accountData.id(), accountData.name(), accountData.username(), auth);
            influencer.setTwitter(updatedTwitter);
            influencerRepo.save(influencer);

            return influencer;
        });
    }

    public Optional<Tweet> tweetText(Influencer influencer) throws HttpClientErrorException {
        String tweetText = ollamaService.createTweet(influencer.getPersonality());
        Auth auth = influencer.getTwitter().auth();

        if (isTokenExpired(auth)) refreshAuthToken(influencer);

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
            Tweet newTweet = new Tweet(tweet.data().id(), tweet.data().text(), link, null, null, influencer.getId(), LocalDateTime.now(), true);
            tweetsRepo.save(newTweet);
            return newTweet;
        });
    }

    public List<Tweet> getTweets() {
        return tweetsRepo.findAllByApprovedEquals(true);
    }

    public List<Tweet> getUnapprovedTweets() {
        return tweetsRepo.findAllByApprovedEquals(false);
    }

    @Transactional
    public void postScheduledTweet() {
        List<Influencer> influencers = influencerRepo.findBySchedulerScheduledTimeBetween(LocalTime.now(), LocalTime.now().plusMinutes(15));

        for (Influencer influencer : influencers) {
            tweetText(influencer);
        }
    }

    public void saveDraftTweet(String tweetText, String imageId, String influencerId) {
        tweetsRepo.save(new Tweet(null, tweetText, null, null, imageId, influencerId, null, false));
    }

    @Transactional
    public void updateDraftTweetImage(String tweetId, String imageId) {
        Optional<Tweet> optionalTweet = tweetsRepo.findById(tweetId);
        if (optionalTweet.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet not found");

        Tweet tweet = optionalTweet.get();
        tweet.setImageId(imageId);
        tweetsRepo.save(tweet);
    }

    public Optional<Tweet> getTweet(String id) {
        return tweetsRepo.findById(id);
    }

    public String uploadImage(String influencerId, Resource image) {
        RestClient client = RestClient.builder().baseUrl(MEDIA_URL).build();

        String authorization = new OAuth1AuthorizationHeaderBuilder()
            .setHttpMethod("POST")
            .setUrl(MEDIA_URL)
            .setConsumerSecret(consumerSecret)
            .setTokenSecret(accessTokenSecret)
            .addParameter("oauth_consumer_key", consumerKey)
            .addParameter("oauth_token", accessToken)
            .addQueryParameter("additional_owners=" + influencerId)
            .build();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("media", image);
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);

        ResponseEntity<TwitterUploadResponse> response = client.post()
            .uri(uriBuilder -> uriBuilder
                .queryParam("additional_owners", influencerId)
                .build())
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(multipartBody)
            .retrieve()
            .toEntity(TwitterUploadResponse.class);

        TwitterUploadResponse twitterUploadResponse = response.getBody();

        if (twitterUploadResponse == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");

        return twitterUploadResponse.mediaId();
    }

    @Transactional
    public Optional<Tweet> postTweet(Tweet tweet, String mediaId) {
        Optional<Influencer> optionalInfluencer = influencerRepo.findById(tweet.getInfluencerId());

        if (optionalInfluencer.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Influencer not found");

        Influencer influencer = optionalInfluencer.get();
        Auth auth = influencer.getTwitter().auth();

        if (isTokenExpired(auth)) refreshAuthToken(influencer);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth.getAccessToken());

        ResponseEntity<TweetResponse> response = restClient.post()
            .uri("/tweets")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .body(new TweetRequestMediaBody(tweet.getText(), new Media(new String[]{mediaId})))
            .retrieve()
            .toEntity(TweetResponse.class);

        return Optional.ofNullable(response.getBody()).map(updatedTweet -> {
            String link = String.format("https://x.com/%s/status/%s", influencer.getTwitter().id(), updatedTweet.data().id());
            tweet.setTweetId(updatedTweet.data().id());
            tweet.setApproved(true);
            tweet.setCreatedAt(LocalDateTime.now());
            tweet.setLink(link);

            return tweetsRepo.save(tweet);
        });
    }
}
