package org.fastcampus.jober.space.dto.request;

import org.fastcampus.jober.space.entity.Space;

public class SpaceCreateRequestDto {
    private String spaceName;
    private String adminName;
    private String adminNum;

    public Space toEntity() {
        return Space.builder()
                .name(this.spaceName)
                .adminName(this.adminName)
                .adminNum(this.adminNum)
                .build();
    }
}
