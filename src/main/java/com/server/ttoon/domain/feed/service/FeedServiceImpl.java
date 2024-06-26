package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.dto.FeedDto;
import com.server.ttoon.domain.feed.entity.Character;
import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.entity.FeedImage;
import com.server.ttoon.domain.feed.repository.CharacterRepository;
import com.server.ttoon.domain.feed.repository.FeedImageRepository;
import com.server.ttoon.domain.feed.repository.FeedRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.util.SecurityUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.ttoon.common.response.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedServiceImpl implements FeedService{

    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> addFeedCharacter(AddCharacterDto addCharacterDto) {

        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));

        Character character = Character.builder()
                .name(addCharacterDto.getName())
                .info(addCharacterDto.getInfo())
                .member(member)
                .build();

        characterRepository.save(character);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> changeFeedCharacter(CharacterDto characterDto) {

        Character character = characterRepository.findById(characterDto.getId())
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));

        character.updateCharacter(characterDto.getName(), characterDto.getInfo());

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getFeedCharacter(){

        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));


        List<Character> characters = characterRepository.findAllByMember(member);

        List<CharacterDto> characterDtos = new ArrayList<>();
        for(Character character:characters){
            characterDtos.add(character.toCharacterDto());
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, characterDtos));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteFeedCharacter(Long characterId) {

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));

        characterRepository.delete(character);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    // 피드 화면 조회
    @Override
    public ResponseEntity<ApiResponse<?>> getFeeds(int page, int size) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));

        // 페이지 번호, 사이즈 지정해서 Pageable 객체 생성.
        Pageable pageable = PageRequest.of(page, size);

        Slice<Feed> feedSlice = feedRepository.findAllByMember(member, pageable);

        // feedSlice 를 DTO 타입 리스트로 변환하기
        List<FeedDto> feedDtoList = feedSlice.stream()
                .map(feed -> FeedDto.builder()
                        .feedId(feed.getId())
                        .title(feed.getTitle())
                        .imageUrl(feed.getFeedImageList().stream()
                                .map(FeedImage::getImageUrl).collect(Collectors.toList()))
                        .createdDate(feed.getCreatedAt())
                        .build()
                )
                .toList();

        // 오늘 쓴 피드 있다면 리스트에 추가하기
        Optional<Feed> feedOptional = feedRepository.findByCreatedAtAndMember(LocalDateTime.now(), member);

        if(feedOptional.isPresent()){
            Feed feed = feedOptional.get();

            FeedDto feedDto = FeedDto.builder()
                    .feedId(feed.getId())
                    .title(feed.getTitle())
                    .content(feed.getContent())
                    .imageUrl(feed.getFeedImageList().stream()
                            .map(FeedImage::getImageUrl).collect(Collectors.toList())
                    )
                    .createdDate(feed.getCreatedAt())
                    .build();

            feedDtoList.add(feedDto);
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDtoList));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getOneFeed(Long feedId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomRuntimeException(FEED_NOT_FOUND_ERROR));

        List<FeedImage> feedImageList = feedImageRepository.findAllByFeed(feed);

        FeedDto feedDto = FeedDto.builder()
                .title(feed.getTitle())
                .content(feed.getContent())
                .imageUrl(feedImageList.stream()
                        .map(FeedImage::getImageUrl).collect(Collectors.toList())
                )
                .createdDate(feed.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDto));
    }
}
