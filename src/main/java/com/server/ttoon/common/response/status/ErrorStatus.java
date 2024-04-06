package com.server.ttoon.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    SERVER_MAINTENANCE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서버 점검중입니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
