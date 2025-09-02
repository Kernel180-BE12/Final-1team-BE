package org.fastcampus.jober.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.common.SecurityProps;
import org.fastcampus.jober.filter.CsrfCookieFilter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final SecurityProps props;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // 세션 모니터링 + 관리용
    @Bean
    public SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider); // parent 미지정 (null)
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var conf = new CorsConfiguration();
        // ★ 배포 환경에선 와일드카드(*) 대신 '정확한 오리진'만 허용
        conf.setAllowedOrigins(List.of("http://localhost:3000", "https://your-frontend.com"));
        conf.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        conf.setAllowedHeaders(List.of("Content-Type","Authorization","X-XSRF-TOKEN"));
        conf.setAllowCredentials(true); // 쿠키/인증 포함 요청이면 필수
        // 필요 시 응답 헤더 노출
        conf.setExposedHeaders(List.of("Location"));
        // 프리플라이트 캐시 시간
        conf.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }

    @Bean
    OpenApiCustomizer logoutPathDoc() {
        return openApi -> {
            var pathItem = new PathItem().post(
                    new Operation()
                            .addTagsItem("User")
                            .summary("로그아웃")
                            .description("Security LogoutFilter가 처리합니다.")
                            .addParametersItem(new HeaderParameter()
                                    .name("X-XSRF-TOKEN").required(false).description("CSRF 보호용 토큰(쿠키와 동일값)")
                                    .schema(new StringSchema()))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse().description("성공"))
                                    .addApiResponse("403", new ApiResponse().description("CSRF 미설정/불일치")))
            );
            openApi.path("/user/logout", pathItem);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .cors(_ -> {})
                .csrf(csrf -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/h2-console/**", "/admin/sessions/**")
                ) // 세션 기반이므로 CSRF 활성화 (SPA에서는 쿠키 CSRF 토큰 사용)
                .addFilterAfter(new CsrfCookieFilter(),
                        BasicAuthenticationFilter.class)
                // 2) H2 콘솔은 frame으로 열리므로 sameOrigin 필요
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(props.getPermitAll().toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, props.getPermitPostUser().toArray(String[]::new)).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 사용 안 함(우리는 JSON 엔드포인트 사용)
                .httpBasic(AbstractHttpConfigurer::disable) // 테스트용/간단 API에서는 유용하지만, 여기선 세션 기반을 쓰므로 꺼둠
                .logout(l -> l
                        .logoutUrl("/user/logout")     // POST
                        // 1) 세션레지스트리에서 제거
                        .addLogoutHandler((request, response, authentication) -> {
                            var session = request.getSession(false);
                            if (session != null) {
                                sessionRegistry().removeSessionInformation(session.getId());
                            }
                        })
                        // 2) 표준 처리
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        // 3) JSON 응답
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setContentType("application/json");
                            res.setStatus(200);
                            res.getWriter().write("{\"success\":true}");
                        }))
                .sessionManagement(session -> session
                        // 필요시 세션 정책도 지정:
                        // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(-1)               // 제한 없음
                                .sessionRegistry(sessionRegistry()) // SessionRegistry 빈 사용
                        )
                );

        return http.build();
    }
}
