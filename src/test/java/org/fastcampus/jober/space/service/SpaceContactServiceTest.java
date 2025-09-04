package org.fastcampus.jober.space.service;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * SpaceContactService 테스트 클래스
 */
@ExtendWith(MockitoExtension.class)
class SpaceContactServiceTest {

    @Mock
    private SpaceContactsRepository spaceContactsRepository;

    @InjectMocks
    private SpaceContactService spaceContactService;

    private ContactRequestDto contactRequestDto;
    private SpaceContactsUpdateRequestDto updateRequestDto;
    private SpaceContacts existingContact;
    private SpaceContacts updatedContact;

    @BeforeEach
    void setUp() {
        // 연락처 추가 요청 DTO 설정
        ContactRequestDto.ContactInfo contactInfo = ContactRequestDto.ContactInfo.builder()
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .build();

        contactRequestDto = ContactRequestDto.builder()
                .spaceId(1L)
                .contacts(Arrays.asList(contactInfo))
                .build();

        // 연락처 수정 요청 DTO 설정
        updateRequestDto = SpaceContactsUpdateRequestDto.builder()
                .spaceId(1L)
                .contactId(1L)
                .name("김철수(수정)")
                .phoneNumber("010-9876-5432")
                .email("kim.updated@example.com")
                .build();

        // 기존 연락처 엔티티 설정
        existingContact = SpaceContacts.builder()
                .id(1L)
                .spaceId(1L)
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .build();

        // 수정된 연락처 엔티티 설정
        updatedContact = SpaceContacts.builder()
                .id(1L)
                .spaceId(1L)
                .name("김철수(수정)")
                .phoneNum("010-9876-5432")
                .email("kim.updated@example.com")
                .build();
    }

    @Test
    @DisplayName("연락처 추가 - 성공")
    void addContacts_Success() {
        // given
        List<SpaceContacts> contacts = contactRequestDto.toValidateEntities();
        when(spaceContactsRepository.saveAll(anyList())).thenReturn(contacts);

        // when
        ContactResponseDto result = spaceContactService.addContacts(contactRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getSpaceId()).isEqualTo(1L);
        assertThat(result.getContacts()).hasSize(1);
        assertThat(result.getContacts().get(0).getName()).isEqualTo("김철수");
        
        verify(spaceContactsRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("연락처 수정 - 성공")
    void updateContactInfo_Success() {
        // given
        when(spaceContactsRepository.findById(1L)).thenReturn(Optional.of(existingContact));
        when(spaceContactsRepository.save(any(SpaceContacts.class))).thenReturn(updatedContact);

        // when
        SpaceContactsUpdateResponseDto result = spaceContactService.updateContactInfo(updateRequestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("김철수(수정)");
        assertThat(result.getPhoneNumber()).isEqualTo("010-9876-5432");
        assertThat(result.getEmail()).isEqualTo("kim.updated@example.com");
        
        verify(spaceContactsRepository, times(1)).findById(1L);
        verify(spaceContactsRepository, times(1)).save(any(SpaceContacts.class));
    }

    @Test
    @DisplayName("연락처 수정 - 연락처를 찾을 수 없음")
    void updateContactInfo_ContactNotFound() {
        // given
        when(spaceContactsRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> spaceContactService.updateContactInfo(updateRequestDto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND)
                .hasMessage("연락처를 찾을 수 없습니다.");
        
        verify(spaceContactsRepository, times(1)).findById(1L);
        verify(spaceContactsRepository, never()).save(any(SpaceContacts.class));
    }

    @Test
    @DisplayName("연락처 수정 - 다른 스페이스의 연락처 수정 시도")
    void updateContactInfo_DifferentSpaceId() {
        // given
        SpaceContacts differentSpaceContact = SpaceContacts.builder()
                .id(1L)
                .spaceId(2L) // 다른 스페이스 ID
                .name("김철수")
                .phoneNum("010-1234-5678")
                .email("kim@example.com")
                .build();

        when(spaceContactsRepository.findById(1L)).thenReturn(Optional.of(differentSpaceContact));

        // when & then
        assertThatThrownBy(() -> spaceContactService.updateContactInfo(updateRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스페이스의 연락처가 아닙니다.");
        
        verify(spaceContactsRepository, times(1)).findById(1L);
        verify(spaceContactsRepository, never()).save(any(SpaceContacts.class));
    }

    @Test
    @DisplayName("연락처 수정 - DTO 검증 실패")
    void updateContactInfo_ValidationFailure() {
        // given
        SpaceContactsUpdateRequestDto invalidRequest = SpaceContactsUpdateRequestDto.builder()
                .spaceId(1L)
                .contactId(1L)
                .name(null) // 이름이 null
                .phoneNumber("010-9876-5432")
                .email("kim.updated@example.com")
                .build();

        when(spaceContactsRepository.findById(1L)).thenReturn(Optional.of(existingContact));

        // when & then
        assertThatThrownBy(() -> spaceContactService.updateContactInfo(invalidRequest))
                .isInstanceOf(Exception.class); // 엔티티의 updateContactInfo에서 예외 발생
        
        verify(spaceContactsRepository, times(1)).findById(1L);
        verify(spaceContactsRepository, never()).save(any(SpaceContacts.class));
    }

    @Test
    @DisplayName("연락처 수정 - 저장 실패")
    void updateContactInfo_SaveFailure() {
        // given
        when(spaceContactsRepository.findById(1L)).thenReturn(Optional.of(existingContact));
        when(spaceContactsRepository.save(any(SpaceContacts.class)))
                .thenThrow(new RuntimeException("저장 실패"));

        // when & then
        assertThatThrownBy(() -> spaceContactService.updateContactInfo(updateRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("저장 실패");
        
        verify(spaceContactsRepository, times(1)).findById(1L);
        verify(spaceContactsRepository, times(1)).save(any(SpaceContacts.class));
    }
}
