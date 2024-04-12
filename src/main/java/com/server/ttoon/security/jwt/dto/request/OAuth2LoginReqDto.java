package com.server.ttoon.security.jwt.dto.request;

import lombok.Data;

@Data
public class OAuth2LoginReqDto {
    private String providerId;
    private String provider;
}
