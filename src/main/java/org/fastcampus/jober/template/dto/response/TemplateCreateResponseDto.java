package org.fastcampus.jober.template.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AI 서버로부터의 템플릿 생성 응답을 담는 DTO 클래스 AI Flask 서버의 응답 구조를 프론트엔드가 기대하는 형태로 매핑합니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 템플릿 생성 응답 DTO")
public class TemplateCreateResponseDto {

  @JsonProperty("success")
  @Schema(description = "API 요청 성공 여부", example = "true")
  private Boolean success;

  @JsonProperty("response")
  @Schema(description = "AI 응답 메시지", example = "템플릿을 생성했습니다.")
  private String response;

  @JsonProperty("state")
  @Schema(description = "업데이트된 AI 세션 상태 정보")
  private Map<String, Object> state;

  @JsonProperty("options")
  @Schema(description = "사용자 선택 옵션들")
  private List<String> options;

  @JsonProperty("template")
  @Schema(description = "생성된 템플릿 내용")
  private String template;

  @JsonProperty("structured_template")
  @Schema(description = "구조화된 템플릿 (단수형)")
  private Object structuredTemplate;

  @JsonProperty("editable_variables")
  @Schema(description = "편집 가능한 변수들")
  private Map<String, Object> editableVariables;

  @JsonProperty("structured_templates")
  @Schema(description = "구조화된 템플릿들 (복수형)")
  private List<Object> structuredTemplates;

  @JsonProperty("hasImage")
  @Schema(description = "이미지 포함 여부")
  private Boolean hasImage;
}
