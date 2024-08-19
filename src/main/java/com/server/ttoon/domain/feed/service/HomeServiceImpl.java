package com.server.ttoon.domain.feed.service;

import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.feed.dto.FeedDto;
import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.entity.FeedImage;
import com.server.ttoon.domain.feed.repository.FeedImageRepository;
import com.server.ttoon.domain.feed.repository.FeedRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService{

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;
    @Override
    public ResponseEntity<ApiResponse<?>> getCallender(YearMonth yearMonth, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

        List<Feed> feedList = feedRepository.findAllByMemberAndAndCreatedAt(member, yearMonth.toString());

        List<FeedDto.homeFeedDto> homeFeedDtos = feedList.stream()
                .map(feed -> FeedDto.homeFeedDto.builder()
                        .feedId(feed.getId())
                        .thumbnail(feed.getThumbnail())
                        .createdDate(feed.getCreatedAt())
                        .build()
                ).toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, homeFeedDtos));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getOneFeed(LocalDate localDate, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

        Feed feed = feedRepository.findByCreatedAtAndMember(localDate, member)
                .orElseThrow(() -> new ResourceNotFoundException("해당 날짜: " + localDate + " 에 올린 피드가 없습니다."));

        List<FeedImage> feedImageList = feedImageRepository.findAllByFeed(feed);

        FeedDto feedDto = FeedDto.builder()
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrl(feedImageList.stream()
                        .map(FeedImage::getImageUrl).collect(Collectors.toList())
                )
                .createdDate(feed.getCreatedAt())
                .likes(feed.getLikes())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDto));
    }
}
