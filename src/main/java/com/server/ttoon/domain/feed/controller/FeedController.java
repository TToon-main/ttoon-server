package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.service.FeedService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Tag(name = "Feed API", description = "피드 관련 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "피드 화면 조회", description = "피드 화면상의 데이터를 전달합니다.")
    @GetMapping("/feeds")
    public ResponseEntity<ApiResponse<?>> getFeeds(@RequestParam(name = "page", required = true) int page,
                                                   @RequestParam(name = "size", required = true) int size,
                                                   @RequestParam(name = "myFilter", required = true) Boolean myFilter
                                                   ){

        return feedService.getFeeds(page, size, myFilter);
    }

    @Operation(summary = "단일 피드 조회", description = "피드 하나의 데이터를 조회합니다.")
    @GetMapping("/feeds/{feedId}")
    public ResponseEntity<ApiResponse<?>> getOneFeed(@PathVariable("feedId") Long feedId) {

        return feedService.getOneFeed(feedId);
    }

    @Operation(summary = "좋아요 추가", description = "피드 좋아요를 추가합니다.")
    @PostMapping("/feeds/{feedId}")
    public ResponseEntity<ApiResponse<?>> addLike(@PathVariable("feedId") Long feedId){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return feedService.addLike(memberId, feedId);
    }
    @Operation(summary = "좋아요 취소", description = "피드 좋아요를 취소합니다.")
    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity<ApiResponse<?>> deleteLike(@PathVariable("feedId") Long feedId){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return feedService.addLike(memberId, feedId);
    }

    @Operation(summary = "등장인물 추가", description = "사용자의 기록 속 새로운 등장인물을 추가합니다.")
    @PostMapping("/character")
    public ResponseEntity<ApiResponse<?>> addFeedCharacter(@RequestBody AddCharacterDto addCharacterDto){

        return feedService.addFeedCharacter(addCharacterDto);
    }

    @Operation(summary = "등장인물 수정", description = "사용자의 기록 속 등장인물 정보를 수정합니다.")
    @PatchMapping("/character")
    public ResponseEntity<ApiResponse<?>> changeFeedCharacter(@RequestBody CharacterDto characterDto){

        return feedService.changeFeedCharacter(characterDto);
    }

    @Operation(summary = "등장인물 조회", description = "사용자의 기록 속 등장인물 정보들을 조회합니다.")
    @GetMapping("/character")
    public ResponseEntity<ApiResponse<?>> getFeedCharacters(){

        return feedService.getFeedCharacter();
    }

    @Operation(summary = "등장인물 삭제", description = "사용자의 기록 속 등장인물을 삭제합니다.")
    @DeleteMapping("/character/{characterId}")
    public ResponseEntity<ApiResponse<?>> deleteFeedCharacter(@PathVariable Long characterId){

        return feedService.deleteFeedCharacter(characterId);
    }
}
