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
import java.util.stream.Collectors;

/**
 * 템플릿 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {
    
    private final TemplateRepository templateRepository;
    
    /**
     * spaceID가 0이 아닌 템플릿들의 title만 조회
     * @param spaceId 스페이스 ID (0이 아닌 값)
     * @return 템플릿 제목 응답 DTO 리스트
     */
    @Operation(
        summary = "템플릿 제목 조회",
        description = "spaceID가 0이 아닌 특정 spaceId의 템플릿 제목들을 조회합니다."
    )
    public List<TemplateTitleResponseDto> getTitlesBySpaceIdNotZero(
        @Parameter(description = "스페이스 ID (0이 아닌 값)", required = true) Long spaceId
    ) {
        // spaceId가 0인지 검증
        if (spaceId == null || spaceId == 0L) {
            throw new IllegalArgumentException("spaceId는 0이 아닌 값이어야 합니다.");
        }
        
        List<String> titles = templateRepository.findTitlesBySpaceIdNotZero(spaceId);
        
        return titles.stream()
                .map(title -> TemplateTitleResponseDto.builder()
                        .title(title)
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * spaceID가 0이 아닌 템플릿들을 조회
     * @param spaceId 스페이스 ID (0이 아닌 값)
     * @return 템플릿 엔티티 리스트
     */
    @Operation(
        summary = "템플릿 엔티티 조회",
        description = "spaceID가 0이 아닌 특정 spaceId의 템플릿들을 조회합니다."
    )
    public List<Template> getTemplatesBySpaceIdNotZero(
        @Parameter(description = "스페이스 ID (0이 아닌 값)", required = true) Long spaceId
    ) {
        // spaceId가 0인지 검증
        if (spaceId == null || spaceId == 0L) {
            throw new IllegalArgumentException("spaceId는 0이 아닌 값이어야 합니다.");
        }
        
        return templateRepository.findBySpaceIdNotZero(spaceId);
    }
}
