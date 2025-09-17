package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.fastcampus.jober.space.entity.Authority;

@Getter
@AllArgsConstructor
public class SpaceListResponseDto {
  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "스페이스 이름", example = "테스트 회사")
  private String spaceName;

  @Schema(description = "내 역할", example = "ADMIN")
  private Authority authority;
}
