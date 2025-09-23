package org.fastcampus.jober.template.service;

import java.util.List;
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

    public Flux<TemplateCreateResponseDto> templateSSE(TemplateCreateRequestDto templateCreateRequestDto) {
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
        return externalApiUtil.postJson(url, requestBody, TemplateCreateResponseDto.class, "AI Flask 서버");
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