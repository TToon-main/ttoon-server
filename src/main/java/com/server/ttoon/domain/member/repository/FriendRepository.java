package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Friend;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Boolean existsByInvitorAndInvitee(Member invitor, Member invitee);
    Boolean existsByInviteeAndInvitor(Member invitee, Member invitor);
    Boolean existsByInviteeAndInvitorAndStatus(Member invitee, Member invitor, Status status);

    List<Friend> findAllByStatus(Status status);
    List<Friend> findAllByInviteeAndStatus(Member member, Status status);

    Page<Friend> findByInvitorAndStatusOrInviteeAndStatus(Member invitor, Status status1, Member invitee, Status status2, Pageable pageable);
    List<Friend> findByInvitorAndStatusOrInviteeAndStatus(Member invitor, Status status1, Member invitee, Status status2);

    List<Friend> findByInviteeAndStatus(Member member, Status status);

    Optional<Friend> findByInviteeAndInvitorAndStatus(Member invitee, Member invitor, Status status);
}
