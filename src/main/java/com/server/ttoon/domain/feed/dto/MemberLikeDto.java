package com.server.ttoon.domain.feed.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberLikeDto {

    private String userName;
    private String userImage;

    @Data
    @Builder
    public static class LikeDto{
        private int like;
    }
}
