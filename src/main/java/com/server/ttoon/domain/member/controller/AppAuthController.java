package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.dto.request.OAuth2LoginReqDto;
import com.server.ttoon.domain.member.service.AppAuthService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERREOR;

@Tag(name = "앱 로그인 API", description = "로그인 및 회원가입 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;
    private final MemberRepository memberRepository;

    @Operation(summary = "로그인", description = "RequestBody 로 provider, providerId 받아서 검증 후 로그인합니다.")
    @PostMapping("/auth/app/login")
    public ResponseEntity<ApiResponse<?>> appLogin(@RequestBody OAuth2LoginReqDto oAuth2LoginReqDto){

        return appAuthService.login(oAuth2LoginReqDto);
    }

    @Operation(summary = "회원가입", description = "이용 약관 동의 후 회원의 권한을 USER 로 변경합니다.")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> appJoin(){

        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));
        return appAuthService.join(member);
    }
}
