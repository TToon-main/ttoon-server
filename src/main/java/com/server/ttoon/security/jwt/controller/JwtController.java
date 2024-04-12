package com.server.ttoon.security.jwt.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.security.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<?>> reissue(@RequestHeader(value = "Authorization") String accessToken, @RequestHeader(value = "refreshToken") String refreshToken){
        //Bearer 접두사 삭제
        accessToken = accessToken.substring(7);
        return jwtService.reissue(accessToken,refreshToken);
    }
}
