package org.fastcampus.jober.space.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceContactService {

  private final SpaceContactsRepository spaceContactsRepository;

  /**
   * 스페이스에 연락처를 추가하는 비즈니스 로직
   * 
   * 기존 연락처를 유지하면서 새로운 연락처들을 추가합니다.
   * 트랜잭션으로 처리되어 모든 연락처 등록이 성공하거나 모두 실패합니다.
   * 
   * @param requestDto 연락처 추가 요청 데이터
   * @return 추가된 연락처 정보를 포함한 응답 DTO
   */
  @Transactional
  public ContactResponseDto addContacts(ContactRequestDto requestDto) {
    // 새로운 연락처들 저장 (기존 연락처는 유지)
    List<SpaceContacts> contacts = requestDto.getContacts().stream()
        .map(contactInfo -> SpaceContacts.builder()
            .name(contactInfo.getName())
            .phoneNum(contactInfo.getPhoneNum())
            .email(contactInfo.getEmail())
            .spaceId(requestDto.getSpaceId())
            .build())
        .collect(Collectors.toList());

    List<SpaceContacts> savedContacts = spaceContactsRepository.saveAll(contacts);

    // 전체 연락처 목록 조회 (기존 + 새로 추가된 연락처)
    List<SpaceContacts> allContacts = spaceContactsRepository.findBySpaceId(requestDto.getSpaceId());

    // 응답 DTO 생성
    List<ContactResponseDto.ContactInfo> contactInfos = allContacts.stream()
        .map(contact -> ContactResponseDto.ContactInfo.builder()
            .id(contact.getId())
            .name(contact.getName())
            .phoneNum(contact.getPhoneNum())
            .email(contact.getEmail())
            .build())
        .collect(Collectors.toList());

    return ContactResponseDto.builder()
        .spaceId(requestDto.getSpaceId())
        .contacts(contactInfos)
        .registeredAt(LocalDateTime.now())
        .build();
  }
}
