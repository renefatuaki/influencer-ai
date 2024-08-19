package dev.elfa.backend.controller;

import dev.elfa.backend.dto.InfluencerDto;
import dev.elfa.backend.dto.ResponseWrapper;
import dev.elfa.backend.dto.auth.AuthorizationRequestBody;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.model.FileMetadata;
import dev.elfa.backend.model.Influencer;
import dev.elfa.backend.model.Tweet;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/twitter")
@RequiredArgsConstructor
public class TwitterController {
    private final TwitterService twitterService;
    private final InfluencerService influencerService;
    private final OllamaService ollamaService;
    private final StabilityService stabilityService;
    private final GridFsService gridFsService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<InfluencerDto>> addTwitterAccount(@RequestBody AuthorizationRequestBody authorizationRequestBody) {
        Auth auth = twitterService.getAuthToken(authorizationRequestBody.code());

        if (!auth.isAuthorized()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ResponseWrapper<>("Account couldn't be authenticated. Restart the authorization process."));

        Optional<TwitterAccountData> twitterAccountData = twitterService.getAccountData(auth.getAccessToken());

        if (twitterAccountData.isEmpty()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ResponseWrapper<>("Account couldn't be retrieved. Restart the authorization process."));

        Influencer influencer = twitterService.saveAccount(twitterAccountData.get(), auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(InfluencerDto.convertToDto(influencer)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTwitterAccount(@PathVariable String id) {
        Optional<Influencer> influencer = influencerService.getInfluencer(id);

        if (influencer.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        twitterService.updateAccount(influencer.get());

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/tweet/{id}")
    public ResponseEntity<Tweet> tweetText(@PathVariable String id) {
        Optional<Influencer> influencer = influencerService.getInfluencer(id);

        if (influencer.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            Optional<Tweet> tweetData = twitterService.tweetText(influencer.get());

            return tweetData
                    .map(data -> ResponseEntity.status(HttpStatus.OK).body(data))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @PostMapping("/tweet/{id}/image")
    public ResponseEntity<Void> tweetImage(@PathVariable String id) throws IOException {
        Optional<Influencer> influencerOptional = influencerService.getInfluencer(id);
        if (influencerOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Influencer influencer = influencerOptional.get();
        String tweetText = ollamaService.createTweet(influencer.getPersonality());
        String promptStabilityAi = StabilityService.getTweetPrompt(tweetText);
        Optional<Resource> optionalImage = stabilityService.createImage(promptStabilityAi);
        if (optionalImage.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        Resource image = optionalImage.get();
        FileMetadata fileMetadata = gridFsService.saveImage(image, influencer.getId());
        twitterService.saveDraftTweet(tweetText, fileMetadata.getId(), influencer.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/tweets")
    public ResponseEntity<List<Tweet>> getTweets() {
        List<Tweet> tweets = twitterService.getTweets();

        return ResponseEntity.status(HttpStatus.OK).body(tweets);
    }

    @GetMapping("/unapproved/tweets")
    public ResponseEntity<List<Tweet>> getUnapprovedTweets() {
        List<Tweet> tweets = twitterService.getUnapprovedTweets();

        return ResponseEntity.status(HttpStatus.OK).body(tweets);
    }

    @PutMapping("/tweet/{id}/approve")
    public ResponseEntity<Tweet> approveTweet(@PathVariable String id) {
        Optional<Tweet> optionalTweet = twitterService.getTweet(id);
        if (optionalTweet.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Tweet tweet = optionalTweet.get();
        Resource image = gridFsService.getResource(tweet.getImageId());
        String mediaId = twitterService.uploadImage(tweet.getInfluencerId(), image);

        try {
            Optional<Tweet> tweetData = twitterService.postTweet(tweet, mediaId);

            return tweetData
                    .map(data -> ResponseEntity.status(HttpStatus.OK).body(data))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @GetMapping("/tweet/image/{id}")
    public ResponseEntity<Resource> getTweetImage(@PathVariable String id) {
        Resource image = gridFsService.getResource(id);

        return ResponseEntity.ok()
                .contentType(new org.springframework.http.MediaType("image", "webp"))
                .body(image);
    }
}
