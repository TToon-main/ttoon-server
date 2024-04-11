package com.server.ttoon.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    SERVER_MAINTENANCE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서버 점검중입니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "COMMON401", "해당 리소스에 유효한 인증 자격 증명이 필요합니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN,"COMMON403", "해당 리소스에 접근 권한이 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "사용자 정보를 찾을 수 없습니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
