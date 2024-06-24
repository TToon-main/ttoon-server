package com.server.ttoon.domain.feed.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.feed.dto.AddCharacterDto;
import com.server.ttoon.domain.feed.dto.CharacterDto;
import com.server.ttoon.domain.feed.entity.Character;
import com.server.ttoon.domain.feed.repository.CharacterRepository;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.server.ttoon.common.response.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedServiceImpl implements FeedService{
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;

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


}
