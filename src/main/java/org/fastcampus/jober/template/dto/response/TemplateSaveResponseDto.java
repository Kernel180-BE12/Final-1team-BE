package org.fastcampus.jober.template.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
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
public class TemplateSaveResponseDto {

    @Schema(description = "저장된 템플릿의 고유 ID", example = "101")
    private Long id;

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;

    @Schema(description = "템플릿 제목", example = "저장된 템플릿 제목")
    private String title;

    @Schema(description = "템플릿 설명", example = "신규 가입 고객에게 보내는 환영 메시지입니다.")
    private String description;

    @Schema(description = "템플릿 타입", example = "환영 메시지")
    private String type;

    // --- ▼▼▼ AI 생성 데이터와 필드명을 일치시킵니다. ▼▼▼ ---

    @Schema(description = "템플릿 원본 내용", example = "안녕하세요, #{고객명}님!")
    private String template;

    @Schema(description = "구조화된 템플릿 객체 (JSON 문자열 형태)")
    private String structuredTemplate; // DB에는 보통 JSON 문자열로 저장됩니다.

    @Schema(description = "편집 가능한 변수 객체 (JSON 문자열 형태)")
    private String editableVariables; // DB에는 보통 JSON 문자열로 저장됩니다.

    @Schema(description = "이미지 포함 여부", example = "false")
    private Boolean hasImage;

    /**
     * Template 엔티티를 TemplateSaveResponseDto로 변환합니다.
     *
     * @param template 템플릿 엔티티
     * @return TemplateSaveResponseDto
     */
    public static TemplateSaveResponseDto from(Template template) {
        return TemplateSaveResponseDto.builder()
                .id(template.getId()) // ID 필드 추가
                .spaceId(template.getSpaceId())
                .title(template.getTitle())
                .description(template.getDescription())
                .type(template.getType())
                .template(template.getTemplate())
                .structuredTemplate(template.getStructuredTemplate())
                .editableVariables(template.getEditableVariables())
                .hasImage(template.getHasImage())
                .build();
    }
}