package org.fastcampus.jober.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenApiCustomizer forceServers() {
    return openApi ->
        openApi.setServers(
            List.of(
                new Server().url("https://api.jober-1team.com"),
                new Server().url("http://localhost:8080")));
  }
}
