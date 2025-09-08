package org.fastcampus.jober.template.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 세션 상태 정보를 담는 DTO 클래스
 * AI Flask 서버와의 대화 맥락과 진행 상황을 유지합니다.
 */
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
    
    /**
     * AI Flask 서버가 기대하는 Map 형태로 변환합니다.
     * 
     * @return Map 형태의 상태 정보
     */
    public Map<String, Object> toMap() {
        Map<String, Object> stateMap = new HashMap<>();
        stateMap.put("step", this.step);
        stateMap.put("original_request", this.originalRequest);
        return stateMap;
    }
}
