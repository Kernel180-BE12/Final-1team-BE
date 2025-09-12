package org.fastcampus.jober.template.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplateListResponseDto {
    private String title;
    private String parameterizedTemplate;
}
