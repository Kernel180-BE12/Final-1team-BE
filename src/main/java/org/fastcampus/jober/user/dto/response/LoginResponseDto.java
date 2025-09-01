package org.fastcampus.jober.user.dto.response;

// 확장성, 의미 표현, 일관성을 위해 dto 로 wrapping
public record LoginResponseDto(Long userId, String username) {
}
