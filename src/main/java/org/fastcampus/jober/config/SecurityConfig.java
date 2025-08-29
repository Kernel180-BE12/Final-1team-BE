package org.fastcampus.jober.config;

import org.fastcampus.jober.filter.CsrfCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        return http
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/h2-console/**")
                ) // 세션 기반이므로 CSRF 활성화 (SPA에서는 쿠키 CSRF 토큰 사용)
                .addFilterAfter(new CsrfCookieFilter(),
                        org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class)
                // 2) H2 콘솔은 frame으로 열리므로 sameOrigin 필요
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/user/register",
                                "/user/login")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/h2-console/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 사용 안 함(우리는 JSON 엔드포인트 사용)
                .httpBasic(AbstractHttpConfigurer::disable) // 테스트용/간단 API에서는 유용하지만, 여기선 세션 기반을 쓰므로 꺼둠
                .logout(logout -> logout
                        .logoutUrl("/user/logout")  // POST /user/logout (CSRF 필요)
                        .deleteCookies("JSESSIONID")
                )
                .build();
    }
}
