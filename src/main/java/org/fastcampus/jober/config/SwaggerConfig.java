package org.fastcampus.jober.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Jober Spring boot Server API")
                .version("v1.0")
                .description("프로젝트 백엔드 API 문서입니다."));
  }
}

// @Bean
// public OpenAPI openAPI() {
//    return new OpenAPI()
//            .info(new Info()
//                    .title("My Service API")
//                    .version("v1.0.0")
//                    .description("설명...")
//                    .contact(new Contact().name("SoonYang Hur").email("soonyang@example.com")))
//            .addServersItem(new Server().url("/")) // 리버스 프록시면 외부 기준 URL 지정
//            .components(new Components()
//                    .addSecuritySchemes("bearerAuth",
//                            new SecurityScheme()
//                                    .type(SecurityScheme.Type.HTTP)
//                                    .scheme("bearer")
//                                    .bearerFormat("JWT")))
//            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
// }
