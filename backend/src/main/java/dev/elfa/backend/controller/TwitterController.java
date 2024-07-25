package dev.elfa.backend.controller;

import dev.elfa.backend.dto.auth.AuthorizationRequestBody;
import dev.elfa.backend.dto.auth.TwitterAccountData;
import dev.elfa.backend.model.auth.OAuth2;
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

    @PostMapping
    public ResponseEntity<String> addTwitter(@RequestBody AuthorizationRequestBody authorizationRequestBody) {
        OAuth2 oAuth2 = twitterService.getOAuth2Token(authorizationRequestBody.code());

        if (!oAuth2.isAuthorized()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Account couldn't be authenticated. Restart the authorization process.");

        Optional<TwitterAccountData> twitterAccountData = twitterService.getUserData(oAuth2.accessToken());

        if (twitterAccountData.isEmpty()) return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Account couldn't be retrieved. Restart the authorization process.");

        twitterService.saveAccount(twitterAccountData.get(), oAuth2);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
