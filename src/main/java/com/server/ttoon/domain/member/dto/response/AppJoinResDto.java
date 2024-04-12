package com.server.ttoon.domain.member.dto.response;

import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.security.jwt.dto.TokenDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppJoinResDto {

    private TokenDto tokenDto;

}
