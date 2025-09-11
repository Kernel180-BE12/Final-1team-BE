package org.fastcampus.jober.user.dto.response;

import org.fastcampus.jober.user.entity.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {

    private String username;
    private String name;
    private String email;

    public static UserInfoResponseDto fromEntity(Users user) {
        return new UserInfoResponseDto(user.getUsername(), user.getName(), user.getEmail());
    }
}
