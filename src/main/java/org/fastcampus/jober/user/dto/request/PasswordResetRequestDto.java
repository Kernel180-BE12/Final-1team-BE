package org.fastcampus.jober.user.dto.request;

public record PasswordResetRequestDto(String newPassword, String token) {}
