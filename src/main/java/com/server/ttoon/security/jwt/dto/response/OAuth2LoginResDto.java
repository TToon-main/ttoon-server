package com.server.ttoon.security.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonPropertyOrder({"isGuest", "accessToken", "refreshToken"})
@Builder
public class OAuth2LoginResDto {
    private String accessToken;
    private String refreshToken;
    private boolean isGuest;

    @JsonProperty("isGuest")
    public boolean isGuest(){
        return isGuest;
    }
    @JsonProperty("accessToken")
    public String getAccessToken(){
        return accessToken;
    }
    @JsonProperty("refreshToken")
    public String getRefreshToken(){
        return refreshToken;
    }
}
