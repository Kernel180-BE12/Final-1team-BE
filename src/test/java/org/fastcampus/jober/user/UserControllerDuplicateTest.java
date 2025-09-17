//package org.fastcampus.jober.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.fastcampus.jober.user.dto.request.CheckEmailRequestDto;
//import org.fastcampus.jober.user.dto.request.CheckIdRequestDto;
//import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * 사용자 중복체크 API 테스트
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//class UserControllerDuplicateTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired ObjectMapper objectMapper;
//
//    private String json(Object o) throws Exception {
//        return objectMapper.writeValueAsString(o);
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // 테스트용 사용자 등록
//        var registerBody = new RegisterRequestDto(
//                "existinguser", "Passw0rd!", "기존사용자", "existing@example.com"
//        );
//
//        mockMvc.perform(
//                post("/user/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(registerBody))
//        ).andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("아이디 중복체크 - 사용 가능한 아이디")
//    void checkId_available() throws Exception {
//        // given
//        var checkIdRequest = new CheckIdRequestDto("newuser");
//
//        // when & then
//        mockMvc.perform(
//                post("/user/id/check")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(checkIdRequest))
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().string(""));
//    }
//
//    @Test
//    @DisplayName("아이디 중복체크 - 이미 존재하는 아이디")
//    void checkId_duplicate() throws Exception {
//        // given
//        var checkIdRequest = new CheckIdRequestDto("existinguser");
//
//        // when & then
//        mockMvc.perform(
//                post("/user/id/check")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(checkIdRequest))
//        )
//        .andExpect(status().isBadRequest())
//        .andExpect(jsonPath("$.message").value("이미 존재하는 아이디입니다."));
//    }
//
//
//    @Test
//    @DisplayName("이메일 중복체크 - 사용 가능한 이메일")
//    void checkEmail_available() throws Exception {
//        // given
//        var checkEmailRequest = new CheckEmailRequestDto("new@example.com");
//
//        // when & then
//        mockMvc.perform(
//                post("/user/email/check")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(checkEmailRequest))
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().string(""));
//    }
//
//    @Test
//    @DisplayName("이메일 중복체크 - 이미 존재하는 이메일")
//    void checkEmail_duplicate() throws Exception {
//        // given
//        var checkEmailRequest = new CheckEmailRequestDto("existing@example.com");
//
//        // when & then
//        mockMvc.perform(
//                post("/user/email/check")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json(checkEmailRequest))
//        )
//        .andExpect(status().isBadRequest())
//        .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));
//    }
//
//}
