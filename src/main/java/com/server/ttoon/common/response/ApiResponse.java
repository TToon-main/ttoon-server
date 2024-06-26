package com.server.ttoon.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "data"}) // 변수 순서를 지정
public class ApiResponse<T> {
    private boolean isSuccess;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(SuccessStatus status, T result) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), result);
    }
    public static <T> ApiResponse<T> onSuccess(SuccessStatus status) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), null);
    }
    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(ErrorStatus status, T data) {
        return new ApiResponse<>(false, status.getCode(), status.getMessage(), data);
    }
    public static <T> ApiResponse<T> onFailure(ErrorStatus status) {
        return new ApiResponse<>(false, status.getCode(), status.getMessage(), null);
    }
    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }
    @JsonProperty("code")
    public String getCode() {
        return code;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}
