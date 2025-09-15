package org.fastcampus.jober.template.dto.response;

import org.fastcampus.jober.template.entity.Template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@AllArgsConstructor (access = AccessLevel.PROTECTED)
public class TemplateSaveResponseDto {

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
     * Template 엔티티를 TemplateSaveResponseDto로 변환합니다.
     * @param template 템플릿 엔티티
     * @return TemplateSaveResponseDto
     */
    public static TemplateSaveResponseDto from(Template template) {
        return TemplateSaveResponseDto.builder()
                .spaceId(template.getSpaceId())
                .title(template.getTitle())
                .extractedVariables(template.getExtractedVariables())
                .finalTemplate(template.getFinalTemplate())
                .htmlPreview(template.getHtmlPreview())
                .parameterizedTemplate(template.getParameterizedTemplate())
                .type(template.getType())
                .build();
    }   
}
