package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.service.MemberService;

import java.time.LocalDate;

public class CheckUpdateTask implements Runnable{

    private final MemberService memberService;
    private final Long memberId;

    public CheckUpdateTask(MemberService memberService, Long memberId) {
        this.memberService = memberService;
        this.memberId = memberId;
    }


    @Override
    public void run() {
        Member member = memberService.findByMemberId(memberId);

        if(LocalDate.now().equals(member.getCreateToonDate())){
            throw new CustomRuntimeException(ErrorStatus.REQUEST_EXIST_ERROR);
        }

        memberService.updateCreateToonDate(member);
    }
}
