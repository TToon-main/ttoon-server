package com.server.ttoon.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ToonDto {
    private Long mainCharacterId;
    private List<Long> others;
    private int number;
    private String title;
    private String content;

    @Data
    @Builder
    public static class sendDto{
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class imageDto{
        private List<String> imageUrls;
    }

    @Data
    @Builder
    public static class toonResponseDto{
        private Long feedId;
        private List<String> imageUrls;
    }

}
