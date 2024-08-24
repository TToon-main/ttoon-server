package com.server.ttoon.domain.member.entity;

import com.server.ttoon.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitor_member_id")
    private Member invitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_member_id")
    private Member invitee;

    public void changeStatus(Status status){
        this.status = status;
    }

}
