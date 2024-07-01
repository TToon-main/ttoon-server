package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FeedService {
    ResponseEntity<ApiResponse<?>> addFeedCharacter(AddCharacterDto addCharacterDto);
    ResponseEntity<ApiResponse<?>> changeFeedCharacter(CharacterDto characterDto);
    ResponseEntity<ApiResponse<?>> getFeedCharacter();
    ResponseEntity<ApiResponse<?>> deleteFeedCharacter(Long characterId);
    ResponseEntity<ApiResponse<?>> getFeeds(int page, int size);
    ResponseEntity<ApiResponse<?>> getOneFeed(Long feedId);
}
