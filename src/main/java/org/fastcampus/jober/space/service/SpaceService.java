package org.fastcampus.jober.space.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;

    @Transactional
    public SpaceResponseDto createSpace(SpaceRequestDto dto) {
        // dto -> entity 변환
        Space space = Space.builder()
                .name(dto.getSpaceName())
                .adminName(dto.getAdminName())
                .adminNum(dto.getAdminNum())
                .build();

        Space savedSpace = spaceRepository.save(space);

        return toResponseDto(savedSpace);
    }

    @Transactional
    public SpaceResponseDto updateSpace(Long id, SpaceRequestDto dto) {
        if (!spaceRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 스페이스입니다.");
        }

        // 데이터 조회
        Space existingSpace = spaceRepository.findById(id).get();

        // 데이터 업데이트
        existingSpace.setName(dto.getSpaceName());
        existingSpace.setAdminName(dto.getAdminName());
        existingSpace.setAdminNum(dto.getAdminNum());

        // 저장
        Space updatedSpace = spaceRepository.save(existingSpace);

        return toResponseDto(existingSpace);
    }

    @Transactional
    public void deleteSpace(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 스페이스입니다.");
        }
        spaceRepository.deleteById(id);
    }

    @Transactional
    public List<SpaceResponseDto> findMySpace(Long memberId) {
        // 유저 아이디로 객체 조회한 목록
        List<Space> spaces = spaceRepository.findByMemberId(memberId);
        // 저장된 객체들 dto로 변경해서 담을 리스트
        List<SpaceResponseDto> result = new ArrayList<>();

        for (Space space : spaces) {
            SpaceResponseDto dto = toResponseDto(space);
            result.add(dto);
        }
        return result;
    }

    // entity -> dto 변환 메서드
    private SpaceResponseDto toResponseDto(Space space) {
        return new SpaceResponseDto(
                space.getId(),
                space.getName(),
                List.of(),  // members
                List.of()   // bigSends
        );
    }
}
