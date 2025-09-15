// package org.fastcampus.jober.user;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.fastcampus.jober.user.dto.request.LoginRequestDto;
// import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
// import org.fastcampus.jober.user.dto.request.UpdateRequestDto;
// import org.fastcampus.jober.user.dto.response.UserInfoResponseDto;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockHttpSession;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static
// org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
/// **
// * 회원정보 조회 및 수정 기능 테스트
// */
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// @ActiveProfiles("test")
// class UserControllerInfoTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired ObjectMapper objectMapper;
//
//    private String json(Object o) throws Exception {
//        return objectMapper.writeValueAsString(o);
//    }
//
//    private MockHttpSession session;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // 테스트용 사용자 등록 및 로그인
//        var registerBody = new RegisterRequestDto(
//                "testuser", "Passw0rd!", "테스트사용자", "test@example.com"
//        );
//
//        mockMvc.perform(
//                post("/user/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(registerBody))
//        ).andExpect(status().isOk());
//
//        // 로그인하여 세션 생성
//        var loginBody = new LoginRequestDto("testuser", "Passw0rd!");
//        MvcResult loginResult = mockMvc.perform(
//                post("/user/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(loginBody))
//        ).andExpect(status().isOk()).andReturn();
//
//        session = (MockHttpSession) loginResult.getRequest().getSession();
//    }
//
//    @Test
//    @DisplayName("회원정보 조회 성공")
//    void getUserInfo_success() throws Exception {
//        // when & then
//        MvcResult result = mockMvc.perform(
//                get("/user/info")
//                        .session(session)
//                        .with(csrf())
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.username").value("testuser"))
//        .andExpect(jsonPath("$.name").value("테스트사용자"))
//        .andExpect(jsonPath("$.email").value("test@example.com"))
//        .andReturn();
//
//        // 응답 데이터 검증
//        String responseBody = result.getResponse().getContentAsString();
//        UserInfoResponseDto response = objectMapper.readValue(responseBody,
// UserInfoResponseDto.class);
//
//        assertThat(response.getUsername()).isEqualTo("testuser");
//        assertThat(response.getName()).isEqualTo("테스트사용자");
//        assertThat(response.getEmail()).isEqualTo("test@example.com");
//    }
//
//    @Test
//    @DisplayName("인증되지 않은 사용자의 회원정보 조회 실패")
//    void getUserInfo_unauthorized() throws Exception {
//        // when & then
//        mockMvc.perform(
//                get("/user/info")
//                        .with(csrf())
//        )
//        .andExpect(status().isForbidden());
//    }
//
//
//    @Test
//    @DisplayName("name 수정 성공")
//    void updateUserInfo_name_success() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto("새로운이름", null);
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isOk());
//
//        // 수정된 정보 확인
//        mockMvc.perform(
//                get("/user/info")
//                        .session(session)
//                        .with(csrf())
//        )
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.username").value("testuser"))
//        .andExpect(jsonPath("$.name").value("새로운이름"))
//        .andExpect(jsonPath("$.email").value("test@example.com"));
//    }
//
//    @Test
//    @DisplayName("email 수정 성공")
//    void updateUserInfo_email_success() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto(null, "newemail@example.com");
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isOk());
//
//        // 수정된 정보 확인
//        mockMvc.perform(
//                get("/user/info")
//                        .session(session)
//                        .with(csrf())
//        )
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.username").value("testuser"))
//        .andExpect(jsonPath("$.name").value("테스트사용자"))
//        .andExpect(jsonPath("$.email").value("newemail@example.com"));
//    }
//
//
//
//
//    @Test
//    @DisplayName("현재와 동일한 name으로 수정 시 성공")
//    void updateUserInfo_sameName_success() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto("테스트사용자", null);
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isNoContent()); // 204 No Content (동일한 값이므로 변경사항 없음)
//    }
//
//    @Test
//    @DisplayName("현재와 동일한 email로 수정 시 성공")
//    void updateUserInfo_sameEmail_success() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto(null, "test@example.com");
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isNoContent()); // 204 No Content (동일한 값이므로 변경사항 없음)
//    }
//
//    @Test
//    @DisplayName("인증되지 않은 사용자의 회원정보 수정 실패")
//    void updateUserInfo_unauthorized() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto("새로운이름", null);
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("빈 요청으로 수정 시 변경사항 없음")
//    void updateUserInfo_emptyRequest_noChange() throws Exception {
//        // given
//        var updateBody = new UpdateRequestDto(null, null);
//
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(updateBody))
//        )
//        .andExpect(status().isNoContent()); // 204 No Content
//    }
//
//    @Test
//    @DisplayName("잘못된 JSON 형식으로 수정 요청 시 실패")
//    void updateUserInfo_invalidJson_fail() throws Exception {
//        // when & then
//        mockMvc.perform(
//                put("/user/update")
//                        .session(session)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ invalid json }")
//        )
//        .andExpect(status().isInternalServerError());
//    }
// }
