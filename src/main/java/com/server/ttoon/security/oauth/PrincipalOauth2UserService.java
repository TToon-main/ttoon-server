package com.server.ttoon.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.oauth.provider.AppleUserInfo;
import com.server.ttoon.security.oauth.provider.GoogleUserInfo;
import com.server.ttoon.security.oauth.provider.KakaoUserInfo;
import com.server.ttoon.security.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserInfo oAuth2UserInfo = null;
        OAuth2User oAuth2User;

        if(userRequest.getClientRegistration().getRegistrationId().equals("apple"))
        {
            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            oAuth2UserInfo = new AppleUserInfo(decodeJwtTokenPayload(idToken));
        }
        else {
            oAuth2User = super.loadUser(userRequest);
            if (userRequest.getClientRegistration().getRegistrationId().equals("google"))
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao"))
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }
        String provider = oAuth2UserInfo.getProvider().toUpperCase();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();

        Member member = memberRepository.findByProviderAndProviderId(Provider.valueOf(provider),providerId);

        if(member == null)
        {
            String authority = "ROLE_GUEST";
            String nickName = provider + "_" + providerId;
            member = Member.builder()
                    .authority(Authority.valueOf(authority))
                    .nickName(nickName)
                    .provider(Provider.valueOf(provider))
                    .providerId(providerId)
                    .email(email)
                    .build();
            memberRepository.save(member);
            return new PrincipalDetails(member,oAuth2UserInfo.getAttributes());
        }
        return new PrincipalDetails(member, oAuth2UserInfo.getAttributes());
    }


    public Map<String, Object> decodeJwtTokenPayload(String jwtToken) {
        Map<String, Object> jwtClaims = new HashMap<>();
        try {
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(decodedString, Map.class);
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
//        logger.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
        }
        return jwtClaims;
    }
}
