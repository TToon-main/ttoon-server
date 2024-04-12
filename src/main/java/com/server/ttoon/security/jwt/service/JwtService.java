package com.server.ttoon.security.jwt.service;

import com.server.ttoon.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface JwtService {
    ResponseEntity<ApiResponse<?>> reissue(String accessToken, String refreshToken);
}
