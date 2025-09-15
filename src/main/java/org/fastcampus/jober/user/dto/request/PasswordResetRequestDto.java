package org.fastcampus.jober.user.dto.request;

import java.time.Instant;

public record PasswordResetRequestDto(String newPassword, String token) {

    public Instant getRequestedAt() {
        return Instant.now();
    }
}
