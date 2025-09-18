package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.fastcampus.jober.space.entity.Authority;

@Getter
@Setter
@AllArgsConstructor
public class SpaceMemberAddRequestDto {
  @Schema(description = "권한 정보", example = "ADMIN")
  private Authority authority;

  @Schema(description = "초대 이메일", example = "abc@abc.com")
  @NotBlank
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;
}
