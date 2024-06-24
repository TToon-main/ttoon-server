package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import org.springframework.http.ResponseEntity;

public interface FeedService {
    ResponseEntity<ApiResponse<?>> addFeedCharacter(AddCharacterDto addCharacterDto);
    ResponseEntity<ApiResponse<?>> changeFeedCharacter(CharacterDto characterDto);
    ResponseEntity<ApiResponse<?>> getFeedCharacter();
    ResponseEntity<ApiResponse<?>> deleteFeedCharacter(Long characterId);
}
