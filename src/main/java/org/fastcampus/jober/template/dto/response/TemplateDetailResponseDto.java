package org.fastcampus.jober.template.dto.response;

import java.time.LocalDateTime;
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
    private String description; // description 필드 추가
    private String status;
    private String sessionId;
    private Integer totalAttempts;
    private Boolean isAccepted;
    private Boolean hasImage; // hasImage 필드 추가

    // --- ▼▼▼ 엔티티와 필드명을 일치시킵니다. ▼▼▼ ---
    private String template; // 옛 이름: parameterizedTemplate
    private String structuredTemplate; // 옛 이름: finalTemplate
    private String editableVariables; // 옛 이름: extractedVariables

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
                .description(template.getDescription()) // description 추가
                .status(template.getStatus() != null ? template.getStatus().name() : null)
                .sessionId(template.getSessionId())
                .totalAttempts(template.getTotalAttempts())
                .isAccepted(template.getIsAccepted())
                .hasImage(template.getHasImage()) // hasImage 추가
                .template(template.getTemplate()) // 필드명 변경
                .structuredTemplate(template.getStructuredTemplate()) // 필드명 변경
                .editableVariables(template.getEditableVariables()) // 필드명 변경
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