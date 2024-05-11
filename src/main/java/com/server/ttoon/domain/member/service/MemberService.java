package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<ApiResponse<?>> getAccountInfo(String userId);

    ResponseEntity<ApiResponse<?>> modifyProfile(String userId, ModifyRequestDto modifyRequestDto, String newUrl, String fileName);
}
