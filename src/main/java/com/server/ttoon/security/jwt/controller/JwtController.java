package com.server.ttoon.security.jwt.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.security.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Jwt 토큰 재발급 API")
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    @Operation(summary = "토큰 재발급 API", description = "리프레쉬 토큰을 검증한 후 액세스 토큰을 재발급합니다.", security = @SecurityRequirement(name = ""))
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<?>> reissue(@RequestHeader(value = "Authorization") String accessToken, @RequestHeader(value = "refreshToken") String refreshToken){
        //Bearer 접두사 삭제
        accessToken = accessToken.substring(7);
        return jwtService.reissue(accessToken,refreshToken);
    }
}
