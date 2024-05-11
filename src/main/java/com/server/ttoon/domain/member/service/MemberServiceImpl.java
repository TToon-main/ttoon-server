package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.dto.response.AccountResponseDto;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    // 프로필 + 계정 정보 조회 메소드
    public ResponseEntity<ApiResponse<?>> getAccountInfo(String userId){

        Member member = memberRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .nickName(member.getNickName())
                .email(member.getEmail())
                .imageUrl(member.getImageUrl())
                .fileName(member.getImageFileName())
                .provider(member.getProvider())
                .point(member.getPoint())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, accountResponseDto));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> modifyProfile(String userId, ModifyRequestDto modifyRequestDto, String newUrl, String fileName) {

        Member member = memberRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

        member.updateNickName(modifyRequestDto.getNickName());

        member.updateImage(newUrl);

        member.updateFileName(fileName);

        memberRepository.save(member);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }
}
