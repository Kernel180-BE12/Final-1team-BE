package org.fastcampus.jober.template.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.template.entity.Template;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDeleteRequestDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "삭제할 템플릿 ID", example = "1")
  private Long templateId;

  /**
   * 템플릿 삭제 권한 검증 및 처리
   *
   * @param template 삭제할 템플릿 엔티티
   */
  public void validateAndPrepareForDeletion(Template template) {
    template.validateDeletePermission(this.spaceId);
  }
}
