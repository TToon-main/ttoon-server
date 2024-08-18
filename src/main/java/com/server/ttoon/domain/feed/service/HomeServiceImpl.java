package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.feed.dto.FeedDto;
import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.repository.FeedRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService{

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    @Override
    public ResponseEntity<ApiResponse<?>> getCallender(YearMonth yearMonth, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

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
}
