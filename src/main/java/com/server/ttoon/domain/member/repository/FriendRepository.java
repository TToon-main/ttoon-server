package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Friend;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Boolean existsByInvitorAndInvitee(Member invitor, Member invitee);
    Boolean existsByInviteeAndInvitor(Member invitee, Member invitor);

    List<Friend> findAllByStatus(Status status);
    List<Friend> findAllByInviteeAndStatus(Member member, Status status);
}
