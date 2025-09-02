package org.fastcampus.jober.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProps {
    private List<String> permitAll = List.of();
    private List<String> permitPostUser = List.of();
}
