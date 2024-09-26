package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.dto.ToonDto;
import org.apache.coyote.Response;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedService {
    ResponseEntity<ApiResponse<?>> addFeedCharacter(AddCharacterDto addCharacterDto);
    ResponseEntity<ApiResponse<?>> changeFeedCharacter(CharacterDto characterDto);
    ResponseEntity<ApiResponse<?>> getFeedCharacter();
    ResponseEntity<ApiResponse<?>> deleteFeedCharacter(Long characterId);
    ResponseEntity<ApiResponse<?>> getFeeds(int page, int size, Boolean filter);

    ResponseEntity<ApiResponse<?>> deleteFeed(Long feedId);

    ResponseEntity<ApiResponse<?>> addLike(Long memberId, Long feedId);

    ResponseEntity<ApiResponse<?>> deleteLike(Long memberId, Long feedId);

    ResponseEntity<ApiResponse<?>> testToon(Long memberId, List<String> images, String title, String content, LocalDate date);

    ResponseEntity<ApiResponse<?>> createToon(Long memberId, ToonDto toonDto);

    //ResponseEntity<ApiResponse<?>> createToonTest(ToonDto toonDto);

    ResponseEntity<ApiResponse<?>> completeToon(Long feedId, ToonDto.imageDto imageDto);
}
