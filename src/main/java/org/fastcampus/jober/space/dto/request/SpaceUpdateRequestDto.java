package org.fastcampus.jober.space.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.fastcampus.jober.user.entity.Users;

@Setter
@Getter
public class  SpaceUpdateRequestDto {
    private String spaceName;
    private String adminName;
    private String adminNum;
    private Users user;
}
