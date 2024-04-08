package com.server.ttoon.security.config;

import com.server.ttoon.security.jwt.TokenProvider;
import com.server.ttoon.security.jwt.filter.JwtAccessDeniedHandler;
import com.server.ttoon.security.jwt.filter.JwtAuthenticationEntryPoint;
import com.server.ttoon.security.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            // 사용자 이메일을 가져온다.
            String email = oAuth2User.getAttribute("email");
            // 서비스 제공 플랫폼(GOOGLE, KAKAO, NAVER)이 어디인지 가져온다.
            String provider = oAuth2User.getAttribute("provider");

            // CustomOAuth2UserService에서 셋팅한 로그인한 회원 존재 여부를 가져온다.
            boolean isExist = oAuth2User.getAttribute("exist");
            // OAuth2User로 부터 Role을 얻어온다.
            String role = oAuth2User.getAuthorities().stream()
                    .findFirst() // 첫번째 Role을 찾아온다.
                    .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                    .getAuthority(); // Role을 가져온다.

            // jwt token 발행을 시작한다.
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
