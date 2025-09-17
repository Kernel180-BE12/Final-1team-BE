//package org.fastcampus.jober.template.dto.response;
//
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import org.fastcampus.jober.template.dto.request.TemplateState;
//
///** AI 서버로부터의 템플릿 생성 응답을 담는 DTO 클래스 AI Flask 서버의 구조화된 응답을 프론트엔드로 전달합니다. */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Schema(description = "AI 템플릿 생성 응답 DTO")
//public class TemplateCreateResponseDto {
//
//  @JsonProperty("message")
//  @Schema(description = "AI 응답 메시지", example = "템플릿을 생성했습니다.")
//  private String message;
//
//  @JsonProperty("state")
//  @Schema(description = "업데이트된 AI 세션 상태 정보")
//  private TemplateState state;
//
//  @JsonProperty("templateContent")
//  @Schema(description = "생성된 템플릿 내용")
//  private String templateContent;
//
//  @JsonProperty("templateOptions")
//  @Schema(description = "템플릿 선택 옵션들")
//  private List<String> templateOptions;
//
//  @JsonProperty("htmlPreview")
//  @Schema(description = "템플릿 HTML 미리보기")
//  private String htmlPreview;
//
//  @JsonProperty("finalTemplate")
//  @Schema(description = "최종 템플릿 내용")
//  private String finalTemplate;
//
//  @JsonProperty("parameterizedTemplate")
//  @Schema(description = "매개변수화된 템플릿")
//  private String parameterizedTemplate;
//
//  @JsonProperty("extractedVariables")
//  @Schema(description = "추출된 변수들")
//  private String extractedVariables;
//}



package org.fastcampus.jober.template.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.template.dto.request.TemplateState;

/**
 * AI 서버(Flask)로부터 오는 응답을 담는 DTO 클래스.
 * AI 서버의 JSON 응답 구조와 정확히 일치해야 합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateResponseDto {

    @JsonProperty("success")
    private Boolean success;

    // AI 서버의 'response' 키에 해당합니다. 필드명도 일관성을 위해 response로 변경합니다.
    @JsonProperty("response")
    private String response;

    @JsonProperty("state")
    private TemplateState state;

    // AI 서버의 'options' 키에 해당합니다.
    @JsonProperty("options")
    private List<String> options;

    // AI 서버의 'template' 키에 해당합니다.
    @JsonProperty("template")
    private String template;

    // AI 서버의 'structured_template' 키에 해당합니다.
    // 임시로 Object를 사용하거나, 이 구조에 맞는 새로운 DTO(예: StructuredTemplateDto)를 만들어 사용해야 합니다.
    @JsonProperty("structured_template")
    private Object structuredTemplate;

    // AI 서버의 'editable_variables' 키에 해당합니다. Python의 dict는 Java의 Map<String, Object>에 해당합니다.
    @JsonProperty("editable_variables")
    private Map<String, Object> editableVariables;

    // AI 서버의 'structured_templates' 키에 해당합니다.
    @JsonProperty("structured_templates")
    private List<Object> structuredTemplates;

    // AI 서버의 'hasImage' 키에 해당합니다.
    @JsonProperty("hasImage")
    private Boolean hasImage;
}