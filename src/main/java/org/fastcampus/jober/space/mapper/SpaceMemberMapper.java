package org.fastcampus.jober.space.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.entity.SpaceMember;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpaceMemberMapper {
  SpaceMemberMapper INSTANCE = Mappers.getMapper(SpaceMemberMapper.class);

  @Mapping(source = "user.userId", target = "userId")
  SpaceMemberResponseDto toResponseDto(SpaceMember member);

  List<SpaceMemberResponseDto> toResponseDtoList(List<SpaceMember> spaceMembers);
}
