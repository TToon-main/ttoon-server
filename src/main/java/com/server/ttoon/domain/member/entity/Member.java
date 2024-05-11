package com.server.ttoon.domain.member.entity;

import com.server.ttoon.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String imageFileName;
    private String imageUrl;
    private int point;
    private String email;

    public void changeToUser(Member member){
        this.authority = Authority.ROLE_USER;
        this.provider = member.getProvider();
        this.providerId = member.getProviderId();
        this.nickName = member.getNickName();
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateImage(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void updateFileName(String fileName){
        this.imageFileName = fileName;
    }
}
