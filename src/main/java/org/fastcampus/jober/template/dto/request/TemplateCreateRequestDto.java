package org.fastcampus.jober.template.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 템플릿 생성 요청을 위한 DTO 클래스
 * 프론트엔드에서 전달받은 사용자 메시지와 AI 세션 상태를 담고,
 * AI Flask 서버 요청 형태로 변환하는 책임을 갖습니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "템플릿 생성 요청 DTO")
public class TemplateCreateRequestDto {
    
    @JsonProperty("message")
    @Schema(description = "템플릿 생성을 위한 사용자 메시지", example = "마케팅용 카카오 알림톡 템플릿을 만들어주세요")
    private String message;
    
    @JsonProperty("state")
    @Schema(description = "AI 세션 상태 정보")
    private TemplateState state;
    
    /**
     * AI Flask 서버 요청용 Map 객체로 변환합니다.
     * 데이터 변환과 null 처리 책임을 DTO가 담당합니다.
     * 
     * @return AI Flask 서버가 기대하는 형태의 요청 body
     */
    public Map<String, Object> toRequestBody() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", this.message);
        requestBody.put("state", this.state != null ? this.state.toMap() : new HashMap<>());
        return requestBody;
    }
}
