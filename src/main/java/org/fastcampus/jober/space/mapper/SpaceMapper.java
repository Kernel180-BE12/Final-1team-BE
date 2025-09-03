package org.fastcampus.jober.space.mapper;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpaceMapper {

    SpaceMapper INSTANCE = Mappers.getMapper(SpaceMapper.class);

    // 생성용: DTO → Entity
    @Mapping(target = "spaceId", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "spaceUrl", ignore = true)
    @Mapping(target = "spaceMembers", ignore = true)
    Space toEntity(SpaceCreateRequestDto dto);

    // 조회용: Entity → DTO
    SpaceResponseDto toResponseDto(Space space);

    // 부분 업데이트: DTO → 기존 Entity (null 값 무시)
    default void updateSpaceFromDto(SpaceUpdateRequestDto dto, @MappingTarget Space entity) {
        if (!entity.isAdminUser(dto.getUser())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "스페이스 관리자만 가능합니다.");
        }

        if (dto.getSpaceName() != null && !dto.getSpaceName().isBlank()) {
            entity.setSpaceName(dto.getSpaceName());
        }
        if (dto.getAdminName() != null && !dto.getAdminName().isBlank()) {
            entity.setAdminName(dto.getAdminName());
        }
        if (dto.getAdminNum() != null) {
            entity.setAdminNum(dto.getAdminNum());
        }
    }

}
