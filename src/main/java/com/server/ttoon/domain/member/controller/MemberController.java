package com.server.ttoon.domain.member.controller;

import com.server.ttoon.common.config.S3Service;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return memberService.getAccountInfo(userId);
    }

    @Operation(summary = "프로필 정보 수정", description = "사용자의 프로필 정보를 수정합니다.")
    @PatchMapping( value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<?>> modifyProfile(@RequestPart MultipartFile file, @RequestPart ModifyRequestDto modifyRequestDto) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();


        String fileName = null;
        String newUrl = null;
        if (file != null) {
            // s3 에서 해당 파일 삭제하려면 fileName 자체가 필요함.
            // imageUrl에는 aws 에서 임의로 코드같은걸 붙이기 때문에 그게 없는 순수한 fileName 필요
            fileName = file.getOriginalFilename();
            newUrl = s3Service.saveFile(file, "images");
        }

        String oldUrl = modifyRequestDto.getOldImageName();
        if (oldUrl != null) {
            s3Service.deleteImage(oldUrl, "images");
        }

        return memberService.modifyProfile(userId, modifyRequestDto, newUrl, fileName);
    }
}
