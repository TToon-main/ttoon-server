package com.server.ttoon.domain.attendance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;

@Data
@Builder
public class AttendanceDto {
    private DayOfWeek dayOfWeek;
    private Boolean status;
}
