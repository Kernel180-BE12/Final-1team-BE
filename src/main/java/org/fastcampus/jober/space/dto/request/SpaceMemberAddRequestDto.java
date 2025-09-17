package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.fastcampus.jober.space.entity.Authority;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.user.entity.Users;

@Getter
@Setter
@AllArgsConstructor
public class SpaceMemberAddRequestDto {
  @Schema(description = "권한 정보", example = "ADMIN")
  private Authority authority;

  @Schema(description = "구성원 태그", example = "퇴사자")
  private String tag;

  @Schema(description = "스페이스 엔티티", example = "Space 객체")
  private Space space;

  @Schema(description = "사용자 엔티티", example = "Users 객체")
  private Users user;
}
