package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.AskRequestDto;
import com.server.ttoon.domain.member.dto.request.NickNameRequestDto;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.domain.member.service.MailService;
import com.server.ttoon.security.jwt.dto.request.OAuth2LoginReqDto;
import com.server.ttoon.domain.member.service.AppAuthService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERROR;

@Tag(name = "Auth API", description = "로그인 및 회원가입 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    @Operation(summary = "로그인", description = "RequestBody 로 provider, providerId, 이메일을 받아서 검증 후 로그인합니다.", security = @SecurityRequirement(name = ""))
    @PostMapping("/auth/app/login")
    public ResponseEntity<ApiResponse<?>> appLogin(@RequestBody OAuth2LoginReqDto oAuth2LoginReqDto){

        return appAuthService.login(oAuth2LoginReqDto);
    }

    @Operation(summary = "회원가입", description = "이용 약관 동의 후 회원의 권한을 USER 로 변경합니다.")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> appJoin(@RequestBody NickNameRequestDto nickNameRequestDto){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return appAuthService.join(memberId, nickNameRequestDto.getNickName());
    }

    @Operation(summary = "서버 버전 불러오기", description = "서버의 최소 버전 불러오기", security = @SecurityRequirement(name = ""))
    @GetMapping("/auth/version")
    public ResponseEntity<ApiResponse<?>> currentVersion(){

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, "1.0.0"));
    }

    @Operation(summary = "문의하기", description = "앱 문의사항 문의하기")
    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<?>> sendEmail(@RequestBody AskRequestDto askRequestDto){

        return mailService.sendEmail(askRequestDto);
    }

}
