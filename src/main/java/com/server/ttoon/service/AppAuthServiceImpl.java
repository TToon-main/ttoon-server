package com.server.ttoon.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.AppJoinReqDto;
import com.server.ttoon.domain.member.dto.request.AppLoginReqDto;
import com.server.ttoon.domain.member.dto.response.AppJoinResDto;
import com.server.ttoon.domain.member.dto.response.AppLoginResDto;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppAuthServiceImpl implements AppAuthService{

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public ResponseEntity<ApiResponse<?>> join(AppJoinReqDto appJoinReqDto){

        Member member = Member.builder()
                .nickName(appJoinReqDto.getNickName())
                .provider(appJoinReqDto.getProvider())
                .providerId(appJoinReqDto.getProviderId())
                .authority(Authority.ROLE_USER)
                .build();
        // 새로운 회원 저장.
        memberRepository.save(member);

        // authorities 추출.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

        // member 에 대한 principalDetails 만들어서 authentication 생성.
        PrincipalDetails memberDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

        // authentication 으로 Accesstoken, Refreshtoken 생성.
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        AppJoinResDto appJoinResDto = AppJoinResDto.builder()
                .tokenDto(tokenDto)
                .build();


        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED, appJoinResDto));
    }

    public ResponseEntity<ApiResponse<?>> login(AppLoginReqDto appLoginReqDto){

        // provider 와 providerId 를 이용해 유저 찾기.
        Provider provider = appLoginReqDto.getProvider();
        Long providerId = appLoginReqDto.getProviderId();
        Optional<Member> optionalMember = memberRepository.findByProviderAndProviderId(provider, providerId);

        AppLoginResDto appLoginResDto;

        // 리포지토리에 해당 유저가 존재하지 않으면 회원가입 메소드 진입.
        if(optionalMember.isEmpty()){
            // isExist = false -> 회원이 아니다.
           appLoginResDto = AppLoginResDto.builder()
                   .isExist(false)
                   .build();

           return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, appLoginResDto));
        }

        Member member = optionalMember.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

        PrincipalDetails memberDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        appLoginResDto = AppLoginResDto.builder()
                .isExist(true)
                .tokenDto(tokenDto)
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, appLoginResDto));
    }
}
