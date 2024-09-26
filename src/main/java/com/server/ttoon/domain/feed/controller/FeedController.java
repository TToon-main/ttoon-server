package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.common.config.S3Service;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.dto.ToonDto;
import com.server.ttoon.domain.feed.service.FeedService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Feed API", description = "피드 관련 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final S3Service s3Service;

    @Operation(summary = "피드 화면 조회", description = "피드 화면상의 데이터를 전달합니다.")
    @GetMapping("/feeds")
    public ResponseEntity<ApiResponse<?>> getFeeds(@RequestParam(name = "page", required = true) int page,
                                                   @RequestParam(name = "size", required = true) int size,
                                                   @RequestParam(name = "onlyMine", required = true) Boolean onlyMine
                                                   ){

        return feedService.getFeeds(page, size, onlyMine);
    }

    @Operation(summary = "피드 삭제", description = "해당 피드를 삭제합니다.")
    @DeleteMapping("/delete/{feedId}")
    public ResponseEntity<ApiResponse<?>> deleteFeed(@PathVariable("feedId") Long feedId){

        return feedService.deleteFeed(feedId);
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

        return feedService.deleteLike(memberId, feedId);
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

    @Operation(summary = "테스트용 피드 생성", description = "테스트용 피드를 생성합니다.")
    @PostMapping(value = "/test/toon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> testToon(@RequestPart("files") MultipartFile[] files,
                                                   @RequestPart("title") String title,
                                                   @RequestPart("content") String content,
                                                   @RequestPart("date") LocalDate date) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();

        if (files.length != 4) {
            throw new IllegalArgumentException("Exactly 4 images are required");
        }

        List<String> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String image = s3Service.saveFile(file, "test");
            images.add(image);
        }
        return feedService.testToon(memberId, images, title, content, date);
    }
//    에러 생기거나, 나중에 ai 테스트 할때 사용할 테스트용 api.
//    @Operation(summary = "기록 추가(웹툰 생성) 테스트용1233445", description = "테스트용~!~!@~!@~!@~!ㄸ#@!#$%$#@!~")
//    @PostMapping(value = "/toon/test")
//    public ResponseEntity<ApiResponse<?>> createToonTest(@RequestBody ToonDto toonDto){
//
//        return feedService.createToonTest(toonDto);
//    }

    @Operation(summary = "기록 추가(웹툰 생성)", description = "기록 추가 화면에서 완료 버튼 클릭 시, 요청하는 API.")
    @PostMapping(value = "/toon")
    public ResponseEntity<ApiResponse<?>> createToon(@RequestBody ToonDto toonDto){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return feedService.createToon(memberId, toonDto);
    }

    @Operation(summary = "기록 추가(이미지 선택 완료)", description = "사용자가 4개의 컷 모두 선택 완료했을 때 요청하는 API.")
    @PostMapping(value = "/toon/complete/{feedId}")
    public ResponseEntity<ApiResponse<?>> completeToon(@PathVariable Long feedId,
                                                       @RequestBody ToonDto.imageDto imageDto
    ){

        return feedService.completeToon(feedId, imageDto);
    }


}
