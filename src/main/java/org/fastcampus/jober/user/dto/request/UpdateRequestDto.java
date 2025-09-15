package org.fastcampus.jober.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.user.entity.Users;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {

    // private String username;

    @NotBlank
    @Pattern(regexp = Users.USER_NAME_PATTERN)
    private String name;

    @NotBlank
    @Pattern(regexp = Users.USER_EMAIL_PATTERN)
    private String email;

    // /**
    //  * 기존 엔티티를 업데이트 (변경된 필드만)
    //  * @param existingUser 기존 사용자 엔티티
    //  * @return 변경된 필드가 있는지 여부
    //  */
    // public boolean updateEntity(Users existingUser) {
    //     return existingUser.updateUserInfo(username, name, email);
    // }

    // /**
    //  * 변경사항이 있는지 확인
    //  * @param existingUser 기존 사용자 엔티티
    //  * @return 변경사항이 있는지 여부
    //  */
    // public boolean hasChanges(Users existingUser) {
    //     boolean usernameChanged = username != null && !username.equals(existingUser.getUsername());
    //     boolean nameChanged = name != null && !name.equals(existingUser.getName());
    //     boolean emailChanged = email != null && !email.equals(existingUser.getEmail());
        
    //     return usernameChanged || nameChanged || emailChanged;
    // }
}
