package org.fastcampus.jober.space.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import org.fastcampus.jober.space.dto.request.SpaceMemberRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.user.entity.Users;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpaceMemberMapper {
  SpaceMemberMapper INSTANCE = Mappers.getMapper(SpaceMemberMapper.class);

  @Mapping(source = "user.userId", target = "userId")
  SpaceMemberResponseDto toResponseDto(SpaceMember member);

  List<SpaceMemberResponseDto> toResponseDtoList(List<SpaceMember> spaceMembers);

  @Mapping(
      target = "space",
      expression = "java(createSpaceEntity(spaceMemberRequestDto.getSpaceId()))")
  @Mapping(
      target = "user",
      expression = "java(createUserEntity(spaceMemberRequestDto.getUserId()))")
  SpaceMember toEntity(SpaceMemberRequestDto spaceMemberRequestDto);

  default Space createSpaceEntity(Long spaceId) {
    if (spaceId == null) return null;
    return Space.builder().spaceId(spaceId).build();
  }

  default Users createUserEntity(Long userId) {
    if (userId == null) return null;
    return Users.forCreateSpace(userId);
  }
}
