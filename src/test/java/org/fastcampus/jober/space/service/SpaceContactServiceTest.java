package org.fastcampus.jober.space.service;

import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceContactServiceTest {

  @Mock
  private SpaceContactsRepository spaceContactsRepository;

  @InjectMocks
  private SpaceContactService spaceContactService;

  private ContactRequestDto requestDto;
  private List<SpaceContacts> savedContacts;

  @BeforeEach
  void setUp() {
    // 테스트용 요청 데이터 설정
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

    requestDto = ContactRequestDto.builder()
        .spaceId(1L)
        .contacts(Arrays.asList(contactInfo1, contactInfo2))
        .build();

    // 테스트용 저장된 연락처 데이터 설정
    SpaceContacts savedContact1 = SpaceContacts.builder()
        .id(1L)
        .name("김철수")
        .phoneNum("010-1234-5678")
        .email("kim@example.com")
        .spaceId(1L)
        .build();

    SpaceContacts savedContact2 = SpaceContacts.builder()
        .id(2L)
        .name("이영희")
        .phoneNum("010-9876-5432")
        .email("lee@example.com")
        .spaceId(1L)
        .build();

    savedContacts = Arrays.asList(savedContact1, savedContact2);
  }

  @Test
  @DisplayName("연락처 추가 테스트 - 성공")
  void addContacts_Success() {
    // given
    when(spaceContactsRepository.saveAll(any())).thenReturn(savedContacts);
    when(spaceContactsRepository.findBySpaceId(1L)).thenReturn(savedContacts);

    // when
    ContactResponseDto result = spaceContactService.addContacts(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSpaceId()).isEqualTo(1L);
    assertThat(result.getContacts()).hasSize(2);
    assertThat(result.getContacts().get(0).getName()).isEqualTo("김철수");
    assertThat(result.getContacts().get(1).getName()).isEqualTo("이영희");
    assertThat(result.getRegisteredAt()).isNotNull();

    // verify
    verify(spaceContactsRepository, times(1)).saveAll(any());
    verify(spaceContactsRepository, times(1)).findBySpaceId(1L);
  }

  @Test
  @DisplayName("연락처 추가 테스트 - 빈 연락처 목록")
  void addContacts_EmptyContacts() {
    // given
    ContactRequestDto emptyRequest = ContactRequestDto.builder()
        .spaceId(1L)
        .contacts(Arrays.asList())
        .build();

    when(spaceContactsRepository.saveAll(any())).thenReturn(Arrays.asList());
    when(spaceContactsRepository.findBySpaceId(1L)).thenReturn(Arrays.asList());

    // when
    ContactResponseDto result = spaceContactService.addContacts(emptyRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSpaceId()).isEqualTo(1L);
    assertThat(result.getContacts()).isEmpty();

    // verify
    verify(spaceContactsRepository, times(1)).saveAll(any());
    verify(spaceContactsRepository, times(1)).findBySpaceId(1L);
  }

  @Test
  @DisplayName("연락처 추가 테스트 - 기존 연락처 유지 확인")
  void addContacts_VerifyKeepExistingContacts() {
    // given
    when(spaceContactsRepository.saveAll(any())).thenReturn(savedContacts);
    when(spaceContactsRepository.findBySpaceId(1L)).thenReturn(savedContacts);

    // when
    spaceContactService.addContacts(requestDto);

    // then
    verify(spaceContactsRepository, times(1)).saveAll(any());
    verify(spaceContactsRepository, times(1)).findBySpaceId(1L);
    verify(spaceContactsRepository, never()).deleteBySpaceId(anyLong());
  }
}
