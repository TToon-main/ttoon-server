package com.server.ttoon.domain.attendance.controller;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.attendance.service.AttendanceService;
import com.server.ttoon.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attendance API", description = "출석 관련 기능")
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 체크", description = "오늘에 해당하는 요일에 출석체크를 합니다.")
    @PostMapping("/attendance")
    public ResponseEntity<ApiResponse<?>> checkAttendance(){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return attendanceService.checkAttendance(memberId);
    }

    @Operation(summary = "출석 체크 조회", description = "이번 주 출석체크 현황을 조회합니다.")
    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<?>> getAttendance(){

        Long memberId = SecurityUtil.getCurrentMemberId();

        return attendanceService.getAttendance(memberId);
    }
}