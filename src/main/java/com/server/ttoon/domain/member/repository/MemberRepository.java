package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository <Member, Long> {


   Member findByProviderAndProviderId(Provider provider, String providerId);
   List<Member> findByNickNameContainingIgnoreCase(String keyword);
   Optional<Member> findByNickName(String nickName);
}
