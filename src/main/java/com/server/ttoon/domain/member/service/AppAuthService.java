package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.AppAuthReqDto;
import org.springframework.http.ResponseEntity;

public interface AppAuthService {

    ResponseEntity<ApiResponse<?>> join(AppAuthReqDto appAuthReqDto);

    ResponseEntity<ApiResponse<?>> login(AppAuthReqDto appAuthReqDto);
}
