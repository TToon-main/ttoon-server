package com.server.ttoon.domain.member.dto.request;

import com.server.ttoon.domain.member.entity.Provider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppJoinReqDto {
    private Long providerId;
    private Provider provider;
    private String nickName;

}
