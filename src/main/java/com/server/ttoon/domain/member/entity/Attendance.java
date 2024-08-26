package com.server.ttoon.domain.member.entity;

import com.server.ttoon.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private WeekDay weekDayOfWeek;

    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

}
