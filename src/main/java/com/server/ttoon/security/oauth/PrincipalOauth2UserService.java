package com.server.ttoon.security.oauth;

import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.domain.member.entity.Member;
import com.server.ttoon.domain.member.entity.Provider;
import com.server.ttoon.domain.member.repository.MemberRepository;
import com.server.ttoon.security.auth.PrincipalDetails;
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

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("=======================================================================-=====");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google"))
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao"))
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        String provider = oAuth2UserInfo.getProvider().toUpperCase();
        String providerId = oAuth2UserInfo.getProviderId();

        Member member = memberRepository.findByProviderAndProviderId(provider,providerId);

        if(member == null)
        {
            String authority = "ROLE_GUEST";
            String nickName = provider + "_" + providerId;
            member = Member.builder()
                    .authority(Authority.valueOf(authority))
                    .nickName(nickName)
                    .provider(Provider.valueOf(provider))
                    .providerId(providerId)
                    .build();
            memberRepository.save(member);
            return new PrincipalDetails(member,oAuth2User.getAttributes());
        }
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
