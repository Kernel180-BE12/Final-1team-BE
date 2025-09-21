package org.fastcampus.jober.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/** 외부 API 호출을 위한 유틸리티 클래스 HTTP 통신, 로깅, 에러 처리를 담당합니다. */
@Slf4j
@Component
public class ExternalApiUtil {

  private final RestTemplate restTemplate;
  private final WebClient webClient;

    public ExternalApiUtil(WebClient.Builder builder, RestTemplate restTemplate, @Value("${ai.flask.base-url}") String aiFlaskBaseUrl) {
        this.webClient = builder.baseUrl(aiFlaskBaseUrl).build();
        this.restTemplate = restTemplate;
    }

    public Flux<String> stream(Object requestBody, String url) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);
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

      return apiResponse;

    } catch (Exception e) {
      log.error("{} 통신 중 오류 발생: {}", apiName, e.getMessage(), e);
      throw new RuntimeException(apiName + " 호출 중 오류가 발생했습니다: " + e.getMessage());
    }
  }
}
