package com.server.ttoon.security.util;


import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.security.auth.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil() { }

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static Long getCurrentMemberId() {

        final PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principalDetails.getMember() == null || principalDetails.getUsername() == null)
            throw new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR);

        return Long.parseLong(principalDetails.getUsername());
    }
}
