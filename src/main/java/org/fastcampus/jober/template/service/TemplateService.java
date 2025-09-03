package org.fastcampus.jober.template.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;

/**
 * 템플릿 관련 비즈니스 로직을 처리하는 서비스 클래스
 * AI Flask 서버와의 통신을 통해 템플릿 생성 등의 기능을 제공합니다.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final RestTemplate restTemplate;

    /**
     * AI Flask 서버의 기본 URL
     * application.yml의 ai.flask.base-url 값을 주입받습니다.
     */
    @Value("${ai.flask.base-url}")
    private String aiFlaskBaseUrl;

    @Transactional
    public Boolean saveTemplate(Long id, Long spaceId, Boolean isSaved) {
        Template template = templateRepository.findByIdAndSpaceId(id, spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "템플릿을 찾을 수 없습니다."));

        return template.updateIsSaved(isSaved);
    }

  /**
   * 특정 spaceId의 템플릿들의 title만 조회
   * @param spaceId 스페이스 ID
   * @return 템플릿 제목 응답 DTO 리스트
   */
  @Operation(
    summary = "템플릿 제목 조회",
    description = "특정 spaceId의 템플릿 제목들을 조회합니다."
  )
  public List<TemplateTitleResponseDto> getTitlesBySpaceId(
    @Parameter(description = "스페이스 ID", required = true) Long spaceId
  ) {
    return Template.findTitlesBySpaceId(templateRepository, spaceId);
  }

  /**
   * 특정 spaceId의 템플릿들을 조회
   * @param spaceId 스페이스 ID
   * @return 템플릿 엔티티 리스트
   */
  @Operation(
    summary = "템플릿 엔티티 조회",
    description = "특정 spaceId의 템플릿들을 조회합니다."
  )
  public List<Template> getTemplatesBySpaceId(
    @Parameter(description = "스페이스 ID", required = true) Long spaceId
  ) {
    return Template.findBySpaceId(templateRepository, spaceId);
  }

    /**
     * 사용자 메시지를 기반으로 AI가 템플릿을 생성하도록 요청합니다.
     *
     * @param userMessage 사용자가 입력한 템플릿 생성 요청 메시지
     * @return AI가 생성한 템플릿 내용 (String 형태)
     * @throws RuntimeException AI 서버 통신 실패 시 발생
     */
    public Object createTemplate(String userMessage, Map<String, Object> sessionState) {
        try {
            // AI Flask 서버로 보낼 URL 구성
            String url = aiFlaskBaseUrl + "/api/chat";

            // POST 요청을 위한 JSON body 구성 (message + state)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", userMessage);
            requestBody.put("state", sessionState != null ? sessionState : new HashMap<>());

            // HTTP 헤더 설정 (JSON 전송)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("AI Flask 서버로 템플릿 생성 요청 전송: {}", url);
            log.info("전송할 JSON: {}", requestBody);

            // AI Flask 서버로 POST 요청을 보내고 응답을 Object로 받음 (JSON 전체)
            Object aiResponse = restTemplate.postForObject(url, request, Object.class);

            log.info("AI Flask 서버로부터 응답 수신 완료");

            return aiResponse;

        } catch (Exception e) {
            log.error("AI Flask 서버와의 통신 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("템플릿 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
