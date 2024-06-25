package com.server.ttoon.domain.feed.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CharacterDto {
    private Long id;
    private String name;
    private String info;
}
