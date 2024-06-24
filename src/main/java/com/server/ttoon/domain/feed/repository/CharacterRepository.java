package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Character;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    List<Character> findAllByMember(Member member);
}
