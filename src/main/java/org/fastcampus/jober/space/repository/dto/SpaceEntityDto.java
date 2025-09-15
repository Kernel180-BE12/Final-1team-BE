package org.fastcampus.jober.space.repository.dto;

import lombok.Getter;
import org.fastcampus.jober.space.entity.Authority;

@Getter
public record SpaceEntityDto(
        Long spaceId,

        String spaceName,

        Authority authority
) {

}
