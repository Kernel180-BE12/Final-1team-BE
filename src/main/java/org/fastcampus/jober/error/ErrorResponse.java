package org.fastcampus.jober.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private final String code;          // 비즈니스 에러코드 (ex. USER_NOT_FOUND)
    private final String message;       // 사용자용 메시지
    private final int status;           // HTTP status
    private final String path;          // 요청 경로
    private final String requestId;     // MDC나 헤더로 세팅한 요청 ID
    private final Instant timestamp;    // ISO-8601

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, Object> details; // 필드 에러 등 부가정보
}

