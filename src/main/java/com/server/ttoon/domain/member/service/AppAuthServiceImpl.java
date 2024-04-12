package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.AppAuthReqDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppAuthServiceImpl implements AppAuthService{

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    // 이용 약관 동의 후 회원가입 로직, 권한 ROLE_USER 로 변경
    public ResponseEntity<ApiResponse<?>> join(AppAuthReqDto appAuthReqDto){

        String provider = appAuthReqDto.getProvider();
        String providerId = appAuthReqDto.getProviderId();
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId);

        if(member == null){
            return ResponseEntity.ok(ApiResponse.onFailure(ErrorStatus.MEMBER_NOT_FOUND));
        }

        // 권한 ROLE_USER 로 변경
        member = Member.builder()
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

    // 로그인하기 버튼 눌렀을 때 실행. 이미 회원이면 토큰 생성 후 반환, 회원 아니면 멤버 GUEST로 저장 후 토큰 생성, 반환.
    public ResponseEntity<ApiResponse<?>> login(AppAuthReqDto appAuthReqDto){

        // provider 와 providerId 를 이용해 유저 찾기.
        String provider = appAuthReqDto.getProvider();
        String providerId = appAuthReqDto.getProviderId();
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId);

        AppLoginResDto appLoginResDto;

        // 회원이 아니면 권한 ROLE_GUEST 부여해서 디비 저장하고 반환.
        if(member == null){

            Member newMember = Member.builder()
                    .provider(Provider.valueOf(appAuthReqDto.getProvider()))
                    .providerId(appAuthReqDto.getProviderId())
                    .nickName("고양이")
                    .authority(Authority.ROLE_GUEST)
                    .build();

            // 토큰 생성 로직
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

            PrincipalDetails memberDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // isGuest = true -> 게스트 (아직 회원아님)
            appLoginResDto = AppLoginResDto.builder()
                    .tokenDto(tokenDto)
                    .isGuest(true)
                    .build();


            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, appLoginResDto));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

        PrincipalDetails memberDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        appLoginResDto = AppLoginResDto.builder()
                .isGuest(false)
                .tokenDto(tokenDto)
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, appLoginResDto));
    }
}
