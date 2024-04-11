package com.server.ttoon.domain.member.dto.request;

import lombok.Data;

@Data
public class AppAuthReqDto {
    private String providerId;
    private String provider;
}
