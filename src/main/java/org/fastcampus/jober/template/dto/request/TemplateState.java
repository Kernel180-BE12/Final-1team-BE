package org.fastcampus.jober.template.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** AI 세션 상태 정보를 담는 DTO 클래스 AI Flask 서버와의 대화 맥락과 진행 상황을 유지합니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 세션 상태 정보")
public class TemplateState {

  @JsonProperty("step")
  @Schema(description = "현재 대화 단계", example = "initial")
  private String step;

  @JsonProperty("original_request")
  @Schema(description = "최초 사용자 요청", example = "마케팅용 카카오톡 템플릿 만들기")
  private String originalRequest;

  @JsonProperty("selected_style")
  @Schema(description = "사용자가 선택한 템플릿 스타일", example = "기본형")
  private String selectedStyle;

  @JsonProperty("selected_template")
  @Schema(description = "사용자가 선택한 유사 템플릿 내용")
  private String selectedTemplate;

  @JsonProperty("validation_result")
  @Schema(description = "템플릿 검증 결과")
  private Map<String, Object> validationResult;

  @JsonProperty("correction_attempts")
  @Schema(description = "템플릿 수정 시도 횟수", example = "0")
  private Integer correctionAttempts;

  @JsonProperty("next_action")
  @Schema(description = "다음 수행할 액션", example = "awaiting_confirmation")
  private String nextAction;

  @JsonProperty("template_pipeline_state")
  @Schema(description = "템플릿 생성 파이프라인의 세부 상태")
  private Map<String, Object> templatePipelineState;

  /**
   * AI Flask 서버가 기대하는 Map 형태로 변환합니다.
   * 하위 호환성을 보장하기 위해 기존 필드는 기본값으로, 새 필드는 null이 아닐 때만 포함합니다.
   *
   * @return Map 형태의 상태 정보
   */
  public Map<String, Object> toMap() {
    Map<String, Object> stateMap = new HashMap<>();

    // 기존 필드 (하위 호환성 보장 - 기본값 제공)
    stateMap.put("step", this.step != null ? this.step : "initial");
    stateMap.put("original_request", this.originalRequest != null ? this.originalRequest : "");

    // 새 필드들 (null이 아닐 때만 추가 - AI 서버 에러 방지)
    if (this.selectedStyle != null) {
      stateMap.put("selected_style", this.selectedStyle);
    }
    if (this.selectedTemplate != null) {
      stateMap.put("selected_template", this.selectedTemplate);
    }
    if (this.validationResult != null) {
      stateMap.put("validation_result", this.validationResult);
    }
    if (this.correctionAttempts != null) {
      stateMap.put("correction_attempts", this.correctionAttempts);
    }
    if (this.nextAction != null) {
      stateMap.put("next_action", this.nextAction);
    }
    if (this.templatePipelineState != null) {
      stateMap.put("template_pipeline_state", this.templatePipelineState);
    }

    return stateMap;
  }
}
