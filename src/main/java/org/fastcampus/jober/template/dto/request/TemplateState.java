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
   *
   * @return Map 형태의 상태 정보
   */
  public Map<String, Object> toMap() {
    Map<String, Object> stateMap = new HashMap<>();
    stateMap.put("step", this.step);
    stateMap.put("original_request", this.originalRequest);
    stateMap.put("selected_style", this.selectedStyle);
    stateMap.put("selected_template", this.selectedTemplate);
    stateMap.put("validation_result", this.validationResult);
    stateMap.put("correction_attempts", this.correctionAttempts);
    stateMap.put("next_action", this.nextAction);
    stateMap.put("template_pipeline_state", this.templatePipelineState);
    return stateMap;
  }
}
