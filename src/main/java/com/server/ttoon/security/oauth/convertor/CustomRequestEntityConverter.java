package com.server.ttoon.security.oauth.convertor;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import io.jsonwebtoken.Jwts;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

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

@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>>, org.springframework.core.convert.converter.Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;
    @Value("${apple.key-path}")
    String path;
    @Value("${apple.key-id}")
    String keyId;
    @Value("${apple.team-id}")
    String teamId;
    @Value("${apple.client-id}")
    String clientId;
    @Value("${apple.url}")
    String url;

    public CustomRequestEntityConverter() {
        defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
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

        System.out.println("===================================================");
        System.out.println("====================================================");
        System.out.println("path = " + path);
        System.out.println("===================================================");
        System.out.println("====================================================");
        ClassPathResource resource = new ClassPathResource(path);

        InputStream in = resource.getInputStream();
        PEMParser pemParser = new PEMParser(new StringReader(IOUtils.toString(in, StandardCharsets.UTF_8)));
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPrivateKey(object);
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
