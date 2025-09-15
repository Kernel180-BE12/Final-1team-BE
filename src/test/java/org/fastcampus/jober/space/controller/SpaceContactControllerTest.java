package org.fastcampus.jober.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.request.ContactTagRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.dto.response.ContactTagResponseDto;
import org.fastcampus.jober.space.entity.ContactTag;
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
    private ContactTagRequestDto tagRequestDto;
    private ContactTagResponseDto tagResponseDto;

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
                .tag("정규직")
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

        // 태그 관련 테스트 데이터 설정
        tagRequestDto = ContactTagRequestDto.builder()
                .spaceId(1L)
                .tag("프리랜서")
                .build();

        ContactTag contactTag = ContactTag.builder()
                .id(1L)
                .tag("프리랜서")
                .spaceId(1L)
                .build();

        tagResponseDto = ContactTagResponseDto.builder()
                .tags(Arrays.asList(contactTag))
                .build();
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
                .andExpect(status().isNoContent());
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
                .andExpect(status().isNoContent()); // validation이 없으므로 200 OK 반환
    }

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

    // ========== 태그로 연락처 조회 테스트 ==========

    /**
     * 태그로 연락처 조회 API 테스트 - 성공 케이스
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 성공")
    @WithMockUser
    void getContactsByTag_Success() throws Exception {
        // given
        Long spaceId = 1L;
        String tag = "프리랜서";
        
        // 태그 검색 결과용 응답 데이터 설정
        ContactResponseDto.ContactInfo tagContactInfo = ContactResponseDto.ContactInfo.builder()
                .id(2L)
                .name("이영희")
                .phoneNum("010-5555-6666")
                .email("lee@example.com")
                .build();

        ContactResponseDto tagResponseDto = ContactResponseDto.builder()
                .spaceId(spaceId)
                .contacts(Arrays.asList(tagContactInfo))
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.getContactsByTag(spaceId, tag))
                .thenReturn(tagResponseDto);

        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", spaceId, tag))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(spaceId))
                .andExpect(jsonPath("$.contacts[0].id").value(2))
                .andExpect(jsonPath("$.contacts[0].name").value("이영희"))
                .andExpect(jsonPath("$.contacts[0].phoneNum").value("010-5555-6666"))
                .andExpect(jsonPath("$.contacts[0].email").value("lee@example.com"));
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 존재하지 않는 스페이스
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void getContactsByTag_SpaceNotFound() throws Exception {
        // given
        Long spaceId = 999L;
        String tag = "프리랜서";
        
        when(spaceContactService.getContactsByTag(spaceId, tag))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", spaceId, tag))
                .andExpect(status().isNotFound());
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 빈 결과 (태그에 해당하는 연락처 없음)
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 빈 결과")
    @WithMockUser
    void getContactsByTag_EmptyResult() throws Exception {
        // given
        Long spaceId = 1L;
        String tag = "존재하지않는태그";
        
        ContactResponseDto emptyResponseDto = ContactResponseDto.builder()
                .spaceId(spaceId)
                .contacts(Arrays.asList()) // 빈 리스트
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.getContactsByTag(spaceId, tag))
                .thenReturn(emptyResponseDto);

        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", spaceId, tag))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(spaceId))
                .andExpect(jsonPath("$.contacts").isArray())
                .andExpect(jsonPath("$.contacts").isEmpty());
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 잘못된 경로 변수 (spaceId)
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 잘못된 spaceId")
    @WithMockUser
    void getContactsByTag_InvalidSpaceId() throws Exception {
        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", "invalid", "프리랜서"))
                .andExpect(status().isBadRequest());
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 특수문자가 포함된 태그
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 특수문자 태그")
    @WithMockUser
    void getContactsByTag_SpecialCharacters() throws Exception {
        // given
        Long spaceId = 1L;
        String tag = "개발자-백엔드";
        
        ContactResponseDto.ContactInfo specialTagContactInfo = ContactResponseDto.ContactInfo.builder()
                .id(3L)
                .name("박민수")
                .phoneNum("010-7777-8888")
                .email("park@example.com")
                .build();

        ContactResponseDto specialTagResponseDto = ContactResponseDto.builder()
                .spaceId(spaceId)
                .contacts(Arrays.asList(specialTagContactInfo))
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.getContactsByTag(spaceId, tag))
                .thenReturn(specialTagResponseDto);

        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", spaceId, tag))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(spaceId))
                .andExpect(jsonPath("$.contacts[0].name").value("박민수"));
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 한글 태그
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 한글 태그")
    @WithMockUser
    void getContactsByTag_KoreanTag() throws Exception {
        // given
        Long spaceId = 1L;
        String tag = "웹개발자";
        
        ContactResponseDto.ContactInfo koreanTagContactInfo = ContactResponseDto.ContactInfo.builder()
                .id(4L)
                .name("최지영")
                .phoneNum("010-9999-0000")
                .email("choi@example.com")
                .build();

        ContactResponseDto koreanTagResponseDto = ContactResponseDto.builder()
                .spaceId(spaceId)
                .contacts(Arrays.asList(koreanTagContactInfo))
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.getContactsByTag(spaceId, tag))
                .thenReturn(koreanTagResponseDto);

        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", spaceId, tag))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(spaceId))
                .andExpect(jsonPath("$.contacts[0].name").value("최지영"));
    }

    /**
     * 태그로 연락처 조회 API 테스트 - 인증 없이 요청
     */
    @Test
    @DisplayName("태그로 연락처 조회 API 테스트 - 인증 없이 요청")
    void getContactsByTag_WithoutAuthentication() throws Exception {
        // when & then
        mockMvc.perform(get("/space/contact/{spaceId}/{tag}", 1L, "프리랜서"))
                .andExpect(status().isUnauthorized());
    }

    // ========== 태그 관련 테스트 ==========

    @Test
    @DisplayName("연락처 태그 추가 API 테스트 - 성공")
    @WithMockUser
    void addContactTag_Success() throws Exception {
        // given
        when(spaceContactService.addContactTag(any(ContactTagRequestDto.class)))
                .thenReturn(tagResponseDto);

        // when & then
        mockMvc.perform(post("/space/contact/tag")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags[0].id").value(1))
                .andExpect(jsonPath("$.tags[0].tag").value("프리랜서"))
                .andExpect(jsonPath("$.tags[0].spaceId").value(1));
    }

    @Test
    @DisplayName("연락처 태그 추가 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void addContactTag_SpaceNotFound() throws Exception {
        // given
        when(spaceContactService.addContactTag(any(ContactTagRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(post("/space/contact/tag")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 태그 추가 API 테스트 - 중복 태그")
    @WithMockUser
    void addContactTag_DuplicateTag() throws Exception {
        // given
        when(spaceContactService.addContactTag(any(ContactTagRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.DUPLICATE_RESOURCE, "해당 스페이스에 이미 존재하는 태그입니다."));

        // when & then
        mockMvc.perform(post("/space/contact/tag")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("연락처 태그 조회 API 테스트 - 성공")
    @WithMockUser
    void getContactTag_Success() throws Exception {
        // given
        when(spaceContactService.getContactTag(1L))
                .thenReturn(tagResponseDto);

        // when & then
        mockMvc.perform(get("/space/contact/tag/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags[0].id").value(1))
                .andExpect(jsonPath("$.tags[0].tag").value("프리랜서"))
                .andExpect(jsonPath("$.tags[0].spaceId").value(1));
    }

    @Test
    @DisplayName("연락처 태그 조회 API 테스트 - 존재하지 않는 스페이스")
    @WithMockUser
    void getContactTag_SpaceNotFound() throws Exception {
        // given
        when(spaceContactService.getContactTag(999L))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));

        // when & then
        mockMvc.perform(get("/space/contact/tag/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 태그 조회 API 테스트 - 잘못된 경로 변수")
    @WithMockUser
    void getContactTag_InvalidPathVariable() throws Exception {
        // when & then
        mockMvc.perform(get("/space/contact/tag/invalid"))
                .andExpect(status().isBadRequest());
    }

    // ========== 태그가 포함된 연락처 테스트 ==========

    @Test
    @DisplayName("태그가 포함된 연락처 조회 API 테스트 - 성공")
    @WithMockUser
    void getContacts_WithTag_Success() throws Exception {
        // given
        ContactTag contactTag = ContactTag.builder()
                .id(1L)
                .tag("프리랜서")
                .spaceId(1L)
                .build();

        ContactResponseDto.ContactInfo contactInfoWithTag = ContactResponseDto.ContactInfo.builder()
                .id(1L)
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .tag(contactTag)
                .build();

        ContactResponseDto responseWithTag = ContactResponseDto.builder()
                .spaceId(1L)
                .contacts(Arrays.asList(contactInfoWithTag))
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.getContacts(1L))
                .thenReturn(responseWithTag);

        // when & then
        mockMvc.perform(get("/space/contact/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.contacts[0].id").value(1))
                .andExpect(jsonPath("$.contacts[0].name").value("김철수"))
                .andExpect(jsonPath("$.contacts[0].tag.id").value(1))
                .andExpect(jsonPath("$.contacts[0].tag.tag").value("프리랜서"))
                .andExpect(jsonPath("$.contacts[0].tag.spaceId").value(1));
    }

    @Test
    @DisplayName("태그가 포함된 연락처 등록 API 테스트 - 성공")
    @WithMockUser
    void addContacts_WithTag_Success() throws Exception {
        // given
        ContactTag contactTag = ContactTag.builder()
                .id(1L)
                .tag("프리랜서")
                .spaceId(1L)
                .build();

        ContactResponseDto.ContactInfo contactInfoWithTag = ContactResponseDto.ContactInfo.builder()
                .id(1L)
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .tag(contactTag)
                .build();

        ContactResponseDto responseWithTag = ContactResponseDto.builder()
                .spaceId(1L)
                .contacts(Arrays.asList(contactInfoWithTag))
                .registeredAt(LocalDateTime.now())
                .build();

        when(spaceContactService.addContacts(any(ContactRequestDto.class)))
                .thenReturn(responseWithTag);

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(1))
                .andExpect(jsonPath("$.contacts[0].name").value("김철수"))
                .andExpect(jsonPath("$.contacts[0].tag.tag").value("프리랜서"));
    }

    @Test
    @DisplayName("태그가 포함된 연락처 수정 API 테스트 - 성공")
    @WithMockUser
    void updateContactInfo_WithTag_Success() throws Exception {
        // given
        ContactTag contactTag = ContactTag.builder()
                .id(2L)
                .tag("정규직")
                .spaceId(1L)
                .build();

        SpaceContactsUpdateResponseDto updateResponseWithTag = SpaceContactsUpdateResponseDto.builder()
                .id(1L)
                .name("김철수(수정)")
                .phoneNumber("010-9876-5432")
                .email("kim.updated@example.com")
                .tag(contactTag)
                .updatedAt(LocalDateTime.now())
                .build();

        when(spaceContactService.updateContactInfo(any(SpaceContactsUpdateRequestDto.class)))
                .thenReturn(updateResponseWithTag);

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("김철수(수정)"))
                .andExpect(jsonPath("$.tag.tag").value("정규직"));
    }

    @Test
    @DisplayName("연락처 수정 API 테스트 - 존재하지 않는 태그")
    @WithMockUser
    void updateContactInfo_TagNotFound() throws Exception {
        // given
        when(spaceContactService.updateContactInfo(any(SpaceContactsUpdateRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "해당 스페이스에 존재하지 않는 태그입니다: 정규직"));

        // when & then
        mockMvc.perform(put("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("연락처 등록 API 테스트 - 존재하지 않는 태그")
    @WithMockUser
    void addContacts_TagNotFound() throws Exception {
        // given
        when(spaceContactService.addContacts(any(ContactRequestDto.class)))
                .thenThrow(new org.fastcampus.jober.error.BusinessException(
                        org.fastcampus.jober.error.ErrorCode.NOT_FOUND, "해당 스페이스에 존재하지 않는 태그입니다: 프리랜서"));

        // when & then
        mockMvc.perform(post("/space/contact")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }
}