package org.fastcampus.jober.space.dto.request;

import lombok.*;
import org.fastcampus.jober.space.entity.Space;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceCreateRequestDto {
    private String spaceName;
    private String adminName;
    private String adminNum;
}
