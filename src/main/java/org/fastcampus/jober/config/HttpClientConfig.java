package org.fastcampus.jober.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** HTTP 클라이언트 설정 클래스 AI Flask 서버와 통신하기 위한 RestTemplate 빈을 제공합니다. */
@Configuration
public class HttpClientConfig {

  /**
   * RestTemplate 빈 생성 외부 AI Flask 서버와의 HTTP 통신을 위해 사용됩니다.
   *
   * @return RestTemplate 인스턴스
   */
  @Bean
  public RestTemplate restTemplate() {
    // WebClient 쓰기
    return new RestTemplate();
  }
}
