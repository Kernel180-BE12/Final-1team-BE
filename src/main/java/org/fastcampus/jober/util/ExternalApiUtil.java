package org.fastcampus.jober.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.template.dto.request.TemplateState;
import org.fastcampus.jober.template.dto.response.TemplateCreateResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 외부 API 호출을 위한 유틸리티 클래스 HTTP 통신, 로깅, 에러 처리를 담당합니다. */
@Slf4j
@Component
public class ExternalApiUtil {

  private final RestTemplate restTemplate;
  private final WebClient webClient;
  private final ObjectMapper objectMapper;

    public ExternalApiUtil(WebClient.Builder builder, RestTemplate restTemplate, @Value("${ai.flask.base-url}") String aiFlaskBaseUrl, ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl(aiFlaskBaseUrl).build();
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Flux<TemplateCreateResponseDto> stream(Object requestBody, String url) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(Object.class)
                .filter(Objects::nonNull)
                .map(this::parseAiResponse);
    }

  /**
   * 외부 API로 JSON POST 요청을 전송합니다.
   *
   * @param url 요청할 URL
   * @param requestBody 요청 body (JSON으로 변환됨)
   * @param responseType 응답 타입 클래스
   * @param apiName API 이름 (로깅용)
   * @return API 응답 객체
   * @throws RuntimeException API 호출 실패 시 발생
   */
  public <T> T postJson(String url, Object requestBody, Class<T> responseType, String apiName) {
    try {
      // HTTP 헤더 설정 (JSON 전송)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

      log.info("{}로 요청 전송: {}", apiName, url);
      log.info("전송할 JSON: {}", requestBody);

      // 외부 API로 POST 요청을 보내고 응답을 받음
      T apiResponse = restTemplate.postForObject(url, request, responseType);

      log.info("{}로부터 응답 수신 완료", apiName);

      if (responseType == TemplateCreateResponseDto.class) {
          return responseType.cast(parseAiResponse(apiResponse));
      } else {
          return apiResponse;
      }
//      return apiResponse;

    } catch (Exception e) {
      log.error("{} 통신 중 오류 발생: {}", apiName, e.getMessage(), e);
      throw new RuntimeException(apiName + " 호출 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

    /**
     * AI 서버의 원시 응답을 TemplateCreateResponseDto로 파싱합니다. 안전한 타입 캐스팅과 null 처리를 통해 파싱 오류를 방지합니다.
     *
     * @param aiResponse AI 서버의 원시 응답
     * @return 구조화된 템플릿 생성 응답 DTO
     */
    private TemplateCreateResponseDto parseAiResponse(Object aiResponse) {
        try {
            log.info("AIRESPONSE {}", aiResponse.toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.convertValue(aiResponse, Map.class);

            log.info("AI 서버 응답 파싱 시작: {}", responseMap);

            TemplateCreateResponseDto responseDto = new TemplateCreateResponseDto();

            // DTO의 새 구조에 맞춰 필드 값을 설정합니다.
            responseDto.setSuccess(safeGetBoolean(responseMap, "success"));
            responseDto.setResponse(safeGetString(responseMap, "response"));
            responseDto.setTemplate(safeGetString(responseMap, "template"));
            responseDto.setOptions(safeGetList(responseMap, "options"));
            responseDto.setStructuredTemplate(responseMap.get("structured_template"));
            responseDto.setStructuredTemplates(safeGetList(responseMap, "structured_templates"));
            responseDto.setEditableVariables(safeGetMap(responseMap, "editable_variables"));
            responseDto.setHasImage(safeGetBoolean(responseMap, "hasImage"));
            responseDto.setState(safeParseState(responseMap, "state"));

            log.info("AI 응답 파싱 완료: success={}, response={}", responseDto.getSuccess(), responseDto.getResponse());
            return responseDto;

        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage(), e);
            return createFallbackResponse();
        }
    }

    // --- 타입별로 안전하게 값을 가져오는 헬퍼 메소드들 ---

    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean safeGetBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false; // 기본값은 false로 설정
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> safeGetList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            try {
                // 모든 원소가 elementType과 일치하는지 확인하는 로직을 추가할 수 있으나,
                // 현재는 신뢰하고 캐스팅합니다.
                return (List<T>) value;
            } catch (ClassCastException e) {
                log.warn("리스트 타입 캐스팅 실패 - key: {}, value: {}", key, value);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList(); // 리스트가 아니거나 없으면 빈 리스트 반환
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeGetMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    /** Map에서 TemplateState를 안전하게 파싱합니다. */
    private TemplateState safeParseState(Map<String, Object> map, String key) {
        Object stateObj = map.get(key);
        if (stateObj instanceof Map) {
            try {
                // Map을 JSON 문자열로 변환 후, 다시 TemplateState 객체로 파싱
                String stateJson = objectMapper.writeValueAsString(stateObj);
                return objectMapper.readValue(stateJson, TemplateState.class);
            } catch (JsonProcessingException e) {
                log.warn("State 파싱 중 JSON 오류 발생: {}", e.getMessage());
            }
        }
        return new TemplateState(); // 파싱 실패 시 빈 객체 반환
    }

    /** 파싱 실패 시 반환할 기본 응답을 생성합니다. */
    private TemplateCreateResponseDto createFallbackResponse() {
        TemplateCreateResponseDto fallback = new TemplateCreateResponseDto();
        fallback.setSuccess(false); // 실패 상태 명시
        fallback.setResponse("AI 응답 처리 중 오류가 발생했습니다.");
        fallback.setState(new TemplateState());
        return fallback;
    }
}
