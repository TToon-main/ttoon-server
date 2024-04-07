package com.server.ttoon.common.exception.handler;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomRuntimeException(CustomRuntimeException ex) {
        ErrorStatus errorStatus = ex.getStatus();
        ApiResponse<Object> response = ApiResponse.onFailure(errorStatus);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(response);
    }
}
