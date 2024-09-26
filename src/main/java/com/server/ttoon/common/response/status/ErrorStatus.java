package com.server.ttoon.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    SERVER_MAINTENANCE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서버 점검중입니다."),
    BADREQUEST_ERROR(HttpStatus.BAD_REQUEST,"COMMON400_1", "잘못된 요청입니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "COMMON401", "해당 리소스에 유효한 인증 자격 증명이 필요합니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN,"COMMON403", "해당 리소스에 접근 권한이 없습니다."),
    MEMBER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_2", "사용자 정보를 찾을 수 없습니다."),
    FEED_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_3", "피드 정보를 찾을 수 없습니다."),
    BADREQUEST_LIKE_ERROR(HttpStatus.BAD_REQUEST,"COMMON400_4", "좋아요 관련 - 잘못된 요청입니다."),
    FRIEND_EXIST_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_5", "이미 친구이거나 둘 중 한명이 초대 요청을 보냈습니다."),
    NICKNAME_EXIST_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_6", "이미 존재하는 닉네임입니다."),
    FEED_EXIST_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_7", "해당일에 이미 피드가 존재합니다."),
    ATTENDANCE_EXIST_ERROR(HttpStatus.BAD_REQUEST, "COMMON400_8", "오늘 이미 출석체크 했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
