package com.server.ttoon.domain.member.repository;


import com.server.ttoon.domain.member.entity.RevokeReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokeReasonRepository extends JpaRepository<RevokeReason, Long> {
}
