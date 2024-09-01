package com.server.ttoon.domain.feed.controller;

import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.entity.FeedImage;
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

@Component
@RequiredArgsConstructor
public class FeedInitializer {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void feedInit(){

//        List<Member> memberList = memberRepository.findAll();
        List<Member> memberList = new ArrayList<>();

        Member member1 = Member.builder()
                .nickName("asdf")
                .provider(Provider.KAKAO)
                .providerId("1234")
                .authority(Authority.ROLE_USER)
                .email("as563df@naver.com")
                .build();

        Member member2 = Member.builder()
                .nickName("asdfdd")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .authority(Authority.ROLE_USER)
                .email("asd3134f@naver.com")
                .build();

        Member member3 = Member.builder()
                .nickName("asdfgg")
                .provider(Provider.KAKAO)
                .providerId("1234565")
                .authority(Authority.ROLE_USER)
                .email("as222df@naver.com")
                .build();

        memberList.add(member1);
        memberList.add(member2);
        memberList.add(member3);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<FeedImage> imageList = new ArrayList<>();
        int idx = 1;

        for (Member member : memberList) {

            if (member.getAuthority() != Authority.ROLE_USER) {
                continue;
            }

            Feed sampleFeed = new Feed();
            FeedImage feedImage1 = FeedImage.builder()
                    .imageUrl("20240825_scenario0_0.png")
                    .feed(sampleFeed)
                    .isFirst(true)
                    .build();
            FeedImage feedImage2 = FeedImage.builder()
                    .imageUrl("20240825_scenario1_0.png")
                    .feed(sampleFeed)
                    .isFirst(false)
                    .build();
            FeedImage feedImage3 = FeedImage.builder()
                    .imageUrl("20240825_scenario2_0.png")
                    .feed(sampleFeed)
                    .isFirst(false)
                    .build();
            FeedImage feedImage4 = FeedImage.builder()
                    .imageUrl("20240825_scenario3_0.png")
                    .feed(sampleFeed)
                    .isFirst(false)
                    .build();

            imageList.add(feedImage1);
            imageList.add(feedImage2);
            imageList.add(feedImage3);
            imageList.add(feedImage4);

            Feed feed = Feed.builder()
                    .id(sampleFeed.getId())
                    .title("샘플-제목 : " + idx)
                    .content("샘플-내용 : " + idx + " 번째 테스트용 피드입니다.")
                    .member(member)
                    .likes(1)
                    .number(0)
                    .feedImageList(imageList)
                    .build();

            feedRepository.save(feed);
            System.out.println("샘플 데이터 저장완료 : " + idx);

            imageList.clear();
            idx++;
        }
    }
}
