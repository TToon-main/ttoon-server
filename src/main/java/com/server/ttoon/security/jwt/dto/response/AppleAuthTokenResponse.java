package com.server.ttoon.security.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppleAuthTokenResponse {
    private String accessToken;
    private int expiresIn;
    private String idToken;
    private String refreshToken;
    private String tokenType;
}
