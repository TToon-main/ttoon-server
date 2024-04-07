package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository <Member, Long> {

}
