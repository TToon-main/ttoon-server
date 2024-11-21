package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.domain.feed.dto.ToonDto;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class CheckUpdateTask implements Runnable{

    private final MemberService memberService;
    private final FeedService feedService;
    private final Long memberId;
    private final ToonDto toonDto;

    private final CompletableFuture<ResponseEntity<ApiResponse<?>>> completableFuture;



    public CheckUpdateTask(MemberService memberService, FeedService feedService, Long memberId, ToonDto toonDto, CompletableFuture<ResponseEntity<ApiResponse<?>>> completableFuture) {
        this.memberService = memberService;
        this.feedService = feedService;
        this.memberId = memberId;
        this.toonDto = toonDto;
        this.completableFuture = completableFuture;
    }


    @Override
    public void run() {
        try{
            Member member = memberService.findByMemberId(memberId);

            if(LocalDate.now().equals(member.getCreateToonDate())){
                throw new CustomRuntimeException(ErrorStatus.REQUEST_EXIST_ERROR);
            }

            completableFuture.complete(feedService.createToon(memberId, toonDto));
        } catch (CustomRuntimeException e) {
            completableFuture.completeExceptionally(e);
        }
    }
}
