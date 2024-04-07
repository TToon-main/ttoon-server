package com.server.ttoon.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

import static com.server.ttoon.common.response.status.ErrorStatus.*;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401 Unauthorized 응답 생성
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(UNAUTHORIZED_ERROR);

        // JSON 직렬화
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        // 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 응답 전송
        PrintWriter out = response.getWriter();
        out.println(jsonResponse);
        out.flush();
    }
}
