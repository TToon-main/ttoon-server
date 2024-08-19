package com.server.ttoon.domain.feed.service;

import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.dto.FeedDto;
import com.server.ttoon.domain.feed.entity.Figure;
import com.server.ttoon.domain.feed.entity.Feed;
import com.server.ttoon.domain.feed.entity.FeedImage;
import com.server.ttoon.domain.feed.repository.FigureRepository;
import com.server.ttoon.domain.feed.repository.FeedImageRepository;
import com.server.ttoon.domain.feed.repository.FeedRepository;
import com.server.ttoon.domain.member.entity.Friend;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.MemberLikes;
import com.server.ttoon.domain.member.entity.Status;
import com.server.ttoon.domain.member.repository.FriendRepository;
import com.server.ttoon.domain.member.repository.MemberLikesRepository;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
    private final FigureRepository figureRepository;
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;
    private final MemberLikesRepository memberLikesRepository;
    private final FriendRepository friendRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> addFeedCharacter(AddCharacterDto addCharacterDto) {

        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        Figure figure = Figure.builder()
                .name(addCharacterDto.getName())
                .info(addCharacterDto.getInfo())
                .member(member)
                .build();

        figureRepository.save(figure);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(SuccessStatus._CREATED));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> changeFeedCharacter(CharacterDto characterDto) {

        Figure figure = figureRepository.findById(characterDto.getId())
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        figure.updateCharacter(characterDto.getName(), characterDto.getInfo());

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getFeedCharacter(){

        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));


        List<Figure> figures = figureRepository.findAllByMember(member);

        List<CharacterDto> characterDtos = new ArrayList<>();
        for(Figure figure : figures){
            characterDtos.add(figure.toCharacterDto());
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, characterDtos));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteFeedCharacter(Long characterId) {

        Figure figure = figureRepository.findById(characterId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        figureRepository.delete(figure);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    // 피드 화면 조회
    @Override
    public ResponseEntity<ApiResponse<?>> getFeeds(int page, int size, Boolean myFilter) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        // 페이지 번호, 사이즈 지정해서 Pageable 객체 생성.
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 나만 보기 bool 값 1일때
        if(myFilter){

            List<Member> friendList = new ArrayList<>();

            // status 수락 상태인 친구테이블의 튜플들 가져오기
            List<Friend> acceptList = friendRepository.findAllByStatus(Status.ACCEPT);

            List<Feed> feedList = null;

            // 튜플들 중, member 와 친구인 튜플들만 뽑고, 친구의 피드들을 feedList 에 추가.
            for (Friend friend : acceptList) {
                Long friendId;
                if(friend.getInvitor().getId() == memberId){
                    friendId = friend.getInvitee().getId();
                }
                else if(friend.getInvitee().getId() == memberId){
                    friendId = friend.getInvitor().getId();
                }
                else{
                    friendId = null;
                    continue;
                }

                Member friendMember = memberRepository.findById(friendId)
                        .orElseThrow(() -> new ResourceNotFoundException("memberId: " + friendId + " 에 해당하는 친구인 유저가 존재하지 않습니다."));

                friendList.add(friendMember);
            }

            Slice<Feed> feedSlice = feedRepository.findAllByMemberAndFriends(member, friendList, pageable);

            // feedSlice 를 DTO 타입 리스트로 변환하기
            List<FeedDto> feedDtoList = feedSlice.stream()
                    .map(feed -> FeedDto.builder()
                            .feedId(feed.getId())
                            .title(feed.getTitle())
                            .imageUrl(feed.getFeedImageList().stream()
                                    .map(FeedImage::getImageUrl).collect(Collectors.toList()))
                            .createdDate(feed.getCreatedAt())
                            .likes(feed.getLikes())
                            .build()
                    )
                    .toList();

            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDtoList));
        }
        else{ // 나만 보기 bool 값 0일 때

            Slice<Feed> feedSlice = feedRepository.findAllByMember(member, pageable);

            // feedSlice 를 DTO 타입 리스트로 변환하기
            List<FeedDto> feedDtoList = feedSlice.stream()
                    .map(feed -> FeedDto.builder()
                            .feedId(feed.getId())
                            .title(feed.getTitle())
                            .imageUrl(feed.getFeedImageList().stream()
                                    .map(FeedImage::getImageUrl).collect(Collectors.toList()))
                            .createdDate(feed.getCreatedAt())
                            .likes(feed.getLikes())
                            .build()
                    )
                    .toList();

            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feedDtoList));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> addLike(Long memberId, Long feedId){

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomRuntimeException(FEED_NOT_FOUND_ERROR));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        // 유저가 이미 좋아요한 피드인지 확인.
        Optional<MemberLikes> optionalMemberLikes = memberLikesRepository.findByMemberAndFeed(member, feed);

        if(optionalMemberLikes.isPresent()){
            return ResponseEntity.ok(ApiResponse.onFailure(BADREQUEST_LIKE_ERROR));
        }

        // 피드 좋아요 개수 업데이트.
        feed.updateLike(feed.getLikes()+1);

        feedRepository.save(feed);

        // 유저가 좋아요한 피드 레포지토리에 저장
        MemberLikes memberLikes = MemberLikes.builder()
                .member(member)
                .feed(feed)
                .build();

        memberLikesRepository.save(memberLikes);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feed.getLikes()));
    }

    @Override
    public ResponseEntity<ApiResponse<?>> deleteLike(Long memberId, Long feedId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomRuntimeException(FEED_NOT_FOUND_ERROR));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        // 유저가 좋아요했던 피드인지 확인
        Optional<MemberLikes> optionalMemberLikes = memberLikesRepository.findByMemberAndFeed(member, feed);

        if(optionalMemberLikes.isEmpty()){
            return ResponseEntity.ok(ApiResponse.onFailure(BADREQUEST_LIKE_ERROR));
        }

        // 피드 좋아요 개수 업데이트.
        feed.updateLike(feed.getLikes()-1);

        feedRepository.save(feed);

        // 유저가 좋아요 취소한 피드 레포지토리에서 삭제
        MemberLikes memberLikes = optionalMemberLikes.get();

        memberLikesRepository.delete(memberLikes);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, feed.getLikes()));
    }
}
