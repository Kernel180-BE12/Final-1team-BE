package org.fastcampus.jober.template.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "템플릿 생성 요청 DTO")
public class TemplateCreateRequestDto {
    
    @JsonProperty("message")
    @Schema(description = "템플릿 생성을 위한 사용자 메시지", example = "마케팅용 카카오 알림톡 템플릿을 만들어주세요")
    private String message;
    
    @JsonProperty("state")
    @Schema(description = "AI 세션 상태 정보", example = "{\"step\":\"initial\",\"original_request\":\"\"}")
    private Map<String, Object> state;
}
