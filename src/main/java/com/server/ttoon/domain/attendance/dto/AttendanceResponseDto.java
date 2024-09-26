package com.server.ttoon.domain.attendance.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AttendanceResponseDto {
    private int point;
    private List<AttendanceDto> attendanceDtoList;
}
