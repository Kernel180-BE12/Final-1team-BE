package org.fastcampus.jober.template.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateDeleteRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateSaveRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateState;
import org.fastcampus.jober.template.dto.response.TemplateCreateResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateSaveResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.fastcampus.jober.util.ExternalApiUtil;

/** 템플릿 관련 비즈니스 로직을 처리하는 서비스 클래스 AI Flask 서버와의 통신을 통해 템플릿 생성 등의 기능을 제공합니다. */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TemplateService {

  private final ExternalApiUtil externalApiUtil;
  private final TemplateRepository templateRepository;
  private final ObjectMapper objectMapper;
  private final SpaceRepository spaceRepository;

  /** AI Flask 서버의 기본 URL application.yml의 ai.flask.base-url 값을 주입받습니다. */
  @Value("${ai.flask.base-url}")
  private String aiFlaskBaseUrl;

  /** AI Flask 서버의 채팅 API 엔드포인트 application.yml의 ai.flask.chat-endpoint 값을 주입받습니다. */
  @Value("${ai.flask.chat-endpoint}")
  private String aiFlaskChatEndpoint;

  /**
   * 템플릿 생성 요청을 기반으로 AI가 템플릿을 생성하도록 요청합니다.
   *
   * @param request 템플릿 생성 요청 DTO (사용자 메시지와 세션 상태 포함)
   * @return AI가 생성한 구조화된 템플릿 응답 DTO
   * @throws RuntimeException AI 서버 통신 실패 시 발생
   */
  public TemplateCreateResponseDto createTemplate(TemplateCreateRequestDto request) {
    // AI Flask 서버로 보낼 URL 구성
    String url = aiFlaskBaseUrl + aiFlaskChatEndpoint;

    // DTO의 toRequestBody() 메서드를 통해 요청 body 구성
    // 데이터 변환과 null 처리 책임을 DTO에게 위임
    Object requestBody = request.toRequestBody();

    // ExternalApiUtil을 통해 AI Flask 서버로 요청 전송
    Object aiResponse = externalApiUtil.postJson(url, requestBody, Object.class, "AI Flask 서버");

    // AI 응답을 구조화된 DTO로 파싱
    return parseAiResponse(aiResponse);
  }

  /**
   * AI 서버의 원시 응답을 TemplateCreateResponseDto로 파싱합니다.
   *
   * @param aiResponse AI 서버의 원시 응답
   * @return 구조화된 템플릿 생성 응답 DTO
   */
  private TemplateCreateResponseDto parseAiResponse(Object aiResponse) {
    try {
      // Object를 Map으로 변환
      @SuppressWarnings("unchecked")
      Map<String, Object> responseMap = objectMapper.convertValue(aiResponse, Map.class);

      log.info("AI 서버 응답 파싱 시작: {}", responseMap);

      TemplateCreateResponseDto response = new TemplateCreateResponseDto();

      // AI 서버 실제 응답 구조에 맞게 파싱
      response.setMessage((String) responseMap.get("response")); // "response" 필드가 메시지
      response.setTemplateContent((String) responseMap.get("template")); // "template" 필드
      response.setHtmlPreview((String) responseMap.get("htmlPreview"));
      response.setFinalTemplate(
          (String) responseMap.get("structured_template")); // "structured_template"
      response.setParameterizedTemplate((String) responseMap.get("parameterizedTemplate"));

      // editable_variables를 JSON 문자열로 변환
      Object editableVars = responseMap.get("editable_variables");
      if (editableVars != null) {
        response.setExtractedVariables(objectMapper.writeValueAsString(editableVars));
      }

      // options 파싱 (AI 서버에서 "options" 필드로 보냄)
      @SuppressWarnings("unchecked")
      List<String> options = (List<String>) responseMap.get("options");
      response.setTemplateOptions(options);

      // AI 응답의 state 정보 파싱
      @SuppressWarnings("unchecked")
      Map<String, Object> aiStateMap = (Map<String, Object>) responseMap.get("state");

      TemplateState state = new TemplateState();

      if (aiStateMap != null) {
        // AI state에서 직접 정보 추출
        state.setNextAction((String) aiStateMap.get("next_action"));

        // template_pipeline_state에서 상세 정보 추출
        @SuppressWarnings("unchecked")
        Map<String, Object> pipelineState =
            (Map<String, Object>) aiStateMap.get("template_pipeline_state");
        state.setTemplatePipelineState(pipelineState);

        if (pipelineState != null) {
          state.setOriginalRequest((String) pipelineState.get("original_request"));
        }
      }

      response.setState(state);

      log.info(
          "AI 응답 파싱 완료: message={}, next_action={}, original_request={}",
          response.getMessage(),
          state.getNextAction(),
          state.getOriginalRequest());

      return response;
    } catch (Exception e) {
      log.error("AI 응답 파싱 실패: {}", e.getMessage(), e);
      // 파싱 실패 시 기본 응답 반환
      TemplateCreateResponseDto fallbackResponse = new TemplateCreateResponseDto();
      fallbackResponse.setMessage("AI 응답 처리 중 오류가 발생했습니다.");
      return fallbackResponse;
    }
  }

  /**
   * 특정 spaceId의 템플릿들의 title만 조회
   *
   * @param spaceId 스페이스 ID
   * @return 템플릿 제목 응답 DTO 리스트
   */
  @Operation(summary = "템플릿 제목 조회", description = "특정 spaceId의 템플릿 제목들을 조회합니다.")
  public List<TemplateTitleResponseDto> getTitlesBySpaceId(
      @Parameter(description = "스페이스 ID", required = true) Long spaceId) {
    // 스페이스 존재 여부 검증
    spaceRepository.findByIdOrThrow(spaceId);

    return TemplateTitleResponseDto.fromList(templateRepository.findBySpaceId(spaceId));
  }

  /**
   * 특정 spaceId와 templateId의 템플릿 상세 정보를 조회합니다 (completedAt 제외).
   *
   * @param spaceId 스페이스 ID
   * @param templateId 템플릿 ID
   * @return 템플릿 상세 응답 DTO
   */
  public TemplateDetailResponseDto getTemplateDetailBySpaceIdAndTemplateId(
      @Parameter(description = "스페이스 ID", required = true) Long spaceId,
      @Parameter(description = "템플릿 ID", required = true) Long templateId) {
    // 스페이스 존재 여부 검증
    spaceRepository.findByIdOrThrow(spaceId);

    // 템플릿 존재 여부 검증
    Template template =
        templateRepository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId);
    if (template == null) {
      return null;
    }
    return TemplateDetailResponseDto.from(template);
  }

  // /**
  //  * 템플릿 저장 상태를 변경합니다.
  //  * @param id 템플릿 ID
  //  * @param spaceId 스페이스 ID
  //  * @param isSaved 저장 여부
  //  * @return 변경된 저장 상태
  //  */
  // @Transactional
  // public Boolean saveTemplate(Long id, Long spaceId, Boolean isSaved) {
  //     Template template = templateRepository.findByIdAndSpaceId(id, spaceId)
  //             .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "템플릿을 찾을 수 없습니다."));

  //     return template.updateIsSaved(isSaved);
  // }

  /**
   * 템플릿을 저장합니다.
   *
   * @param request 템플릿 저장 요청 DTO
   * @return 템플릿 저장 응답 DTO
   */
  @Transactional
  public TemplateSaveResponseDto saveTemplate(TemplateSaveRequestDto request) {

    // 스페이스 존재 여부 검증
    spaceRepository.findByIdOrThrow(request.getSpaceId());

    // 템플릿 저장
    Template template = templateRepository.save(request.toEntity());
    return TemplateSaveResponseDto.from(template);
  }

  /**
   * 템플릿을 논리적으로 삭제합니다.
   *
   * @param request 템플릿 삭제 요청 DTO
   */
  @Transactional
  public void deleteTemplate(TemplateDeleteRequestDto request) {
    // 스페이스 존재 여부 검증
    spaceRepository.findByIdOrThrow(request.getSpaceId());

    // 템플릿 존재 여부 검증
    Template template =
        templateRepository
            .findByIdAndSpaceId(request.getTemplateId(), request.getSpaceId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "템플릿을 찾을 수 없습니다."));

    // DTO를 통해 권한 검증 및 삭제 준비
    request.validateAndPrepareForDeletion(template);

    // 템플릿 삭제
    template.softDelete();
  }
}
