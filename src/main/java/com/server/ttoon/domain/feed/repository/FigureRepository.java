package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Figure;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Long> {

    List<Figure> findAllByMember(Member member);
}
