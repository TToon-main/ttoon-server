package com.server.ttoon.domain.attendance.repository;

import com.server.ttoon.domain.attendance.entity.Attendance;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Boolean existsByMemberAndDayOfWeek(Member member, DayOfWeek dayOfWeek);
}
