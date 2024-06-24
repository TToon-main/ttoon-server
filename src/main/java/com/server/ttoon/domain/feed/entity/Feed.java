package com.server.ttoon.domain.feed.entity;

import com.server.ttoon.common.BaseEntity;
import com.server.ttoon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;
    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

}
