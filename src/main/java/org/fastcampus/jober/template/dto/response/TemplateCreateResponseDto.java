package org.fastcampus.jober.template.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.template.dto.request.TemplateState;

import java.util.List;

/**
 * AI 서버로부터의 템플릿 생성 응답을 담는 DTO 클래스
 * AI Flask 서버의 구조화된 응답을 프론트엔드로 전달합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 템플릿 생성 응답 DTO")
public class TemplateCreateResponseDto {
    
    @JsonProperty("message")
    @Schema(description = "AI 응답 메시지", example = "템플릿을 생성했습니다.")
    private String message;
    
    @JsonProperty("state")
    @Schema(description = "업데이트된 AI 세션 상태 정보")
    private TemplateState state;
    
    @JsonProperty("templateContent")
    @Schema(description = "생성된 템플릿 내용")
    private String templateContent;
    
    @JsonProperty("templateOptions")
    @Schema(description = "템플릿 선택 옵션들")
    private List<String> templateOptions;
    
    @JsonProperty("htmlPreview")
    @Schema(description = "템플릿 HTML 미리보기")
    private String htmlPreview;
    
    @JsonProperty("finalTemplate")
    @Schema(description = "최종 템플릿 내용")
    private String finalTemplate;
    
    @JsonProperty("parameterizedTemplate")
    @Schema(description = "매개변수화된 템플릿")
    private String parameterizedTemplate;
    
    @JsonProperty("extractedVariables")
    @Schema(description = "추출된 변수들")
    private String extractedVariables;
}