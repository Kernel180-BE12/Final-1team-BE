package org.fastcampus.jober.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.util.PasswordHashing;

import static org.fastcampus.jober.user.entity.Users.*;

/**
 * // 입력값 형식 검증
 * if (!req.username().matches()) {
 * throw new BusinessException(ErrorCode.INVALID_USERNAME);
 * }
 * if (!req.password().matches()) {
 * throw new BusinessException(ErrorCode.INVALID_PASSWORD);
 * }
 * if (!req.email().matches()) {
 * throw new BusinessException(ErrorCode.INVALID_EMAIL);
 * }
 *
 * @param username
 * @param password
 * @param name
 * @param email
 */
public record RegisterRequestDto(
        @Schema(description = "사용자 이름", example = "박경태", pattern = USER_NAME_PATTERN)
        @Pattern(regexp = USER_NAME_PATTERN)
        String username,
        @Pattern(regexp = USER_PASSWORD_PATTERN)
        String password,
        String name,
        @Pattern(regexp = USER_EMAIL_PATTERN)
        String email
) {

    public Users toEntity() {
        return Users.forSignup(username, PasswordHashing.hash(password), name, email);
    }
}
