package org.fastcampus.jober.space.mapper;

import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceListResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.user.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpaceMapper {

//    SpaceMapper INSTANCE = Mappers.getMapper(SpaceMapper.class);

    @Mapping(target = "spaceId", ignore = true)
    @Mapping(target = "admin", expression = "java(createUserEntity(adminUserId))")
    @Mapping(target = "spaceUrl", ignore = true)
    @Mapping(target = "spaceMembers", ignore = true)
    @Mapping(target = "ownerNum", ignore = true)
    Space toEntity(SpaceCreateRequestDto dto, Long adminUserId);
    // 생성용: DTO → Entity

    // 조회용: Entity → DTO
    @Mapping(target = "adminName", source = "ownerName")
    @Mapping(target = "adminNum", source = "ownerNum")
    SpaceResponseDto toResponseDto(Space space);

    @Mapping(source = "space.spaceId", target = "spaceId")
    @Mapping(source = "space.spaceName", target = "spaceName")
    @Mapping(source = "authority", target = "authority")
    SpaceListResponseDto fromMember(SpaceMember member);

    default Users createUserEntity(Long userId) {
        if (userId == null) return null;
        return Users.forCreateSpace(userId);
    }
}
