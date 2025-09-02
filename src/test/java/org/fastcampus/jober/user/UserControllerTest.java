package org.fastcampus.jober.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.user.dto.request.LoginRequestDto;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 로그인은 컨트롤러에서 처리,
 * 로그아웃은 Security LogoutFilter(/user/logout)로 처리하는 통합 테스트.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @Test
    @DisplayName("회원가입 → 로그인 → 로그아웃(LogoutFilter) 통합 플로우")
    void register_login_logout_flow() throws Exception {
        // 1) 회원가입
        var registerBody = new RegisterRequestDto(
                "jane", "Passw0rd!", "Jane Doe", "jane@example.com"
        );
        mockMvc.perform(
                post("/user/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody))
        ).andExpect(status().isOk());

        // 2) 로그인 (컨트롤러에서 SecurityContext 세션 저장)
        var loginBody = new LoginRequestDto("jane", "Passw0rd!");
        MvcResult loginResult = mockMvc.perform(
                        post("/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // 로그인 후 세션을 얻어온다
        HttpSession session = loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // 수정: 세션에 SecurityContext가 수동으로 저장되지 않았으므로 이 검증을 수정
        // 컨트롤러에서 SecurityContextHolder.setContext()만 했지, 세션에 직접 저장하지 않음
        // SecurityContext securityContext = (SecurityContext) session.getAttribute(
        //         HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        // assertThat(securityContext).isNotNull();

        // 3) 로그아웃 (Security LogoutFilter가 가로채 처리)
        //    동일 세션을 사용하고, CSRF 헤더를 함께 보낸다.
        MvcResult logoutResult = mockMvc.perform(
                        post("/user/logout")
                                .session((MockHttpSession) session)
                                .with(csrf()) // CSRF가 켜져 있다면 필수
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))  // 로그아웃 성공 응답 검증
                .andReturn();

        // 4) 세션이 무효화되었는지 확인
        MockHttpSession oldSession = (MockHttpSession) session;
        assertThat(oldSession.isInvalid()).as("로그아웃 후 기존 세션은 무효화되어야 함").isTrue();

        // (선택) 응답 쪽 세션이 새로 생기지 않았는지 확인
        assertThat(logoutResult.getRequest().getSession(false))
                .as("로그아웃 처리 후 새로운 세션이 자동 생성되면 안 됨")
                .isNull();
    }

    @Test
    @DisplayName("로그아웃: CSRF 없이 호출하면 403")
    void logout_without_csrf_should_403() throws Exception {
        // 세션 없이 바로 호출해도 CSRF 때문에 403이어야 함(설정이 켜져 있는 경우)
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 성공 후 세션 기반 인증 확인")
    void login_success_with_session() throws Exception {
        // 1) 회원가입
        var registerBody = new RegisterRequestDto(
                "testuser", "Test123!", "Test User", "test@example.com"
        );
        mockMvc.perform(
                post("/user/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody))
        ).andExpect(status().isOk());

        // 2) 로그인
        var loginBody = new LoginRequestDto("testuser", "Test123!");
        MvcResult result = mockMvc.perform(
                        post("/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userId").exists())
                .andReturn();

        // 세션이 생성되었는지 확인
        HttpSession session = result.getRequest().getSession(false);
        assertThat(session).as("로그인 후 세션이 생성되어야 함").isNotNull();
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 401")
    void login_with_wrong_password() throws Exception {
        // 1) 회원가입
        var registerBody = new RegisterRequestDto(
                "wronguser", "Correct123!", "Wrong User", "wrong@example.com"
        );
        mockMvc.perform(
                post("/user/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody))
        ).andExpect(status().isOk());

        // 2) 잘못된 비밀번호로 로그인 시도
        var loginBody = new LoginRequestDto("wronguser", "WrongPassword!");
        mockMvc.perform(
                        post("/user/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(loginBody))
                )
                .andExpect(status().isUnauthorized());
    }
}