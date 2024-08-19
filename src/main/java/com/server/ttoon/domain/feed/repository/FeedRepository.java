package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    Slice<Feed> findAllByMember(Member member, Pageable pageable);

    @Query("SELECT e from Feed e where e.member = :member OR e.member IN :friends")
    Slice<Feed> findAllByMemberAndFriends(@Param("member") Member member, @Param("friends") List<Member> friends, Pageable pageable);

    @Query("SELECT e from Feed e where e.member = :member and function('DATE', e.createdAt) = :createdAt")
    Optional<Feed> findByCreatedAtAndMember(@Param("createdAt") LocalDate createdAt, @Param("member") Member member);

    @Query("SELECT e from Feed e WHERE e.member = :member And function('DATE_FORMAT', e.createdAt, '%Y-%m') LIKE :yearMonth")
    List<Feed> findAllByMemberAndCreatedAt(@Param("member") Member member, @Param("yearMonth") String yearMonth);
}
