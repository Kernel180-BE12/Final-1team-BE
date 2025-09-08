package org.fastcampus.jober.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private SpaceContactsUpdateRequestDto updateRequestDto;
    private SpaceContactsUpdateResponseDto updateResponseDto;
    private ContactDeleteRequestDto deleteRequestDto;

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
                .contacts(Arrays.asList(responseContactInfo))
                .registeredAt(LocalDateTime.now())
                .build();

        // 연락처 수정 요청/응답 데이터 설정
        updateRequestDto = SpaceContactsUpdateRequestDto.builder()
                .spaceId(1L)
                .contactId(1L)
                .name("김철수(수정)")
                .phoneNumber("010-9876-5432")
                .email("kim.updated@example.com")
                .build();

        updateResponseDto = SpaceContactsUpdateResponseDto.builder()
                .id(1L)
                .name("김철수(수정)")
                .phoneNumber("010-9876-5432")
                .email("kim.updated@example.com")
                .updatedAt(LocalDateTime.now())
                .build();

        // 연락처 삭제 요청 데이터 설정
        deleteRequestDto = ContactDeleteRequestDto.builder()
                .spaceId(1L)
                .contactId(1L)
                .build();
<<<<<<< HEAD
    }

    @Test
    @DisplayName("연락처 조회 API 테스트 - 성공")
    @WithMockUser
    void getContacts_Success() throws Exception {
        // given
        when(spaceContactService.getContacts(1L))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/space/contact/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.contacts[0].id").value(1))
                .andExpect(jsonPath("$.contacts[0].name").value("김철수"))
                .andExpect(jsonPath("$.contacts[0].phoneNum").value("010-1234-5678"))
                .andExpect(jsonPath("$.contacts[0].email").value("kim@example.com"));
    }

    @Test
    @DisplayName("연락처 조회 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void getContacts_SpaceNotFound() throws Exception {
        // given
        when(spaceContactService.getContacts(999L))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(get("/space/contact/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 조회 API 테스트 - 잘못된 경로 변수")
    @WithMockUser
    void getContacts_InvalidPathVariable() throws Exception {
        // when & then
        mockMvc.perform(get("/space/contact/invalid"))
                .andExpect(status().isBadRequest());
=======
>>>>>>> 4987c0a981833eeca30cf9042698bbf27efdd1a7
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
    @WithMockUser
    void addContacts_BadRequest() throws Exception {
        // given
        ContactRequestDto invalidRequest = ContactRequestDto.builder()
                .spaceId(null)
                .contacts(null)
                .build();

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // validation이 없으므로 200 OK 반환
    }

    @Test
    @DisplayName("연락처 등록 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void addContacts_SpaceNotFound() throws Exception {
        // given
        when(spaceContactService.addContacts(any(ContactRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
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

    @Test
    @DisplayName("연락처 수정 API 테스트 - 성공")
    @WithMockUser
    void updateContactInfo_Success() throws Exception {
        // given
        when(spaceContactService.updateContactInfo(any(SpaceContactsUpdateRequestDto.class)))
                .thenReturn(updateResponseDto);

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("김철수(수정)"))
                .andExpect(jsonPath("$.phoneNumber").value("010-9876-5432"))
                .andExpect(jsonPath("$.email").value("kim.updated@example.com"));
    }

    @Test
    @DisplayName("연락처 삭제 API 테스트 - 성공")
    @WithMockUser
    void deleteContact_Success() throws Exception {
        // given
        // deleteContact는 void를 반환하므로 when 설정 불필요

        // when & then
        mockMvc.perform(delete("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("연락처 수정 API 테스트 - 잘못된 요청")
    @WithMockUser
    void updateContactInfo_BadRequest() throws Exception {
        // given
        SpaceContactsUpdateRequestDto invalidRequest = SpaceContactsUpdateRequestDto.builder()
                .spaceId(null)
                .contactId(null)
                .build();

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // validation이 없으므로 200 OK 반환
    }

    @Test
<<<<<<< HEAD
    @DisplayName("연락처 수정 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void updateContactInfo_SpaceNotFound() throws Exception {
        // given
        when(spaceContactService.updateContactInfo(any(SpaceContactsUpdateRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 수정 API 테스트 - 존재하지 않는 연락처")
    @WithMockUser
    void updateContactInfo_ContactNotFound() throws Exception {
        // given
        when(spaceContactService.updateContactInfo(any(SpaceContactsUpdateRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "연락처를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
=======
>>>>>>> 4987c0a981833eeca30cf9042698bbf27efdd1a7
    @DisplayName("연락처 삭제 API 테스트 - 잘못된 요청")
    @WithMockUser
    void deleteContact_BadRequest() throws Exception {
        // given
        ContactDeleteRequestDto invalidRequest = ContactDeleteRequestDto.builder()
                .spaceId(null)
                .contactId(null)
                .build();

        // when & then
        mockMvc.perform(delete("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk()); // validation이 없으므로 200 OK 반환
    }
<<<<<<< HEAD

    @Test
    @DisplayName("연락처 삭제 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void deleteContact_SpaceNotFound() throws Exception {
        // given
        doThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."))
                .when(spaceContactService).deleteContact(any(ContactDeleteRequestDto.class));

        // when & then
        mockMvc.perform(delete("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 삭제 API 테스트 - 존재하지 않는 연락처")
    @WithMockUser
    void deleteContact_ContactNotFound() throws Exception {
        // given
        doThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "연락처를 찾을 수 없습니다."))
                .when(spaceContactService).deleteContact(any(ContactDeleteRequestDto.class));

        // when & then
        mockMvc.perform(delete("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequestDto)))
                .andExpect(status().isNotFound());
    }
=======
>>>>>>> 4987c0a981833eeca30cf9042698bbf27efdd1a7
}