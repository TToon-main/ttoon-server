package com.server.ttoon.domain.feed.entity;

import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.member.entity.Member;
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
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void updateCharacter(String name, String info){
        this.name = name;
        this.info = info;
    }

    public CharacterDto toCharacterDto() {

        return CharacterDto.builder()
                .id(id)
                .name(name)
                .info(info)
                .build();
    }
}
