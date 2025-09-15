package org.fastcampus.jober.template.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.template.entity.Template;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateDetailResponseDto {
  private Long id;
  private Long spaceId;
  private String title;
  private String status;
  private String extractedVariables;
  // 상세 조회에서 생성일시(completedAt) 조회 제외 - 사용자 요구사항에 따라 제외
  // private LocalDateTime completedAt;
  private String sessionId;
  private String finalTemplate;
  private String htmlPreview;
  private String parameterizedTemplate;
  private Integer totalAttempts;
  private Boolean isSaved;
  private Boolean isAccepted;

  /**
   * Template 엔티티를 TemplateDetailResponseDto로 변환합니다.
   *
   * @param template 템플릿 엔티티
   * @return TemplateDetailResponseDto
   */
  public static TemplateDetailResponseDto from(Template template) {
    if (template == null) {
      return null;
    }

    return TemplateDetailResponseDto.builder()
        .id(template.getId())
        .spaceId(template.getSpaceId())
        .title(template.getTitle())
        .status(template.getStatus() != null ? template.getStatus().name() : null)
        .extractedVariables(template.getExtractedVariables())
        .sessionId(template.getSessionId())
        .finalTemplate(template.getFinalTemplate())
        .htmlPreview(template.getHtmlPreview())
        .parameterizedTemplate(template.getParameterizedTemplate())
        .totalAttempts(template.getTotalAttempts())
        .isSaved(template.getIsSaved())
        .isAccepted(template.getIsAccepted())
        .build();
  }

  /**
   * 이 DTO가 유효한지 검증합니다.
   *
   * @return 유효한 경우 true
   */
  public boolean isValid() {
    return id != null && spaceId != null && title != null && !title.trim().isEmpty();
  }
}
