package com.server.ttoon.domain.member.entity;

import com.server.ttoon.common.BaseEntity;
import com.server.ttoon.domain.attendance.entity.Attendance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Authority authority;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerId;
    private String nickName;
    private String image;
    private int point;
    private String email;


    public void changeToUser(Member member){
        this.authority = Authority.ROLE_USER;
        this.provider = member.getProvider();
        this.providerId = member.getProviderId();
        this.nickName = member.getNickName();
        this.email = member.getEmail();
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateImage(String image){
        this.image = image;
    }
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendanceList = new ArrayList<>();

    public void addAttendance(Attendance attendance) {
        this.attendanceList.add(attendance);
        attendance.setMember(this);  // 양방향 관계 설정
    }
}
