package org.fastcampus.jober.space.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.fastcampus.jober.space.entity.Authority;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.user.entity.Users;

@Getter
@Setter
@AllArgsConstructor
public class SpaceMemberAddRequestDto {
    private Authority authority;
    private String tag;
    private Space space;
    private Users user;

}
