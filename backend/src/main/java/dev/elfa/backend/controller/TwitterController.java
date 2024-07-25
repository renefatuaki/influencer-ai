package dev.elfa.backend.controller;

import dev.elfa.backend.dto.auth.AuthorizationRequestBody;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.model.auth.Auth;
import dev.elfa.backend.service.InfluencerService;
import dev.elfa.backend.service.TwitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/twitter")
@RequiredArgsConstructor
public class TwitterController {
    private final TwitterService twitterService;
    private final InfluencerService influencerService;

    @PostMapping
    public ResponseEntity<String> addTwitter(@RequestBody AuthorizationRequestBody authorizationRequestBody) {
        Auth auth = twitterService.getAuthToken(authorizationRequestBody.code());

        if (!auth.isAuthorized()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Account couldn't be authenticated. Restart the authorization process.");

        Optional<TwitterAccountData> twitterAccountData = twitterService.getUserData(auth.accessToken());

        if (twitterAccountData.isEmpty()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Account couldn't be retrieved. Restart the authorization process.");

        twitterService.saveAccount(twitterAccountData.get(), auth);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
