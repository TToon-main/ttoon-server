package com.server.ttoon.common.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    _OK(HttpStatus.OK, "COMMON200", "요청에 성공하였습니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
