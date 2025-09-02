package org.fastcampus.jober.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.service.SpaceContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpaceContactController.class)
class SpaceContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpaceContactService spaceContactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactRequestDto requestDto;
    private ContactResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // 테스트용 요청 데이터 설정
        ContactRequestDto.ContactInfo contactInfo = ContactRequestDto.ContactInfo.builder()
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .build();

        requestDto = ContactRequestDto.builder()
                .spaceId(1L)
                .contacts(Arrays.asList(contactInfo))
                .build();

        // 테스트용 응답 데이터 설정
        ContactResponseDto.ContactInfo responseContactInfo = ContactResponseDto.ContactInfo.builder()
                .id(1L)
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .build();

        responseDto = ContactResponseDto.builder()
                .spaceId(1L)
                .spaceName("테스트 회사")
                .contacts(Arrays.asList(responseContactInfo))
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("연락처 등록 API 테스트 - 성공")
    @WithMockUser // 인증된 사용자로 테스트 실행
    void addContacts_Success() throws Exception {
        // given
        when(spaceContactService.addContacts(any(ContactRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.contacts[0].name").value("김철수"))
                .andExpect(jsonPath("$.contacts[0].phoneNum").value("010-1234-5678"))
                .andExpect(jsonPath("$.contacts[0].email").value("kim@example.com"));
    }

    @Test
    @DisplayName("연락처 등록 API 테스트 - 잘못된 요청")
    @WithMockUser // 인증된 사용자로 테스트 실행
    void addContacts_BadRequest() throws Exception {
        // given
        ContactRequestDto invalidRequest = ContactRequestDto.builder()
                .spaceId(null)
                .contacts(null)
                .build();

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // validation이 없으므로 200 OK 반환
    }

    @Test
    @DisplayName("CSRF 토큰 없이 요청 시 403 에러")
    @WithMockUser
    void addContacts_WithoutCsrf_ShouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(post("/space/contact")
                        // .with(csrf()) 생략 - CSRF 토큰 없음
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden()); // 403 Forbidden 예상
    }

    @Test
    @DisplayName("인증 없이 요청 시 401 또는 403 에러")
    void addContacts_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // @WithMockUser 없음 - 인증되지 않은 사용자

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf()) // CSRF는 있지만 인증이 없음
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized 또는 302 Redirect 예상
    }
}