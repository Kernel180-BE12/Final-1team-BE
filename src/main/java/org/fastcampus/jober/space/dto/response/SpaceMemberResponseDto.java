package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceMemberResponseDto {
  @Schema(description = "구성원 식별자(ID)", example = "1")
  private Long id;

  @Schema(description = "구성원 권한", example = "ADMIN")
  private String authority;

  @Schema(description = "구성원 태그", example = "마케팅팀")
  private String tag;

  @Schema(description = "사용자 ID", example = "2002")
  private Long userId;
}
