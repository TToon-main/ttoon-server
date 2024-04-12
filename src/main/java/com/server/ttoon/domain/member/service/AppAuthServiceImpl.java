package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.security.jwt.dto.request.OAuth2LoginReqDto;
import com.server.ttoon.domain.member.dto.response.AppJoinResDto;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.dto.response.OAuth2LoginResDto;
import com.server.ttoon.security.jwt.dto.response.TokenDto;
import com.server.ttoon.security.jwt.entity.RefreshToken;
import com.server.ttoon.security.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppAuthServiceImpl implements AppAuthService{

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 이용 약관 동의 후 회원가입 로직, 권한 ROLE_USER 로 변경
    @Transactional
    public ResponseEntity<ApiResponse<?>> join(Member member){

        // 권한 ROLE_USER 로 변경
        member.changeToUser();

        // authorities 추출.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

        // member 에 대한 principalDetails 만들어서 authentication 생성.
        PrincipalDetails memberDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

        // authentication 으로 Accesstoken, Refreshtoken 생성.
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberDetails.getUsername())
                .value(tokenDto.getRefreshToken())
                .build();

        RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(memberDetails.getUsername()).orElse(null);
        if(existRefreshToken == null)
            refreshTokenRepository.save(refreshToken);
        else
            existRefreshToken.updateValue(tokenDto.getRefreshToken());

        OAuth2LoginResDto oAuth2LoginResDto = OAuth2LoginResDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .isGuest(false)
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED, oAuth2LoginResDto));
    }

    // 로그인하기 버튼 눌렀을 때 실행. 이미 회원이면 토큰 생성 후 반환, 회원 아니면 멤버 GUEST로 저장 후 토큰 생성, 반환.
    @Transactional
    public ResponseEntity<ApiResponse<?>> login(OAuth2LoginReqDto oAuth2LoginReqDto){

        // provider 와 providerId 를 이용해 유저 찾기.
        String provider = oAuth2LoginReqDto.getProvider();
        String providerId = oAuth2LoginReqDto.getProviderId();
        Member member = memberRepository.findByProviderAndProviderId(Provider.valueOf(provider), providerId);

        // 회원이 아니면 권한 ROLE_GUEST 부여해서 디비 저장하고 반환.
        if(member == null){

             member = Member.builder()
                    .provider(Provider.valueOf(oAuth2LoginReqDto.getProvider()))
                    .providerId(oAuth2LoginReqDto.getProviderId())
                    .nickName(provider + "_" + providerId)
                    .authority(Authority.ROLE_GUEST)
                    .build();

            // 토큰 생성 로직
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

            PrincipalDetails memberDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(memberDetails.getUsername())
                    .value(tokenDto.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshToken);

            // isGuest = true -> 게스트 (아직 회원아님)
            OAuth2LoginResDto oAuth2LoginResDto = OAuth2LoginResDto.builder()
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .isGuest(true)
                    .build();

            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._CREATED, oAuth2LoginResDto));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getAuthority().toString()));

        PrincipalDetails memberDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "", authorities);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberDetails.getUsername())
                .value(tokenDto.getRefreshToken())
                .build();

        RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(memberDetails.getUsername()).orElse(null);
        if(existRefreshToken == null)
            refreshTokenRepository.save(refreshToken);
        else
            existRefreshToken.updateValue(tokenDto.getRefreshToken());

        OAuth2LoginResDto oAuth2LoginResDto = OAuth2LoginResDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .isGuest(false)
                .build();
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, oAuth2LoginResDto));
    }
}
