package org.fastcampus.jober.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
            String url = aiFlaskBaseUrl + "/create-template";
            
            // POST 요청을 위한 JSON body 구성
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("message", userMessage);
            
            // HTTP 헤더 설정 (JSON 전송)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            log.info("AI Flask 서버로 템플릿 생성 요청 전송: {}", url);

            // AI Flask 서버로 POST 요청을 보내고 응답을 String으로 받음
            String templateContent = restTemplate.postForObject(url, request, String.class);

            log.info("AI Flask 서버로부터 템플릿 내용 수신 완료");
            
            return templateContent;
            
        } catch (Exception e) {
            log.error("AI Flask 서버와의 통신 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("템플릿 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
