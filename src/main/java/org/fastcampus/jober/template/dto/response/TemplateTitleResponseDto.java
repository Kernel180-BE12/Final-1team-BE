package org.fastcampus.jober.template.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 템플릿 제목 응답 DTO
 */
@Schema(description = "템플릿 제목 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateTitleResponseDto {
    
    @Schema(
        description = "템플릿 제목",
        example = "카카오톡 알림 템플릿",
        maxLength = 120
    )
    private String title; // 템플릿 제목
}
