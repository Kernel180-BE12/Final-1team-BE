package org.fastcampus.jober.space.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceListResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.mapper.SpaceMapper;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper; // Mapper 주입

    @Transactional
    public void createSpace(SpaceCreateRequestDto dto, CustomUserDetails principal) {
        Space space = spaceMapper.toEntity(dto, principal.getUserId());
        spaceRepository.save(space);
    }

    public SpaceResponseDto getSpace(Long id) {
        Space space = spaceRepository.findByIdOrThrow(id);
        return spaceMapper.toResponseDto(space);
    }

    @Transactional
    public SpaceResponseDto updateSpace(Long spaceId, SpaceUpdateRequestDto dto, CustomUserDetails principal) {

        // 1. 데이터 조회
        Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);

        Long userId = principal.getUserId();
        // 2. 관리자인지 체크
        existingSpace.validateAdminUser(userId);
        // 3. 엔티티 업데이트
        existingSpace.updateSpaceFromDto(dto);
        // 4. Entity to DTO
        return spaceMapper.toResponseDto(existingSpace);
    }

    @Transactional
    public void deleteSpace(Long spaceId, CustomUserDetails principal) {
        Space existingSpace = spaceRepository.findByIdOrThrow(spaceId);

        existingSpace.validateAdminUser(principal.getUserId());

        spaceRepository.deleteById(existingSpace.getSpaceId());
    }

    public List<SpaceListResponseDto> getSpaceList(CustomUserDetails principal) {
        return spaceRepository.findSpacesByUserId(principal.getUserId());
    }
}


