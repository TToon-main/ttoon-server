package com.server.ttoon.common.exception;

import com.server.ttoon.common.response.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomRuntimeException extends RuntimeException{

    private ErrorStatus status;
}
