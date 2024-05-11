package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.security.jwt.dto.request.AppleIdentityTokenDto;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface MemberService {
    ResponseEntity<ApiResponse<?>> getAccountInfo();
    ResponseEntity<ApiResponse<?>> revoke(Long memberId, Optional<AppleIdentityTokenDto> appleIdentityTokenDto, String sender);
}
