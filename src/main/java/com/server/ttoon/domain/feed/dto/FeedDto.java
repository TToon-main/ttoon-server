package com.server.ttoon.domain.feed.dto;

import com.server.ttoon.domain.feed.entity.Feed;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FeedDto {

    private Long feedId;
    private String writerName;
    private String writerImage;
    private String title;
    private String content;
    private List<String> imageUrl;
    private LocalDate createdDate;
    private int likes;
    private Boolean isMine;
    private Boolean likeOrNot;


    @Data
    @Builder
    public static class homeFeedDto{
        private Long feedId;
        private String thumbnail;
        private LocalDate createdDate;
    }

    @Data
    @Builder
    public static class FeedIdDto{
        private Long feedId;
    }

}
