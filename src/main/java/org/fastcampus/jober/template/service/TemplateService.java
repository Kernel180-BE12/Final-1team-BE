package org.fastcampus.jober.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 템플릿 관련 비즈니스 로직을 처리하는 서비스 클래스
 * AI Flask 서버와의 통신을 통해 템플릿 생성 등의 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final RestTemplate restTemplate;

    /**
     * AI Flask 서버의 기본 URL
     * application.yml의 ai.flask.base-url 값을 주입받습니다.
     */
    @Value("${ai.flask.base-url}")
    private String aiFlaskBaseUrl;

    /**
     * 사용자 메시지를 기반으로 AI가 템플릿을 생성하도록 요청합니다.
     * 
     * @param userMessage 사용자가 입력한 템플릿 생성 요청 메시지
     * @return AI가 생성한 템플릿 내용 (String 형태)
     * @throws RuntimeException AI 서버 통신 실패 시 발생
     */
    public String createTemplate(String userMessage) {
        try {
            // AI Flask 서버로 보낼 URL 구성
            // GET 방식으로 message 파라미터를 전달
            String url = UriComponentsBuilder.fromHttpUrl(aiFlaskBaseUrl)
                    .path("/create-template")  // AI Flask 서버의 템플릿 생성 엔드포인트
                    .queryParam("message", userMessage)  // 사용자 메시지를 쿼리 파라미터로 전달
                    .toUriString();

            log.info("AI Flask 서버로 템플릿 생성 요청 전송: {}", url);

            // AI Flask 서버로 GET 요청을 보내고 응답을 String으로 받음
            String templateContent = restTemplate.getForObject(url, String.class);

            log.info("AI Flask 서버로부터 템플릿 내용 수신 완료");
            
            return templateContent;
            
        } catch (Exception e) {
            log.error("AI Flask 서버와의 통신 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("템플릿 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
