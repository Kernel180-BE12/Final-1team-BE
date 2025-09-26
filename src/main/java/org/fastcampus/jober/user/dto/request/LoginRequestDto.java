package org.fastcampus.jober.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

// 불변성 유지를 위해 record 사용 + 코드 간결
public record LoginRequestDto(
        @Schema(description = "아이디", example = "apple")
        String username,
        @Schema(description = "비밀번호", example = "apple!123", format = "password")
        String password
) {}
