package com.server.ttoon.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.AppJoinReqDto;
import com.server.ttoon.domain.member.dto.request.AppLoginReqDto;
import com.server.ttoon.service.AppAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;

    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoLogin(@RequestBody AppLoginReqDto appLoginReqDto){

        return appAuthService.login(appLoginReqDto);
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> join(@RequestBody AppJoinReqDto appJoinReqDto){

        return appAuthService.join(appJoinReqDto);
    }
}
