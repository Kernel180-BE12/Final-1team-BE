package org.fastcampus.jober.space.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    // DTO를 엔티티로 변환하고 유효성 검증
    List<SpaceContacts> contacts = requestDto.toEntities().stream()
        .peek(SpaceContacts::validateContactInfo)
        .collect(Collectors.toList());

    // 연락처 저장
    List<SpaceContacts> savedContacts = spaceContactsRepository.saveAll(contacts);

    // 응답 DTO 생성
    return ContactResponseDto.fromEntities(savedContacts, requestDto.getSpaceId());
  }
  
  /**
   * 연락처 정보를 수정하는 비즈니스 로직
   * 
   * @param requestDto 수정할 연락처 정보 (스페이스 ID, 연락처 ID, 수정할 정보)
   * @return 수정된 연락처 정보를 포함한 응답 DTO
   */
  @Transactional
  public SpaceContactsUpdateResponseDto updateContactInfo(SpaceContactsUpdateRequestDto requestDto) {
    // 연락처 ID로 연락처 조회
    SpaceContacts contact = spaceContactsRepository.findById(requestDto.getContactId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처를 찾을 수 없습니다."));
    
    // 스페이스 ID 검증
    if (!contact.getSpaceId().equals(requestDto.getSpaceId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "해당 스페이스의 연락처가 아닙니다.");
    }
    
    // 연락처 정보 수정
    contact.updateContactInfo(requestDto.getName(), requestDto.getPhoneNumber(), requestDto.getEmail());
    
    // 수정된 연락처 저장
    SpaceContacts updatedContact = spaceContactsRepository.save(contact);
    
    // 응답 DTO 생성
    return SpaceContactsUpdateResponseDto.fromEntities(updatedContact);
  }
}
