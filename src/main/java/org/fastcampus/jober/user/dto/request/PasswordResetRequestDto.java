package org.fastcampus.jober.user.dto.request;

public record PasswordResetRequestDto(String username, String password, String newPassword, String token) {
}
