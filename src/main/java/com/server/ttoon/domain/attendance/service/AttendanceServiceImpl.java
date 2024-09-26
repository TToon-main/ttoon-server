package com.server.ttoon.domain.attendance.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.attendance.entity.Attendance;
import com.server.ttoon.domain.attendance.repository.AttendanceRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.server.ttoon.common.response.status.ErrorStatus.ATTENDANCE_EXIST_ERROR;
import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> attendanceCheck(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        if(attendanceRepository.existsByMemberAndDayOfWeek(member,LocalDate.now().getDayOfWeek()))
            throw new CustomRuntimeException(ATTENDANCE_EXIST_ERROR);

        Attendance attendance = Attendance.builder()
                .member(member)
                .dayOfWeek(LocalDate.now().getDayOfWeek())
                .build();

        member.addAttendance(attendance);
        attendanceRepository.save(attendance);
        System.out.println((member.getAttendanceList().toString()));
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }
}
