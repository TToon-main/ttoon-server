package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.security.jwt.dto.request.OAuth2LoginReqDto;
import org.springframework.http.ResponseEntity;

public interface AppAuthService {

    ResponseEntity<ApiResponse<?>> join(Long memberId, String nickName);

    ResponseEntity<ApiResponse<?>> login(OAuth2LoginReqDto oAuth2LoginReqDto);
}
