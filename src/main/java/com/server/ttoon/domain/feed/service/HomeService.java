package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.response.ApiResponse;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;


public interface HomeService {

    ResponseEntity<ApiResponse<?>> getCallender(YearMonth yearMonth, Long memberId);

    ResponseEntity<ApiResponse<?>> getOneFeed(LocalDate localDate, Long memberId);
}
