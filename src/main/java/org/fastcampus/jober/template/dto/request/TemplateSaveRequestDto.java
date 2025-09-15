package org.fastcampus.jober.template.dto.request;


import org.fastcampus.jober.template.entity.Template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TemplateSaveRequestDto {

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;
    @Schema(description = "템플릿 제목", example = "템플릿 제목")
    private String title;
    @Schema(description = "추출된 변수", example = "추출된 변수")
    private String extractedVariables;
    @Schema(description = "최종 템플릿", example = "최종 템플릿")
    private String finalTemplate;
    @Schema(description = "HTML 미리보기", example = "HTML 미리보기")
    private String htmlPreview;
    @Schema(description = "파라미터화된 템플릿", example = "파라미터화된 템플릿")
    private String parameterizedTemplate;
    @Schema(description = "타입", example = "타입")
    private String type;

    /**
     * Template 엔티티로 변환
     * @return Template 엔티티
     */
    public Template toEntity() {
        return Template.builder()
                .spaceId(spaceId)
                .title(title)
                .extractedVariables(extractedVariables)
                .finalTemplate(finalTemplate)
                .htmlPreview(htmlPreview)
                .parameterizedTemplate(parameterizedTemplate)
                .type(type)
                .build();
    }
}
