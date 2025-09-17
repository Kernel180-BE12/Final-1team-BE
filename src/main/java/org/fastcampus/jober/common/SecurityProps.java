package org.fastcampus.jober.common;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProps {
  private List<String> permitAll = List.of();
  private List<String> permitUser = List.of();
  private List<String> corsAllowedOrigins = List.of();
}
