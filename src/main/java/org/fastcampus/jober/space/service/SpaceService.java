package org.fastcampus.jober.space.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        // MapStruct로 DTO → Entity 변환
        Space space = spaceMapper.toEntity(dto);
        spaceRepository.save(space);
    }

    public SpaceResponseDto getSpace(Long id) {
        Space space = spaceRepository.findByIdOrThrow(id);
        return spaceMapper.toResponseDto(space);
    }

    @Transactional
    public SpaceResponseDto updateSpace(Long id, SpaceUpdateRequestDto dto, Users user) {
        Space existingSpace = spaceRepository.findByIdOrThrow(id);
        existingSpace.updateBy(dto, user);
        // 업데이트 후 저장
        return spaceMapper.toResponseDto(existingSpace);
    }

    @Transactional
    public void deleteSpace(Long id, Users user) {
        Space existingSpace = spaceRepository.findByIdOrThrow(id);
        existingSpace.deleteBy(user);
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

