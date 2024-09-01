package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.config.S3Service;
import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.ErrorStatus;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.domain.member.dto.response.AccountResponseDto;
import com.server.ttoon.domain.member.dto.response.FriendInfoDto;
import com.server.ttoon.domain.member.entity.*;
import com.server.ttoon.domain.member.repository.FriendRepository;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.domain.member.repository.RevokeReasonRepository;
import com.server.ttoon.security.jwt.dto.request.AuthorizationCodeDto;
import com.server.ttoon.security.jwt.dto.response.AppleAuthTokenResponse;
import com.server.ttoon.security.jwt.entity.RefreshToken;
import com.server.ttoon.security.jwt.repository.RefreshTokenRepository;
import com.server.ttoon.security.oauth.convertor.AppleProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
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
import static com.server.ttoon.common.response.status.ErrorStatus.*;
import static com.server.ttoon.common.response.status.SuccessStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokeReasonRepository revokeReasonRepository;
    private final S3Service s3Service;
    private final AppleProperties appleProperties;
    private final FriendRepository friendRepository;

    // 프로필 + 계정 정보 조회 메소드
    public ResponseEntity<ApiResponse<?>> getAccountInfo(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

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
    public ResponseEntity<ApiResponse<?>> modifyProfile(Long memberId, String nickName, String newImage, Boolean isDelete) {

        Optional<Member> memberOptional = memberRepository.findByNickName(nickName);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERROR));

        if(memberOptional.isPresent() && memberOptional.get() != member){
            throw new CustomRuntimeException(NICKNAME_EXIST_ERROR);
        }

        if(nickName != null){
            if(!nickName.isBlank()){
                member.updateNickName(nickName);
            }
        }

        if(newImage != null){
            if(member.getImage() != null){
                if(!member.getImage().isBlank()){
                    s3Service.deleteImage(member.getImage());
                }
            }
            member.updateImage(newImage);
        }
        else{
            if(isDelete){
                if(member.getImage() != null) {
                    if (!member.getImage().isBlank()) {
                        s3Service.deleteImage(member.getImage());
                    }
                }
                member.updateImage(null);
            }
        }

        memberRepository.save(member);

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> revoke(Long memberId, Optional<AuthorizationCodeDto> appleIdentityTokenDto, String sender) throws IOException {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId.toString()).orElse(null);

        if(appleIdentityTokenDto.isEmpty())
            throw new CustomRuntimeException(BADREQUEST_ERROR);

        RevokeReason revokeReason = RevokeReason.builder()
                .userName(member.getNickName())
                .reason(appleIdentityTokenDto.get().getRevokeReason())
                .build();

        revokeReasonRepository.save(revokeReason);

        if(member.getProvider().equals(Provider.APPLE.toString()) && sender.equals("app"))
        {
            if(appleIdentityTokenDto.get().getAuthorizationCode().isEmpty())
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

    @Transactional
    public ResponseEntity<ApiResponse<?>> addFriend(Long memberId, String nickName){
        Member invitor = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));
        Member invitee = memberRepository.findByNickName(nickName).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));

        if(friendRepository.existsByInviteeAndInvitor(invitee,invitor) || friendRepository.existsByInvitorAndInvitee(invitee,invitor))
            throw new CustomRuntimeException(FRIEND_EXIST_ERROR);

        Friend friend = Friend.builder()
                .invitee(invitee)
                .invitor(invitor)
                .status(Status.WAITING)
                .build();
        friendRepository.save(friend);
        return ResponseEntity.ok(onSuccess(_OK));
    }
    @Transactional
    public ResponseEntity<ApiResponse<?>> acceptInvite(Long friendId){
        Friend friend = friendRepository.findById(friendId).get();
        friend.changeStatus(Status.ACCEPT);
        friendRepository.save(friend);

        return ResponseEntity.ok(onSuccess(_OK));
    }

    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteFriend(Long friendId){
        Friend friend = friendRepository.findById(friendId).get();
        friendRepository.delete(friend);

        return ResponseEntity.ok(onSuccess(_OK));
    }

    public ResponseEntity<ApiResponse<?>> getFriends(Long memberId, Pageable pageable){
        List<FriendInfoDto> friendInfoDtoList = new ArrayList<>();

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));
        // 내가 초대자이면서 상태가 accept인거 혹은 내가 초대받은 자이면서 상태가 accept인거 페이징 적용해서 레포지토리에서 찾음
        List<Friend> friendList = friendRepository.findByInvitorAndStatusOrInviteeAndStatus(member,Status.ACCEPT,member,Status.ACCEPT,pageable);
        for(Friend friend : friendList){
            Long findId;
            // 만약 friend의 Invitee와 현재 접속중인 memberId가 같다면 findId는 invitorId가 되어야함
            if(friend.getInvitee().getId() == memberId)
                findId = friend.getInvitor().getId();
            // 아니라면 findId는 invitee가 됨
            else
                findId = friend.getInvitee().getId();

            Member findMember = memberRepository.findById(findId).orElseThrow(() -> new CustomRuntimeException(MEMBER_NOT_FOUND_ERROR));
            String image = findMember.getImage();
            String url = s3Service.getPresignedURL(image);
            FriendInfoDto friendInfoDto = FriendInfoDto.builder()
                    .friendId(friend.getId())
                    .profileUrl(url)
                    .nickName(findMember.getNickName())
                    .build();
            friendInfoDtoList.add(friendInfoDto);
        }

        return ResponseEntity.ok(onSuccess(_OK,friendInfoDtoList));
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