package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.service.HomeService;
import com.server.ttoon.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home/callender")
    public ResponseEntity<ApiResponse<?>> getCallender(@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return homeService.getCallender(yearMonth, memberId);
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<?>> getOneFeed(@RequestParam("date") LocalDate date){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return homeService.getOneFeed(date, memberId);
    }
}
