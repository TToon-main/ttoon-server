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
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getAccountInfo(){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return memberService.getAccountInfo(memberId);
    }

    @Operation(summary = "프로필 정보 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @PatchMapping( value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> modifyProfile(@RequestPart("file") MultipartFile file, @RequestPart("modifyRequestDto") ModifyRequestDto modifyRequestDto) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();

        String newImage = null;
        if (file != null) {
            newImage = s3Service.saveFile(file, "images");
        }

        return memberService.modifyProfile(memberId, modifyRequestDto, newImage);
    }

    @Operation(summary = "서비스 탈퇴", description = "로그인한 사용자의 앱/웹 서비스를 탈퇴합니다.")
    @DeleteMapping("/revoke")
    public ResponseEntity<ApiResponse<?>> revoke(@RequestBody Optional<AuthorizationCodeDto> appleIdentityTokenDto, @RequestHeader("Sender") String sender) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberService.revoke(memberId, appleIdentityTokenDto, sender);
    }

    @Operation(summary = "친구 초대", description = "닉네임으로 친구추가를 합니다.")
    @PostMapping("/friends")
    public ResponseEntity<ApiResponse<?>> addFriend(@RequestBody String nickName){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberService.addFriend(memberId, nickName);
    }

    @Operation(summary = "친구 초대 수락", description = "친구 초대를 수락합니다.")
    @PatchMapping("/friends/{friendId}")
    public ResponseEntity<ApiResponse<?>> acceptInvite(@PathVariable("friendId") Long friendId){
        return memberService.acceptInvite(friendId);
    }

    @Operation(summary = "친구 초대 거절/ 친구 삭제", description = "친구 초대를 거절하거나 친구에서 삭제합니다.")
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<ApiResponse<?>> deleteFriend(@PathVariable("friendId") Long friendId){
        return memberService.deleteFriend(friendId);
    }

    @Operation(summary = "친구 목록 조회", description = "현재 사용자의 친구 목록을 조회합니다.")
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<?>> getFriends(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberService.getFriends(memberId);
    }
}
