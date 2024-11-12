package com.server.ttoon.domain.feed.service;

import com.server.ttoon.domain.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class RequestQueueProcessor {

    private final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
    private final MemberService memberService;

    public void addTask(Long memberId) throws InterruptedException {
        CheckUpdateTask task = new CheckUpdateTask(memberService, memberId);
        blockingQueue.put(task);
    }

    @PostConstruct
    public void startProcessing(){
        Thread processThread = new Thread(() -> {
            try{
                while(true){
                    Runnable request = blockingQueue.take();
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
