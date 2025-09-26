package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.fastcampus.jober.space.entity.Authority;

@Getter
@Builder
public class SpaceMemberRequestDto {
  //   식별자(id) : 생성할 땐 DB에서 자동 생성되니 필요 없음, 수정할 땐 식별해야되니 필요
  @Schema(description = "구성원 식별자(ID)", example = "1")
  private Long id;

  @Schema(description = "구성원 권한", example = "ADMIN")
  private Authority authority;

  @Schema(description = "구성원 태그", example = "재직자")
  private String tag;

  @Schema(description = "스페이스 ID", example = "1001")
  private Long spaceId;

  @Schema(description = "사용자 ID", example = "2002")
  private Long userId;

  @Schema(description = "논리삭제 유무")
  private Boolean isDeleted;
}
