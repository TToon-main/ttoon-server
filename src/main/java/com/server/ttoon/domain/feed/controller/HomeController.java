package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.service.HomeService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@Tag(name = "Home API", description = "홈 관련 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 캘린더 조회", description = "연-월 을 받은 후, 그 달에 해당하는 피드를 모두 반환합니다.")
    @GetMapping("/home/calendar")
    public ResponseEntity<ApiResponse<?>> getCalendar(@RequestParam(name = "yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return homeService.getCalender(yearMonth, memberId);
    }

    @Operation(summary = "홈 화면 단일 피드 조회", description = "홈 화면 처음 접속시 혹은 홈 화면의 날짜를 클릭했을 때, 날짜에 해당하는 피드를 반환합니다.")
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<?>> getOneFeed(@RequestParam(name = "date", required = false) LocalDate date){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return homeService.getOneFeed(date, memberId);
    }
}
