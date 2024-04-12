package com.server.ttoon.security.jwt.service;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.dto.response.OAuth2LoginResDto;
import com.server.ttoon.security.jwt.dto.response.TokenDto;
import com.server.ttoon.security.jwt.entity.RefreshToken;
import com.server.ttoon.security.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.server.ttoon.common.response.status.ErrorStatus.BADREQUEST_ERROR;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtServiceImpl implements JwtService{
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public ResponseEntity<ApiResponse<?>> reissue(String accessToken, String refreshToken){
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken))
            throw new CustomRuntimeException(BADREQUEST_ERROR);

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();

        String memberId = principalDetails.getUsername();
        RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(memberId).get();
        if(existRefreshToken == null)
            throw new CustomRuntimeException(BADREQUEST_ERROR);
        // 3. DB에 매핑 되어있는 Member ID(key)와 Vaule값이 같지않으면 에러 리턴
        if(!refreshToken.equals(existRefreshToken.getValue()))
            throw new CustomRuntimeException(BADREQUEST_ERROR);

        // 4. Vaule값이 같다면 토큰 재발급 진행
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        OAuth2LoginResDto oAuth2LoginResDto = OAuth2LoginResDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(refreshToken)
                .isGuest(principalDetails.getMember().getAuthority().equals(Authority.ROLE_GUEST)?true:false)
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, oAuth2LoginResDto));
    }
}
