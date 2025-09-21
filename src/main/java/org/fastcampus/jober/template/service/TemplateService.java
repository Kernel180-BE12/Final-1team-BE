package org.fastcampus.jober.template.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fastcampus.jober.template.dto.response.*;
import org.fastcampus.jober.user.dto.CustomUserDetails;
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
import org.fastcampus.jober.util.ExternalApiUtil;
import reactor.core.publisher.Flux;

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

    public Flux<String> templateSSE(TemplateCreateRequestDto templateCreateRequestDto) {
        Map<String, Object> requestBody = templateCreateRequestDto.toRequestBody();
        log.info("requestBody {}", requestBody);
        return externalApiUtil.stream(templateCreateRequestDto.toRequestBody(), aiFlaskChatEndpoint);
    }

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
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.convertValue(aiResponse, Map.class);

            log.info("AI 서버 응답 파싱 시작: {}", responseMap);

            TemplateCreateResponseDto responseDto = new TemplateCreateResponseDto();

            // DTO의 새 구조에 맞춰 필드 값을 설정합니다.
            responseDto.setSuccess(safeGetBoolean(responseMap, "success"));
            responseDto.setResponse(safeGetString(responseMap, "response"));
            responseDto.setTemplate(safeGetString(responseMap, "template"));
            responseDto.setOptions(safeGetList(responseMap, "options", String.class));
            responseDto.setStructuredTemplate(responseMap.get("structured_template"));
            responseDto.setStructuredTemplates(safeGetList(responseMap, "structured_templates", Object.class));
            responseDto.setEditableVariables(safeGetMap(responseMap, "editable_variables"));
            responseDto.setHasImage(safeGetBoolean(responseMap, "hasImage"));
            responseDto.setState(safeParseState(responseMap, "state"));

            log.info("AI 응답 파싱 완료: success={}, response={}", responseDto.getSuccess(), responseDto.getResponse());
            return responseDto;

        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage(), e);
            return createFallbackResponse();
        }
    }

    // --- 타입별로 안전하게 값을 가져오는 헬퍼 메소드들 ---

    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean safeGetBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false; // 기본값은 false로 설정
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> safeGetList(Map<String, Object> map, String key, Class<T> elementType) {
        Object value = map.get(key);
        if (value instanceof List) {
            try {
                // 모든 원소가 elementType과 일치하는지 확인하는 로직을 추가할 수 있으나,
                // 현재는 신뢰하고 캐스팅합니다.
                return (List<T>) value;
            } catch (ClassCastException e) {
                log.warn("리스트 타입 캐스팅 실패 - key: {}, value: {}", key, value);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList(); // 리스트가 아니거나 없으면 빈 리스트 반환
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeGetMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    /** Map에서 TemplateState를 안전하게 파싱합니다. */
    private TemplateState safeParseState(Map<String, Object> map, String key) {
        Object stateObj = map.get(key);
        if (stateObj instanceof Map) {
            try {
                // Map을 JSON 문자열로 변환 후, 다시 TemplateState 객체로 파싱
                String stateJson = objectMapper.writeValueAsString(stateObj);
                return objectMapper.readValue(stateJson, TemplateState.class);
            } catch (JsonProcessingException e) {
                log.warn("State 파싱 중 JSON 오류 발생: {}", e.getMessage());
            }
        }
        return new TemplateState(); // 파싱 실패 시 빈 객체 반환
    }

    /** 파싱 실패 시 반환할 기본 응답을 생성합니다. */
    private TemplateCreateResponseDto createFallbackResponse() {
        TemplateCreateResponseDto fallback = new TemplateCreateResponseDto();
        fallback.setSuccess(false); // 실패 상태 명시
        fallback.setResponse("AI 응답 처리 중 오류가 발생했습니다.");
        fallback.setState(new TemplateState());
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

    public List<TemplateListResponseDto> getTemplateList(CustomUserDetails principal, Long spaceId) {
        return templateRepository.findAllBySpaceIdAndUserId(principal.getUserId(), spaceId);
    }
}