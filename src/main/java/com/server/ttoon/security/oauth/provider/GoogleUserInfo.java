package com.server.ttoon.security.oauth.provider;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{
    private Map<String,Object> attributes;
    public GoogleUserInfo(Map<String,Object> attributes){
        this.attributes = attributes;
    }
    @Override
    public String getProviderId() {
        return (String)attributes.get("sub");
    }
    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
