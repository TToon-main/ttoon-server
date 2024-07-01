package com.server.ttoon.domain.feed.dto;

import com.server.ttoon.domain.feed.entity.Feed;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FeedDto {

    private Long feedId;
    private String title;
    private String content;
    private List<String> imageUrl;
    private LocalDateTime createdDate;


}
