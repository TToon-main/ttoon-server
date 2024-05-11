package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public ResponseEntity<ApiResponse<?>> getAccountInfo(){

        return null;
    }
}
