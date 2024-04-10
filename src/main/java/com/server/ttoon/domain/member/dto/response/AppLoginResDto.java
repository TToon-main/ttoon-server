package com.server.ttoon.domain.member.dto.response;

import com.server.ttoon.security.jwt.dto.TokenDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppLoginResDto {

    private TokenDto tokenDto;
    private Boolean isExist; // 회원인지 아닌지
}

