package org.fastcampus.jober.space.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.ContactTagAddRequestDto;
import org.fastcampus.jober.space.dto.request.ContactTagDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.ContactTagUpdateRequestDto;
import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.ContactTagResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.entity.ContactTag;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.ContactTagRepository;
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
  private final ContactTagRepository contactTagRepository;

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
    
    // 태그 업데이트 처리
    if (requestDto.getTag() != null && !requestDto.getTag().trim().isEmpty()) {
      ContactTag contactTag = contactTagRepository.findBySpaceIdAndTag(requestDto.getSpaceId(), requestDto.getTag())
          .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, 
              "해당 스페이스에 존재하지 않는 태그입니다: " + requestDto.getTag()));
      updatedContact.updateContactInfo(null, null, null, contactTag);
    } else if (requestDto.getTag() != null && requestDto.getTag().trim().isEmpty()) {
      // 빈 문자열인 경우 태그 제거
      updatedContact.updateContactInfo(null, null, null, null);
    }
    
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

  /**
   * 연락처 태그를 추가하는 비즈니스 로직
   * 
   * 스페이스 ID와 태그명의 중복을 체크하여 중복되지 않는 경우에만 태그를 추가합니다.
   * 
   * @param requestDto 태그 추가 요청 데이터 (스페이스 ID, 태그명)
   * @param principal 인증된 사용자 정보
   * @return 추가된 태그 정보를 포함한 응답 DTO
   */
  @Transactional
  public ContactTagResponseDto addContactTag(ContactTagAddRequestDto requestDto) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());

    if (contactTagRepository.existsBySpaceIdAndTagIsDeletedTrue(requestDto.getSpaceId(), requestDto.getTag())) {
      ContactTag contactTag = contactTagRepository.findBySpaceIdAndTagIsDeletedTrue(requestDto.getSpaceId(), requestDto.getTag())
          .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처 태그를 찾을 수 없습니다."));
      contactTag.restore();
      contactTagRepository.save(contactTag);
      return ContactTagResponseDto.fromEntity(contactTag);
    }
    
    // 스페이스 ID와 태그명 중복 체크
    if (contactTagRepository.existsBySpaceIdAndTag(requestDto.getSpaceId(), requestDto.getTag())) {
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
          "해당 스페이스에 이미 존재하는 태그입니다.");
    }
    
    // DTO를 엔티티로 변환
    ContactTag contactTag = requestDto.toEntity();
    
    // 태그 저장
    ContactTag savedContactTag = contactTagRepository.save(contactTag);
    
    // 응답 DTO 생성
    return ContactTagResponseDto.fromEntity(savedContactTag);
  }

  /**
   * 연락처 태그 목록을 조회하는 비즈니스 로직
   * 
   * @param spaceId 조회할 스페이스 ID
   * @return 조회된 연락처 태그 목록 정보
   */
  public ContactTagResponseDto getContactTag(Long spaceId) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(spaceId);
    
    // 연락처 태그 목록 조회
    List<ContactTag> contactTags = contactTagRepository.findBySpaceId(spaceId);
    return ContactTagResponseDto.fromEntities(contactTags);
  }
  
  /**
   * 스페이스 ID와 tag를 받아 연락처를 조회하는 비즈니스 로직
   * 
   * @param spaceId 조회할 스페이스 ID
   * @param tag 조회할 연락처 tag
   * @return 조회된 연락처 정보
   */
  @Transactional
  public ContactResponseDto getContactsByTag(Long spaceId, String tag) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(spaceId);

    // 연락처 태그 존재 여부 검증
    contactTagRepository.findBySpaceIdAndTag(spaceId, tag)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처 태그를 찾을 수 없습니다."));
    
    // 연락처 조회
    List<SpaceContacts> contacts = spaceContactsRepository.findBySpaceIdAndTag(spaceId, tag);
    return ContactResponseDto.fromEntities(contacts, spaceId);
  }

  /**
   * 연락처 태그를 수정하는 비즈니스 로직
   * 
   * @param requestDto 연락처 태그 수정 요청 데이터 (스페이스 ID, 태그)
   * @return 수정된 연락처 태그 정보
   */
  @Transactional
  public ContactTagResponseDto updateContactTag(ContactTagUpdateRequestDto requestDto) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());
    
    // 연락처 태그 존재 여부 검증
    ContactTag contactTag = contactTagRepository.findById(requestDto.getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처 태그를 찾을 수 없습니다."));
    
    // 연락처 태그 수정
    contactTag.updateTag(requestDto.getTag());
    
    return ContactTagResponseDto.fromEntity(contactTag);
  }

  /**
   * 연락처 태그를 삭제하는 비즈니스 로직
   * 
   * 태그를 논리삭제하고, 해당 태그를 참조하는 모든 연락처의 contactTag를 null로 설정합니다.
   * 
   * @param requestDto 연락처 태그 삭제 요청 데이터 (스페이스 ID, 태그 ID)
   */
  @Transactional
  public void deleteContactTag(ContactTagDeleteRequestDto requestDto) {
    // Space 존재 여부 검증
    spaceRepository.findByIdOrThrow(requestDto.getSpaceId());
    
    // 연락처 태그 존재 여부 검증
    ContactTag contactTag = contactTagRepository.findById(requestDto.getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "연락처 태그를 찾을 수 없습니다."));
    
    // 해당 태그를 참조하는 연락처들의 contactTag를 null로 설정
    List<SpaceContacts> contactsWithTag = spaceContactsRepository.findByContactTagId(contactTag.getId());
    
    // 소량 데이터(100개 이하)는 엔티티 방식, 대량 데이터는 @Modifying 방식 사용
    if (contactsWithTag.size() <= 100) {
      // JPA 표준 방식: 1차 캐시 일관성 보장, 비즈니스 로직 적용 가능
      contactsWithTag.forEach(SpaceContacts::removeTagReference);
      spaceContactsRepository.saveAll(contactsWithTag);
    } else {
      // 대량 처리 방식: 성능 우선, 1차 캐시 무시
      spaceContactsRepository.removeContactTagReferenceBulk(contactTag.getId());
    }
    
    // 연락처 태그 논리삭제
    contactTag.softDelete();
    contactTagRepository.save(contactTag);
  }

}
