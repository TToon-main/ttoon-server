package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.MemberLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberLikesRepository extends JpaRepository<MemberLikes, Long> {

    Optional<MemberLikes> findByMemberAndFeed(Member member, Feed feed);

}
