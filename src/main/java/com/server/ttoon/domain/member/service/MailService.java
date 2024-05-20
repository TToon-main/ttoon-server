package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.AskRequestDto;
import org.springframework.http.ResponseEntity;

public interface MailService {
    ResponseEntity<ApiResponse<?>> sendEmail(AskRequestDto askRequestDto);
}
