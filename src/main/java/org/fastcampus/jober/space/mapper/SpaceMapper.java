package org.fastcampus.jober.space.mapper;

import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpaceMapper {

//    SpaceMapper INSTANCE = Mappers.getMapper(SpaceMapper.class);

    // 생성용: DTO → Entity
    @Mapping(target = "spaceId", ignore = true)
    @Mapping(target = "adminUserId", source = "adminUserId")
    @Mapping(target = "spaceUrl", ignore = true)
    @Mapping(target = "spaceMembers", ignore = true)
    Space toEntity(SpaceCreateRequestDto dto, Long adminUserId);

    // 조회용: Entity → DTO
    SpaceResponseDto toResponseDto(Space space);

}
