package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.dto.response.AccountResponseDto;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.jwt.dto.request.AppleIdentityTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.server.ttoon.common.response.ApiResponse.*;
import static com.server.ttoon.common.response.status.ErrorStatus.BADREQUEST_ERROR;
import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERREOR;
import static com.server.ttoon.common.response.status.SuccessStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    // 프로필 + 계정 정보 조회 메소드
    public ResponseEntity<ApiResponse<?>> getAccountInfo(Long memberId){

        Member member = memberRepository.findById(memberId)
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
    public ResponseEntity<ApiResponse<?>> modifyProfile(Long memberId, ModifyRequestDto modifyRequestDto, String newUrl, String fileName) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

        member.updateNickName(modifyRequestDto.getNickName());

        member.updateImage(newUrl);

        member.updateFileName(fileName);

        memberRepository.save(member);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    public ResponseEntity<ApiResponse<?>> revoke(Long memberId, Optional<AppleIdentityTokenDto> appleIdentityTokenDto, String sender){

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));
        if(member.getProvider().equals(Provider.APPLE.toString()) && sender.equals("app"))
        {
            if(!appleIdentityTokenDto.isPresent())
                throw new CustomRuntimeException(BADREQUEST_ERROR);

            String idToken = appleIdentityTokenDto.get().getIdToken();



        }
        memberRepository.delete(member);
        return ResponseEntity.ok(onSuccess(_OK));
    }
}