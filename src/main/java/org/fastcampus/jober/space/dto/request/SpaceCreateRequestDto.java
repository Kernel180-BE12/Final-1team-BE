package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceCreateRequestDto {
  @Schema(description = "스페이스 이름", example = "우두머리경두")
  private String spaceName;

  @Schema(description = "스페이스 관리자 이름", example = "홍길동")
  private String ownerName;
}
