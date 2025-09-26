package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.fastcampus.jober.space.entity.Authority;

@Getter
public class MemberUpdateRequestDto {
    @Schema(description = "구성원 권한", example = "ADMIN")
    private Authority authority;

    @Schema(description = "구성원 태그", example = "재직자")
    private String tag;
}
