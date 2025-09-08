package org.fastcampus.jober.space.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceContactService {

  private final SpaceContactsRepository spaceContactsRepository;
  private final SpaceRepository spaceRepository;

  /**
   * 스페이스에 연락처를 조회하는 비즈니스 로직
   * 
   * @param spaceId 조회할 스페이스 ID
   * @return 조회된 연락처 정보
   */
  public ContactResponseDto getContacts(Long spaceId) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(spaceId);
    
    // 연락처 조회
    List<SpaceContacts> contacts = spaceContactsRepository.findBySpaceId(spaceId);
    return ContactResponseDto.fromEntities(contacts, spaceId);
  }

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
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());
    
    // DTO를 엔티티로 변환하고 유효성 검증
    List<SpaceContacts> contacts = requestDto.toValidateEntities();

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
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());
    
    // 연락처 ID로 연락처 조회
    SpaceContacts contact = spaceContactsRepository.findById(requestDto.getContactId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처를 찾을 수 없습니다."));
    
    // DTO를 사용하여 엔티티 업데이트 및 검증
    SpaceContacts updatedContact = requestDto.updateExistingContact(contact);
    
    // 수정된 연락처 저장
    SpaceContacts savedContact = spaceContactsRepository.save(updatedContact);
    
    // 응답 DTO 생성
    return SpaceContactsUpdateResponseDto.fromEntities(savedContact);
  }

  /**
   * 연락처를 논리삭제하는 비즈니스 로직
   * 
   * @param requestDto 삭제할 연락처 정보 (스페이스 ID, 연락처 ID)
   */
  @Transactional
  public void deleteContact(ContactDeleteRequestDto requestDto) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());
    
    // 연락처 ID로 연락처 조회
    SpaceContacts contact = spaceContactsRepository.findById(requestDto.getContactId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처를 찾을 수 없습니다."));
    
    // DTO를 통해 권한 검증 및 삭제 준비
    requestDto.validateAndPrepareForDeletion(contact);
    
    // 연락처 논리삭제 (isDeleted를 true로 설정)
    contact.softDelete();
    spaceContactsRepository.save(contact);
  }
}
