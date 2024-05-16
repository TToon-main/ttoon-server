package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.config.S3Service;
import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.dto.response.AccountResponseDto;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.jwt.dto.request.AuthorizationCodeDto;
import com.server.ttoon.security.jwt.dto.response.AppleAuthTokenResponse;
import com.server.ttoon.security.jwt.entity.RefreshToken;
import com.server.ttoon.security.jwt.repository.RefreshTokenRepository;
import com.server.ttoon.security.oauth.convertor.AppleProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;

import static com.server.ttoon.common.response.ApiResponse.*;
import static com.server.ttoon.common.response.status.ErrorStatus.BADREQUEST_ERROR;
import static com.server.ttoon.common.response.status.ErrorStatus.MEMBER_NOT_FOUND_ERREOR;
import static com.server.ttoon.common.response.status.SuccessStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;
    private final AppleProperties appleProperties;

    // 프로필 + 계정 정보 조회 메소드
    public ResponseEntity<ApiResponse<?>> getAccountInfo(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

        // 이미지의 presignedUrl 받아오기
        String image = member.getImage();
        String url = s3Service.getPresignedURL(image);


        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .nickName(member.getNickName())
                .email(member.getEmail())
                .imageUrl(url)
                .provider(member.getProvider())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK, accountResponseDto));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> modifyProfile(Long memberId, ModifyRequestDto modifyRequestDto, String newImage) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR));

        s3Service.deleteImage(member.getImage());

        member.updateNickName(modifyRequestDto.getNickName());

        member.updateImage(newImage);

        memberRepository.save(member);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> revoke(Long memberId, Optional<AuthorizationCodeDto> appleIdentityTokenDto, String sender) throws IOException {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERREOR));
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId.toString()).orElse(null);
        if(member.getProvider().equals(Provider.APPLE.toString()) && sender.equals("app"))
        {
            if(!appleIdentityTokenDto.isPresent())
                throw new CustomRuntimeException(BADREQUEST_ERROR);

            String code = appleIdentityTokenDto.get().getAuthorizationCode();
            AppleAuthTokenResponse appleAuthToken = generateAuthToken(code);
            appleServiceRevoke(appleAuthToken);
        }
        memberRepository.delete(member);
        if(refreshToken != null)
            refreshTokenRepository.delete(refreshToken);
        return ResponseEntity.ok(onSuccess(_OK));
    }

    private void appleServiceRevoke(AppleAuthTokenResponse appleAuthToken) throws IOException {
        if (appleAuthToken.getAccessToken() != null) {
            RestTemplate restTemplate = new RestTemplateBuilder().build();
            String revokeUrl = "https://appleid.apple.com/auth/revoke";

            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", appleProperties.getCid());
            params.add("client_secret", createClientSecret());
            params.add("token", appleAuthToken.getAccessToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
        }
    }

    private
    AppleAuthTokenResponse generateAuthToken(String code) throws IOException {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String authUrl = "https://appleid.apple.com/auth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", appleProperties.getCid());
        params.add("client_secret", createClientSecret());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<AppleAuthTokenResponse> response = restTemplate.postForEntity(authUrl, httpEntity, AppleAuthTokenResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Apple Auth Token Error");
        }
    }

    public PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(appleProperties.getPath());
        InputStream in = resource.getInputStream();
        PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPrivateKey(object);
    }

    public String createClientSecret() throws IOException {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", appleProperties.getKid());
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(appleProperties.getTid())
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5))) // 만료 시간
                .setAudience(appleProperties.getUrl())
                .setSubject(appleProperties.getCid())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }
}