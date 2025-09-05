package org.fastcampus.jober.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.fastcampus.jober.util.ExternalApiUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 템플릿 관련 비즈니스 로직을 처리하는 서비스 클래스
 * AI Flask 서버와의 통신을 통해 템플릿 생성 등의 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final ExternalApiUtil externalApiUtil;

    /**
     * AI Flask 서버의 기본 URL
     * application.yml의 ai.flask.base-url 값을 주입받습니다.
     */
    @Value("${ai.flask.base-url}")
    private String aiFlaskBaseUrl;

    /**
     * AI Flask 서버의 채팅 API 엔드포인트
     * application.yml의 ai.flask.chat-endpoint 값을 주입받습니다.
     */
    @Value("${ai.flask.chat-endpoint}")
    private String aiFlaskChatEndpoint;

    /**
     * 템플릿 생성 요청을 기반으로 AI가 템플릿을 생성하도록 요청합니다.
     *
     * @param request 템플릿 생성 요청 DTO (사용자 메시지와 세션 상태 포함)
     * @return AI가 생성한 템플릿 내용 (Object 형태)
     * @throws RuntimeException AI 서버 통신 실패 시 발생
     */
    public Object createTemplate(TemplateCreateRequestDto request) {
        // AI Flask 서버로 보낼 URL 구성
        String url = aiFlaskBaseUrl + aiFlaskChatEndpoint;

        // DTO의 toRequestBody() 메서드를 통해 요청 body 구성
        // 데이터 변환과 null 처리 책임을 DTO에게 위임
        Object requestBody = request.toRequestBody();

        // ExternalApiUtil을 통해 AI Flask 서버로 요청 전송
        return externalApiUtil.postJson(url, requestBody, Object.class, "AI Flask 서버");
    }
}
