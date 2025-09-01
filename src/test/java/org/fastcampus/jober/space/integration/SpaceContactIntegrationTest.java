package org.fastcampus.jober.space.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SpaceContactIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private SpaceContactsRepository spaceContactsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @DisplayName("연락처 추가 통합 테스트 - 성공")
  void addContacts_Integration_Success() throws Exception {
    // given
    ContactRequestDto.ContactInfo contactInfo1 = ContactRequestDto.ContactInfo.builder()
        .name("김철수")
        .phoneNum("010-1234-5678")
        .email("kim@example.com")
        .build();

    ContactRequestDto.ContactInfo contactInfo2 = ContactRequestDto.ContactInfo.builder()
        .name("이영희")
        .phoneNum("010-9876-5432")
        .email("lee@example.com")
        .build();

    ContactRequestDto requestDto = ContactRequestDto.builder()
        .spaceId(1L)
        .contacts(Arrays.asList(contactInfo1, contactInfo2))
        .build();

    // when & then
    mockMvc.perform(post("/api/space/add-contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.spaceId").value(1))
        .andExpect(jsonPath("$.contacts").isArray())
        .andExpect(jsonPath("$.contacts.length()").value(2))
        .andExpect(jsonPath("$.contacts[0].name").value("김철수"))
        .andExpect(jsonPath("$.contacts[1].name").value("이영희"))
        .andExpect(jsonPath("$.registeredAt").exists());

    // 데이터베이스 검증
    assertThat(spaceContactsRepository.findBySpaceId(1L)).hasSize(2);
  }

  @Test
  @DisplayName("연락처 추가 통합 테스트 - 기존 연락처 유지")
  void addContacts_Integration_KeepExisting() throws Exception {
    // given - 기존 연락처 생성
    SpaceContacts existingContact = SpaceContacts.builder()
        .name("기존연락처")
        .phoneNum("010-0000-0000")
        .email("existing@example.com")
        .spaceId(1L)
        .build();
    spaceContactsRepository.save(existingContact);

    // 새로운 연락처 요청
    ContactRequestDto.ContactInfo newContactInfo = ContactRequestDto.ContactInfo.builder()
        .name("새연락처")
        .phoneNum("010-1111-1111")
        .email("new@example.com")
        .build();

    ContactRequestDto requestDto = ContactRequestDto.builder()
        .spaceId(1L)
        .contacts(Arrays.asList(newContactInfo))
        .build();

    // when & then
    mockMvc.perform(post("/api/space/add-contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contacts.length()").value(2)); // 기존 1개 + 새로 추가된 1개

    // 데이터베이스 검증 - 기존 연락처가 유지되고 새로운 연락처가 추가됨
    assertThat(spaceContactsRepository.findBySpaceId(1L)).hasSize(2);
    List<SpaceContacts> allContacts = spaceContactsRepository.findBySpaceId(1L);
    assertThat(allContacts).extracting("name").containsExactlyInAnyOrder("기존연락처", "새연락처");
  }

  @Test
  @DisplayName("연락처 추가 통합 테스트 - 빈 연락처 목록")
  void addContacts_Integration_EmptyContacts() throws Exception {
    // given
    ContactRequestDto requestDto = ContactRequestDto.builder()
        .spaceId(1L)
        .contacts(Arrays.asList())
        .build();

    // when & then
    mockMvc.perform(post("/api/space/add-contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contacts").isArray())
        .andExpect(jsonPath("$.contacts.length()").value(0));

    // 데이터베이스 검증
    assertThat(spaceContactsRepository.findBySpaceId(1L)).isEmpty();
  }
}
