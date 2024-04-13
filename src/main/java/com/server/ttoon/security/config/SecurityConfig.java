package com.server.ttoon.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.dto.response.OAuth2LoginResDto;
import com.server.ttoon.security.jwt.dto.response.TokenDto;
import com.server.ttoon.security.jwt.entity.RefreshToken;
import com.server.ttoon.security.jwt.filter.JwtAccessDeniedHandler;
import com.server.ttoon.security.jwt.filter.JwtAuthenticationEntryPoint;
import com.server.ttoon.security.jwt.repository.RefreshTokenRepository;
import com.server.ttoon.security.oauth.PrincipalOauth2UserService;
import com.server.ttoon.security.oauth.convertor.AppleProperties;
import com.server.ttoon.security.oauth.convertor.CustomRequestEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleProperties appleProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(CustomRequestEntityConverter customRequestEntityConverter) {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(customRequestEntityConverter);

        return accessTokenResponseClient;
    }
    @Bean
    public CustomRequestEntityConverter customRequestEntityConverter() {
        return new CustomRequestEntityConverter(appleProperties);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .cors((co)->co.configurationSource(configurationSource()))
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth)->auth.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig.accessTokenResponseClient(accessTokenResponseClient(customRequestEntityConverter())))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService))
                        .successHandler(successHandler()))
                .exceptionHandling((auth)->
                        auth.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler))
                .with(new JwtSecurityConfig(tokenProvider), c-> c.getClass())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // PrincipalDetails로 캐스팅하여 인증된 사용자 정보를 가져온다.
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

            boolean isGuest = false;
            if(principal.getMember().getAuthority().equals(Authority.ROLE_GUEST))
                isGuest = true;

            // jwt token 발행을 시작한다.
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(principal.getUsername())
                    .value(tokenDto.getRefreshToken())
                    .build();
            RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(principal.getUsername()).orElse(null);
            if(existRefreshToken == null)
                refreshTokenRepository.save(refreshToken);
            else {
                existRefreshToken.updateValue(tokenDto.getRefreshToken());
                refreshTokenRepository.save(existRefreshToken);
            }

            OAuth2LoginResDto oAuth2LoginResDto = OAuth2LoginResDto.builder()
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .isGuest(isGuest)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse<OAuth2LoginResDto> apiResponse = ApiResponse.onSuccess(SuccessStatus._OK, oAuth2LoginResDto);
            // JSON 직렬화
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);

            // 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            // 응답 전송
            PrintWriter out = response.getWriter();
            out.println(jsonResponse);
            out.flush();
        };
    }
    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
