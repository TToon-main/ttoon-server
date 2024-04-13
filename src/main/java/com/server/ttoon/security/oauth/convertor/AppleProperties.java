package com.server.ttoon.security.oauth.convertor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "apple")
@Getter
@Setter
public class AppleProperties {
    private String path;
    private String url;
    private String cid;
    private String tid;
    private String kid;
}
