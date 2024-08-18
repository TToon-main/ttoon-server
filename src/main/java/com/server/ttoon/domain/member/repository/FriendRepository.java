package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Friend;
import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Boolean existsByInvitorAndInvitee(Member invitor, Member invitee);
    Boolean existsByInviteeAndInvitor(Member invitee, Member invitor);
}
