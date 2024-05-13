package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.config.S3Service;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.service.MemberService;
import com.server.ttoon.security.jwt.dto.request.AuthorizationCodeDto;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Optional;

@Tag(name = "Setting API", description = "Setting 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;

    @Operation(summary = "프로필 + 계정 정보 조회", description = "사용자의 계정정보, 프로필 모두 조회합니다.")
    @GetMapping("/account")
    public ResponseEntity<ApiResponse<?>> getAccountInfo(){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return memberService.getAccountInfo(memberId);
    }

    @Operation(summary = "프로필 정보 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @PatchMapping( value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<?>> modifyProfile(@RequestPart MultipartFile file, @RequestPart ModifyRequestDto modifyRequestDto) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();

        String newImage = null;
        if (file != null) {
            newImage = s3Service.saveFile(file, "images");
        }

        return memberService.modifyProfile(memberId, modifyRequestDto, newImage);
    }

    @Operation(summary = "서비스 탈퇴", description = "로그인한 사용자의 앱/웹 서비스를 탈퇴합니다.")
    @DeleteMapping("/revoke")
    public ResponseEntity<ApiResponse<?>> revoke(@RequestBody Optional<AuthorizationCodeDto> appleIdentityTokenDto, @RequestHeader("sender") String sender) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberService.revoke(memberId, appleIdentityTokenDto, sender);
    }
}
