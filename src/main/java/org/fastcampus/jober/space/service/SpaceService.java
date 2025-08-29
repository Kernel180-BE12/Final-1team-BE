package org.fastcampus.jober.space.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    public void createSpace(SpaceCreateRequestDto dto) {
        if (dto.getSpaceName() == null || dto.getSpaceName().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "스페이스 이름은 필수입니다.");
        }

        Space space = dto.toEntity();
        spaceRepository.save(space);
    }

    public SpaceResponseDto updateSpace(Long id, SpaceUpdateRequestDto dto
            , Authentication authentication) {

        if (dto.getSpaceName() == null || dto.getSpaceName().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "스페이스 이름은 필수입니다.");
        }

        // 데이터 조회
        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 스페이스입니다."));

        // 수정 권한 체크
        String currentUser = authentication.name();
        if (!existingSpace.getAdminName().equals(currentUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "스페이스 수정 권한이 없습니다.");
        }

        // 데이터 업데이트
        existingSpace.updateSpaceInfo(dto.getSpaceName(), dto.getAdminName(), dto.getAdminNum());

        Space updatedSpace = spaceRepository.save(existingSpace);
        return updatedSpace.toResponseDto();
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
}
