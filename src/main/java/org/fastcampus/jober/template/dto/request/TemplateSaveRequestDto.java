package org.fastcampus.jober.template.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.fastcampus.jober.template.entity.Template;

@Getter
public class TemplateSaveRequestDto {

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;

    @Schema(description = "템플릿 제목", example = "새로운 템플릿 제목")
    private String title;

    @Schema(description = "템플릿 설명", example = "신규 가입 고객 환영 메시지")
    private String description;

    @Schema(description = "템플릿 타입", example = "환영/알림")
    private String type;

    // --- ▼▼▼ AI 생성 데이터와 필드명을 일치시킵니다. ▼▼▼ ---

    @Schema(description = "템플릿 원본 내용", example = "안녕하세요, #{고객명}님!")
    private String template; // 옛 이름: parameterizedTemplate

    @Schema(description = "구조화된 템플릿 객체 (JSON 문자열 형태)")
    private String structuredTemplate; // 옛 이름: finalTemplate

    @Schema(description = "편집 가능한 변수 객체 (JSON 문자열 형태)")
    private String editableVariables; // 옛 이름: extractedVariables

    @Schema(description = "이미지 포함 여부", example = "false")
    private Boolean hasImage;

    /**
     * DTO를 Template 엔티티로 변환합니다.
     *
     * @return Template 엔티티
     */
    public Template toEntity() {
        return Template.builder()
                .spaceId(spaceId)
                .title(title)
                .description(description) // 'description' 필드 추가
                .type(type)
                .template(template) // 필드명 변경: parameterizedTemplate -> template
                .structuredTemplate(structuredTemplate) // 필드명 변경: finalTemplate -> structuredTemplate
                .editableVariables(editableVariables) // 필드명 변경: extractedVariables -> editableVariables
                .hasImage(hasImage) // 'hasImage' 필드 추가
                .isSaved(true) // 저장 요청이므로 isSaved 상태를 true로 설정
                .build();
    }
}