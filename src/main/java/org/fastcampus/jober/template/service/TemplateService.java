package org.fastcampus.jober.template.service;

import java.util.List;
import java.util.Map;

import org.fastcampus.jober.template.dto.response.*;
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
import org.fastcampus.jober.template.entity.Template;
import org.fastcampus.jober.template.repository.TemplateRepository;
import org.fastcampus.jober.user.dto.CustomUserDetails;
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
   * AI 서버의 원시 응답을 TemplateCreateResponseDto로 파싱합니다. 안전한 타입 캐스팅과 null 처리를 통해 파싱 오류를 방지합니다.
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

      // 안전한 문자열 추출 (null 처리 포함)
      response.setMessage(safeGetString(responseMap, "response"));
      response.setTemplateContent(safeGetString(responseMap, "template"));
      response.setHtmlPreview(safeGetString(responseMap, "htmlPreview"));
      response.setFinalTemplate(safeGetString(responseMap, "structured_template"));
      response.setParameterizedTemplate(safeGetString(responseMap, "parameterizedTemplate"));

      // 안전한 JSON 변환
      response.setExtractedVariables(safeConvertToJson(responseMap, "editable_variables"));

      // 안전한 리스트 추출
      response.setTemplateOptions(safeGetStringList(responseMap, "options"));

      // 안전한 state 파싱
      response.setState(safeParseState(responseMap, "state"));

      log.info("AI 응답 파싱 완료: message={}", response.getMessage());
      return response;

    } catch (Exception e) {
      log.error("AI 응답 파싱 실패: {}", e.getMessage(), e);
      return createFallbackResponse();
    }
  }

  /** Map에서 문자열 값을 안전하게 추출합니다. */
  private String safeGetString(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value != null ? value.toString() : null;
  }

  /** Map에서 JSON 변환을 안전하게 수행합니다. */
  private String safeConvertToJson(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value == null) return null;

    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      log.warn("JSON 변환 실패 - key: {}, value: {}", key, value);
      return null;
    }
  }

  /** Map에서 문자열 리스트를 안전하게 추출합니다. */
  @SuppressWarnings("unchecked")
  private List<String> safeGetStringList(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value instanceof List) {
      try {
        return (List<String>) value;
      } catch (ClassCastException e) {
        log.warn("리스트 타입 캐스팅 실패 - key: {}, value: {}", key, value);
        return null;
      }
    }
    return null;
  }

  /** Map에서 TemplateState를 안전하게 파싱합니다. */
  private TemplateState safeParseState(Map<String, Object> map, String key) {
    @SuppressWarnings("unchecked")
    Map<String, Object> stateMap = (Map<String, Object>) map.get(key);

    TemplateState state = new TemplateState();

    if (stateMap != null) {
      try {
        state.setNextAction(safeGetString(stateMap, "next_action"));

        @SuppressWarnings("unchecked")
        Map<String, Object> pipelineState =
            (Map<String, Object>) stateMap.get("template_pipeline_state");
        state.setTemplatePipelineState(pipelineState);

        if (pipelineState != null) {
          state.setOriginalRequest(safeGetString(pipelineState, "original_request"));
        }
      } catch (Exception e) {
        log.warn("State 파싱 중 오류 발생: {}", e.getMessage());
      }
    }

    return state;
  }

  /** 파싱 실패 시 반환할 기본 응답을 생성합니다. */
  private TemplateCreateResponseDto createFallbackResponse() {
    TemplateCreateResponseDto fallback = new TemplateCreateResponseDto();
    fallback.setMessage("AI 응답 처리 중 오류가 발생했습니다.");
    fallback.setState(new TemplateState()); // 빈 state 객체
    return fallback;
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

    public List<TemplateListResponseDto> getTemlpateList(CustomUserDetails principal) {
        return templateRepository.findTemplateByUserId(principal.getUserId());
    }
}
