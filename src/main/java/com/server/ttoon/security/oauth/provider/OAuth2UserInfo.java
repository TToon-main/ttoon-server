package com.server.ttoon.security.oauth.provider;

import java.util.Map;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    Map<String, Object> getAttributes();
}
