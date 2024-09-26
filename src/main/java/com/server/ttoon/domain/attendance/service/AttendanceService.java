package com.server.ttoon.domain.attendance.service;

import com.server.ttoon.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AttendanceService {
    ResponseEntity<ApiResponse<?>> checkAttendance(Long memberId);

    ResponseEntity<ApiResponse<?>> getAttendance(Long memberId);
}
