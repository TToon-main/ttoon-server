package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.feed.dto.ToonDto;
import com.server.ttoon.domain.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class RequestQueueProcessor {

    private final BlockingQueue<CheckUpdateTask> blockingQueue = new LinkedBlockingQueue<>();
    private final FeedService feedService;
    private final MemberService memberService;

    public ResponseEntity<ApiResponse<?>> addTask(Long memberId, ToonDto toonDto) throws InterruptedException, ExecutionException {
        CompletableFuture<ResponseEntity<ApiResponse<?>>> completableFuture = new CompletableFuture<>();
        CheckUpdateTask task = new CheckUpdateTask(memberService, feedService, memberId, toonDto, completableFuture);
        blockingQueue.put(task);
        return completableFuture.get();
    }

    @PostConstruct
    public void startProcessing(){
        Thread processThread = new Thread(() -> {
            try{
                while(true){
                    CheckUpdateTask request = blockingQueue.take();
                    request.run();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        processThread.setDaemon(true);
        processThread.start();
    }

}
