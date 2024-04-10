package com.server.ttoon.domain.member.repository;

import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository <Member, Long> {

    Optional<Member> findByProviderAndProviderId(Provider provider, Long providerId);
}
