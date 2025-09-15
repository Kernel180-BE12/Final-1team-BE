package org.fastcampus.jober.user.dto.request;

import java.time.Instant;

public record PasswordResetTokenRequestDto(String token) {

    public Instant getRequestedAt() {
        return Instant.now();
    }
}
