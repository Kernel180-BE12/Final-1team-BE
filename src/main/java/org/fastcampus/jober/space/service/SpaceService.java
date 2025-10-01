package org.fastcampus.jober.space.service;

import java.util.List;

import org.fastcampus.jober.space.entity.Authority;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceMemberRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceListResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.space.mapper.SpaceMapper;
import org.fastcampus.jober.space.mapper.SpaceMemberMapper;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.dto.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class SpaceService {
  private final SpaceRepository spaceRepository;
  private final SpaceMapper spaceMapper; // Mapper 주입
  private final SpaceMemberMapper spaceMemberMapper;
  private final SpaceMemberRepository spaceMemberRepository;

  @Transactional
  public void createSpace(SpaceCreateRequestDto dto, CustomUserDetails principal) {
    Space space = spaceMapper.toEntity(dto, principal.getUserId());
    Space savedSpace = spaceRepository.save(space);

    SpaceMemberRequestDto adminUser =
        SpaceMemberRequestDto.builder()
            .spaceId(savedSpace.getSpaceId())
            .userId(principal.getUserId())
            .authority(Authority.ADMIN)
            .isDeleted(false)
            .build();

    SpaceMember spaceMember = spaceMemberMapper.toEntity(adminUser);
    spaceMemberRepository.save(spaceMember);
  }

  public SpaceResponseDto getSpace(Long id) {
    Space space = spaceRepository.findByIdOrThrow(id);
    return spaceMapper.toResponseDto(space);
  }

  @Transactional
  public SpaceResponseDto updateSpace(
      Long spaceId, SpaceUpdateRequestDto dto, CustomUserDetails principal) {

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
