package org.fastcampus.jober.space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceResponseDto {
    private Long spaceId;
    private String spaceName;
    private String adminName;
    private String adminNum;
    private String spaceEmail;
    private String spaceUrl;
    private String createAt;
    private String updateAt;

}
