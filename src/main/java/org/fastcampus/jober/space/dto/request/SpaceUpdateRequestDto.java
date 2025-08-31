package org.fastcampus.jober.space.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class  SpaceUpdateRequestDto {
    private String spaceName;
    private String adminName;
    private String adminNum;
}
