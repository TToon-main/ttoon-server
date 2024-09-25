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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService{

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;
    @Override
    public ResponseEntity<ApiResponse<?>> getCalender(YearMonth yearMonth, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Feed> feedList = feedRepository.findAllByMemberAndDateBetween(member, startDate, endDate);

        List<FeedDto.homeFeedDto> homeFeedDtos = feedList.stream()
                .map(feed -> FeedDto.homeFeedDto.builder()
                        .feedId(feed.getId())
                        .thumbnail(feed.getThumbnail())
                        .createdDate(feed.getDate())
                        .build()
                ).toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, homeFeedDtos));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getOneFeed(LocalDate localDate, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

        if(localDate == null){
            localDate = LocalDate.now();
        }

        Feed feed = feedRepository.findByDateAndMember(localDate, member)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.FEED_NOT_FOUND_ERROR));

        List<FeedImage> feedImageList = feedImageRepository.findAllByFeed(feed);

        FeedDto feedDto = FeedDto.builder()
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrl(feedImageList.stream()
                        .map(FeedImage::getImageUrl).collect(Collectors.toList())
                )
                .createdDate(feed.getDate())
                .likes(feed.getLikes())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDto));
    }
}
