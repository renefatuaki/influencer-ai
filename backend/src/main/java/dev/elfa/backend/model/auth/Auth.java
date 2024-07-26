package dev.elfa.backend.model.auth;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Auth {
    private boolean isAuthorized;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
