package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.dto.request.OAuth2LoginReqDto;
import com.server.ttoon.domain.member.service.AppAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;

    @PostMapping("/auth/app/login")
    public ResponseEntity<ApiResponse<?>> appLogin(@RequestBody OAuth2LoginReqDto oAuth2LoginReqDto){

        return appAuthService.login(oAuth2LoginReqDto);
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> appJoin(@AuthenticationPrincipal PrincipalDetails principalDetails){

        Member member = principalDetails.getMember();
        return appAuthService.join(member);
    }
}
