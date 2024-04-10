package com.server.ttoon.domain.member.dto.request;

import com.server.ttoon.domain.member.entity.Provider;
import lombok.Data;

@Data
public class AppLoginReqDto {
    private Long providerId;
    private Provider provider;
}
