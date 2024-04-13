package com.server.ttoon.security.oauth.convertor;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import com.server.ttoon.common.exception.CustomRuntimeException;
import com.server.ttoon.common.response.status.ErrorStatus;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;
    private final String path;
    private final String keyId;
    private final String teamId;
    private final String clientId;
    private final String url;

    public CustomRequestEntityConverter(AppleProperties properties) {
        this.defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        this.path = properties.getPath();
        this.keyId = properties.getKid();
        this.teamId = properties.getTid();
        this.clientId = properties.getCid();
        this.url = properties.getUrl();
    }
    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);
        String registrationId = req.getClientRegistration().getRegistrationId();
        MultiValueMap<String, String> params = (MultiValueMap<String, String>) entity.getBody();

        if (registrationId.contains("apple")) {
            try {
                params.set("client_secret", createClientSecret());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new RequestEntity<>(params, entity.getHeaders(),
                entity.getMethod(), entity.getUrl());
    }
    public PrivateKey getPrivateKey() throws IOException {

        ClassPathResource resource = new ClassPathResource(path);

        try {
            InputStream in = resource.getInputStream();
            PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPrivateKey(object);
            // 파일 처리 로직
        } catch (FileNotFoundException e) {
            // 파일이 없을 경우의 처리 로직
            throw new CustomRuntimeException(ErrorStatus.MEMBER_NOT_FOUND_ERREOR);
        }
    }

    public String createClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(expirationDate) // 만료 시간
                .setAudience(url)
                .setSubject(clientId)
                .signWith(getPrivateKey())
                .compact();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(OAuth2AuthorizationCodeGrantRequest.class);
    }
    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(RequestEntity.class);
    }
}
