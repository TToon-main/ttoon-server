package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.entity.FeedImage;
import com.server.ttoon.domain.feed.repository.FeedImageRepository;
import com.server.ttoon.domain.feed.repository.FeedRepository;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Component
//@RequiredArgsConstructor
public class FeedInitializer {

//    private final FeedRepository feedRepository;
//    private final MemberRepository memberRepository;
//    private final FeedImageRepository feedImageRepository;
//
//    @PostConstruct
//    public void feedInit(){
//
//        Member member1 = new Member();
//        Member member2 = new Member();
//        Member member3 = new Member();
//
//        member1 = Member.builder()
//                .nickName("1번유저")
//                .provider(Provider.KAKAO)
//                .email("11")
//                .authority(Authority.ROLE_USER)
//                .providerId("11111")
//                .build();
//
//        member2 = Member.builder()
//                .nickName("2번유저")
//                .provider(Provider.GOOGLE)
//                .email("22")
//                .authority(Authority.ROLE_USER)
//                .providerId("22222")
//                .build();
//
//        member3 = Member.builder()
//                .nickName("3번유저")
//                .provider(Provider.KAKAO)
//                .email("33")
//                .authority(Authority.ROLE_USER)
//                .providerId("33333")
//                .build();
//
//        List<Member> memberList = new ArrayList<>();
//
//        memberList.add(member1);
//        memberList.add(member2);
//        memberList.add(member3);
//
//        memberRepository.saveAll(memberList);
//
//        List<FeedImage> imageList = new ArrayList<>();
//        int idx = 0;
//
//        for (Member member : memberList) {
//
//            Feed sampleFeed = Feed.builder()
//                    .title("샘플-제목 : " + idx)
//                    .content("샘플-내용 : " + idx + " 번째 테스트용 피드입니다.")
//                    .member(member)
//                    .likes(0)
//                    .feedImageList(imageList)
//                    .build();
//
//            feedRepository.save(sampleFeed);
//
//            FeedImage feedImage1 = FeedImage.builder()
//                    .imageUrl("20240825_scenario0_0.png")
//                    .feed(sampleFeed)
//                    .isFirst(true)
//                    .build();
//            FeedImage feedImage2 = FeedImage.builder()
//                    .imageUrl("20240825_scenario1_0.png")
//                    .feed(sampleFeed)
//                    .isFirst(false)
//                    .build();
//            FeedImage feedImage3 = FeedImage.builder()
//                    .imageUrl("20240825_scenario2_0.png")
//                    .feed(sampleFeed)
//                    .isFirst(false)
//                    .build();
//            FeedImage feedImage4 = FeedImage.builder()
//                    .imageUrl("20240825_scenario3_0.png")
//                    .feed(sampleFeed)
//                    .isFirst(false)
//                    .build();
//
//            feedImageRepository.save(feedImage1);
//            feedImageRepository.save(feedImage2);
//            feedImageRepository.save(feedImage3);
//            feedImageRepository.save(feedImage4);
//
//            imageList.add(feedImage1);
//            imageList.add(feedImage2);
//            imageList.add(feedImage3);
//            imageList.add(feedImage4);
//
//            sampleFeed.setFeedImageList(imageList);
//
//            System.out.println("샘플 데이터 저장완료 : " + idx);
//
//            imageList.clear();
//            idx++;
//        }

//        List<Feed> feedList = feedRepository.findAll();
//        feedRepository.deleteAll(feedList);
//        List<FeedImage> feedImageList = feedImageRepository.findAll();
//        feedImageRepository.deleteAll(feedImageList);
//    }
}