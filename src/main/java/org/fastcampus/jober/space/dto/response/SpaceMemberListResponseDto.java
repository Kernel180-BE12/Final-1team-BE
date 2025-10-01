package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.space.entity.Authority;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceMemberListResponseDto {
    @Schema(description = "구성원 아이디", example = "1")
    private Long id;

  @Schema(description = "구성원 이름", example = "홍길동")
  private String name;

  @Schema(description = "구성원 이메일", example = "aaa@aaa.com")
  private String email;

  @Schema(description = "구성원 권한", example = "ADMIN")
  private Authority authority;

  @Schema(description = "구성원 태그", example = "마케팅팀")
  private String tag;
}
