package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Setting API", description = "Setting 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    @Operation(summary = "계정 정보 조회", description = "로그인한 사용자의 계정정보를 조회합니다.")
    @GetMapping("/api/account")
    public ResponseEntity<ApiResponse<?>> getAccountInfo(){

        return memberService.getAccountInfo();
    }
}
