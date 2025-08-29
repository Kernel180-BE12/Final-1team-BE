package org.fastcampus.jober.space.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpaceMemberRequestDto {
//   식별자(id) : 생성할 땐 DB에서 자동 생성되니 필요 없음, 수정할 땐 식별해야되니 필요
    private Long id;
    private String authority;
    private String status;
    private String tag;
    private String documentPermission;
    private Long spaceId;
    private Long userId;
}
