package org.fastcampus.jober.space.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.mapper.SpaceMapper;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.entity.Users;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper; // Mapper 주입

    @Transactional
    public void createSpace(SpaceCreateRequestDto dto) {
        if (dto.getSpaceName() == null || dto.getSpaceName().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "스페이스 이름은 필수입니다.");
        }

        // MapStruct로 DTO → Entity 변환
        Space space = spaceMapper.toEntity(dto);
        spaceRepository.save(space);
    }

    public SpaceResponseDto getSpace(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 스페이스입니다."));
        return spaceMapper.toResponseDto(space);
    }

    @Transactional
    public SpaceResponseDto updateSpace(Long id, SpaceUpdateRequestDto dto, Users user) {

        // 데이터 조회
        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 스페이스입니다."));

        if (!existingSpace.getAdmin().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "스페이스 수정은 관리자만 가능합니다.");
        }

        spaceMapper.updateSpaceFromDto(dto, existingSpace);

        // 업데이트 후 저장
        Space updatedSpace = spaceRepository.save(existingSpace);

        return spaceMapper.toResponseDto(updatedSpace);
    }

    public void deleteSpace(Long id, Users user) {
        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 스페이스입니다."));

        if (!existingSpace.getAdmin().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "스페이스 삭제는 관리자만 가능합니다.");
        }

        spaceRepository.deleteById(id);
    }
}

//
//    // 특정 유저의 스페이스 목록 조회
//    public List<SpaceResponseDto> findById(Long userId) {
//        // 유저 아이디로 객체 조회한 목록
//        List<Space> spaces = spaceRepository.findById(userId);
//        // 저장된 객체들 dto로 변경해서 담을 리스트
//        List<SpaceResponseDto> result = new ArrayList<>();
//
//        for (Space space : spaces) {
//            SpaceResponseDto dto =
//            result.add(dto);
//        }
//        return result;
//    }
//
//    // 스페이스의 멤버목록 조회
//    public List<SpaceMemberResponseDto> getSpaceMembers(Long spaceId) {
//        spaceMemberRepository.findById(spaceId);
//        List<SpaceMemberResponseDto> result = new ArrayList<>();
//    }

