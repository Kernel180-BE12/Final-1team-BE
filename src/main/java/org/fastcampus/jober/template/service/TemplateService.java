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

/**
 * 템플릿 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {

  private final TemplateRepository templateRepository;

  /**
   * 특정 spaceId의 템플릿들의 title만 조회
   * @param spaceId 스페이스 ID
   * @return 템플릿 제목 응답 DTO 리스트
   */
  @Operation(
    summary = "템플릿 제목 조회",
    description = "특정 spaceId의 템플릿 제목들을 조회합니다."
  )
  public List<TemplateTitleResponseDto> getTitlesBySpaceId(
    @Parameter(description = "스페이스 ID", required = true) Long spaceId
  ) {
    return Template.findTitlesBySpaceId(templateRepository, spaceId);
  }

  /**
   * 특정 spaceId의 템플릿들을 조회
   * @param spaceId 스페이스 ID
   * @return 템플릿 엔티티 리스트
   */
  @Operation(
    summary = "템플릿 엔티티 조회",
    description = "특정 spaceId의 템플릿들을 조회합니다."
  )
  public List<Template> getTemplatesBySpaceId(
    @Parameter(description = "스페이스 ID", required = true) Long spaceId
  ) {
    return Template.findBySpaceId(templateRepository, spaceId);
  }
}
