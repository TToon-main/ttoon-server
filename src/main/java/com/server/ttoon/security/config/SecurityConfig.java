package com.server.ttoon.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.entity.Authority;
import com.server.ttoon.security.auth.PrincipalDetails;
import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.dto.OAuth2LoginResponseDto;
import com.server.ttoon.security.jwt.dto.TokenDto;
import com.server.ttoon.security.jwt.filter.JwtAccessDeniedHandler;
import com.server.ttoon.security.jwt.filter.JwtAuthenticationEntryPoint;
import com.server.ttoon.security.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                        .anyRequest().permitAll()) //우선 모든요청 permitAll
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService)))
                .oauth2Login(handler -> handler.successHandler(successHandler()))
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

            OAuth2LoginResponseDto oAuth2LoginResponseDto = OAuth2LoginResponseDto.builder()
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .isGuest(isGuest)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse<OAuth2LoginResponseDto> apiResponse = ApiResponse.onSuccess(SuccessStatus._OK, oAuth2LoginResponseDto);
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
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
