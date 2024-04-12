package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.AppAuthReqDto;
import com.server.ttoon.domain.member.service.AppAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> appLogin(@RequestBody AppAuthReqDto appAuthReqDto){

        return appAuthService.login(appAuthReqDto);
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> appJoin(@RequestBody AppAuthReqDto appAuthReqDto){

        return appAuthService.join(appAuthReqDto);
    }
}
