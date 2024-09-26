package com.server.ttoon.domain.attendance.entity;

import com.server.ttoon.common.BaseEntity;
import com.server.ttoon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
