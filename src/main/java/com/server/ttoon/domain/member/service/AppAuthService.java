package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.AppJoinReqDto;
import com.server.ttoon.domain.member.dto.request.AppLoginReqDto;
import org.springframework.http.ResponseEntity;

public interface AppAuthService {

    ResponseEntity<ApiResponse<?>> join(AppJoinReqDto appJoinReqDto);

    ResponseEntity<ApiResponse<?>> login(AppLoginReqDto appLoginReqDto);
}
