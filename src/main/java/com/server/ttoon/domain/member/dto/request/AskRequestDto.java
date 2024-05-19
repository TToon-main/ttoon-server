package com.server.ttoon.domain.member.dto.request;

import lombok.Data;

@Data
public class AskRequestDto {
    private String receiver;
    private String category;
    private String body;
}
