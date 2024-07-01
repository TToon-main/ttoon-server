package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Slice<Feed> findAllByMember(Member member, Pageable pageable);

    Optional<Feed> findByCreatedAtAndMember(LocalDateTime createdAt, Member member);
}
