package org.fastcampus.jober.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer forceServers() {
        return openApi -> openApi.setServers(List.of(
                new Server().url("https://api.jober-1team.com"),
                new Server().url("http://localhost:8080")
        ));
    }

}
