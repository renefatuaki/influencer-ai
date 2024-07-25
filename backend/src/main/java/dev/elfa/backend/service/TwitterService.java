package dev.elfa.backend.service;

import dev.elfa.backend.dto.auth.AuthenticationResponse;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.dto.auth.TwitterAccountResponse;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Twitter;
import dev.elfa.backend.model.auth.OAuth2;
import dev.elfa.backend.repository.InfluencerRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class TwitterService {
    private final static String URL = "https://api.twitter.com/2/";
    private final RestClient restClient;
    private final String redirectUri;
    private final String clientId;
    private final String password;
    private final InfluencerRepo influencerRepo;


    public TwitterService(
            @Value("${X_CLIENT_ID}") String clientId,
            @Value("${X_REDIRECT_URI}") String redirectUri,
            @Value("${X_CLIENT_PW}") String password,
            InfluencerRepo influencerRepo
    ) {
        this.restClient = RestClient.builder().baseUrl(URL).build();
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.password = password;
        this.influencerRepo = influencerRepo;
    }

    public OAuth2 getOAuth2Token(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, password);

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
            return new OAuth2(true, entityBody.access_token(), entityBody.refresh_token());
        }

        return new OAuth2(false, null, null);
    }

    public Optional<TwitterAccountData> getUserData(String token) {
        TwitterAccountResponse response = restClient.get()
                .uri("/users/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .body(TwitterAccountResponse.class);

        return Optional.ofNullable(response).map(TwitterAccountResponse::data);
    }

    public void saveAccount(TwitterAccountData account, OAuth2 oAuth2) {
        Twitter twitter = new Twitter(account.id(), account.name(), account.username(), oAuth2);
        Influencer influencer = new Influencer(twitter);
        influencerRepo.save(influencer);
    }
}
