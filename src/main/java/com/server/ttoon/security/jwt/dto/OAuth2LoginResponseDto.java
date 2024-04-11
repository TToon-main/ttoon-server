package com.server.ttoon.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private boolean isGuest;
}
