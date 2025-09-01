package org.fastcampus.jober.space.service;

import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.fastcampus.jober.space.repository.SpaceContactsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<SpaceContacts> contacts = requestDto.toValidatedEntities();

        // 연락처 저장
        List<SpaceContacts> savedContacts = spaceContactsRepository.saveAll(contacts);

        // 응답 DTO 생성
        return ContactResponseDto.fromEntities(savedContacts, requestDto.getSpaceId());
    }

}
