package com.server.ttoon.domain.attendance.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.attendance.dto.AttendanceDto;
import com.server.ttoon.domain.attendance.dto.AttendanceResponseDto;
import com.server.ttoon.domain.attendance.entity.Attendance;
import com.server.ttoon.domain.attendance.repository.AttendanceRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.ttoon.common.response.status.ErrorStatus.ATTENDANCE_EXIST_ERROR;
import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;

    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void clearAttendanceTable() {
        List<Attendance> attendances = attendanceRepository.findAll();

        for (Attendance attendance : attendances) {
            Member member = attendance.getMember();
            if (member != null) {
                member.getAttendanceList().remove(attendance);
            }
        }
        attendanceRepository.deleteAll();
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> checkAttendance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        if(attendanceRepository.existsByMemberAndDayOfWeek(member,LocalDate.now().getDayOfWeek()))
            throw new CustomRuntimeException(ATTENDANCE_EXIST_ERROR);

        Attendance attendance = Attendance.builder()
                .member(member)
                .dayOfWeek(LocalDate.now().getDayOfWeek())
                .build();

        member.addAttendance(attendance);
        member.addPoint();
        attendanceRepository.save(attendance);
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAttendance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        // 모든 요일을 가져오기
        List<DayOfWeek> allDaysOfWeek = List.of(DayOfWeek.values());

        // 각 요일에 대한 출석 정보를 변환
        List<AttendanceDto> attendanceDtoList = allDaysOfWeek.stream()
                .map(dayOfWeek -> {
                    // 해당 요일에 대한 Attendance가 있는지 확인
                    Attendance attendance = member.getAttendanceList().stream()
                            .filter(a -> a.getDayOfWeek() == dayOfWeek)
                            .findFirst()
                            .orElse(null);

                    // Attendance가 존재하면 status를 true로, 없으면 false
                    Boolean status = (attendance != null);

                    return AttendanceDto.builder()
                            .dayOfWeek(dayOfWeek)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        // AttendanceResponseDto 생성
        AttendanceResponseDto responseDto = AttendanceResponseDto.builder()
                .point(member.getPoint())
                .attendanceDtoList(attendanceDtoList)
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, responseDto));
    }
}
