package com.server.ttoon.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

import static com.server.ttoon.common.response.status.ErrorStatus.*;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한이 없이 접근하려 할때 403 FORBIDDEN 응답 생성
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(FORBIDDEN_ERROR);

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