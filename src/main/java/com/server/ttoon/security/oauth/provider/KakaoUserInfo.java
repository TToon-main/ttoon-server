package com.server.ttoon.security.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{
    private Map<String,Object> attributes;
    public KakaoUserInfo(Map<String,Object> attributes){
        this.attributes = attributes;
    }
    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }
}
