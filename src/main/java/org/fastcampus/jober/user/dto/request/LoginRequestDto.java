package org.fastcampus.jober.user.dto.request;

// 불변성 유지를 위해 record 사용 + 코드 간결
public record LoginRequestDto(String username, String password) {}
