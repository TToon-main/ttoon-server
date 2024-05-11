package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<ApiResponse<?>> getAccountInfo();
    ResponseEntity<ApiResponse<?>> revoke(Long memberId);
}
