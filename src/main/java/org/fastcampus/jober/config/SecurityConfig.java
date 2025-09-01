package org.fastcampus.jober.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // 세션 모니터링용
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http.csrf(csrf -> csrf
                        .csrfTokenRequestHandler(requestHandler)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/h2-console/**", "/admin/sessions")
                ) // 세션 기반이므로 CSRF 활성화 (SPA에서는 쿠키 CSRF 토큰 사용)
                .addFilterAfter(new CsrfCookieFilter(),
                        BasicAuthenticationFilter.class)
                // 2) H2 콘솔은 frame으로 열리므로 sameOrigin 필요
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/user/register",
                                "/user/login")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/h2-console/**",
                                "/swagger-ui.html", "/admin/sessions")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 사용 안 함(우리는 JSON 엔드포인트 사용)
                .httpBasic(AbstractHttpConfigurer::disable) // 테스트용/간단 API에서는 유용하지만, 여기선 세션 기반을 쓰므로 꺼둠
                .logout(l -> l
                        .logoutUrl("/user/logout")     // POST
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler((_, res, _) -> {
                            res.setContentType("application/json");
                            res.setStatus(200);
                            res.getWriter().write("{\"success\":true}");
                        })
                )
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
}
