package org.fastcampus.jober.template.controller;

/**
 * 템플릿 관련 HTTP 요청을 처리하는 컨트롤러
 */
import lombok.extern.slf4j.Slf4j;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.service.TemplateService;

import java.util.List;

import org.springframework.web.bind.annotation.*;


/**
 * 템플릿 관련 REST API를 제공하는 컨트롤러 클래스
 * 템플릿 생성, 조회, 수정, 삭제 등의 기능을 처리합니다.
 */
@Slf4j
@Tag(name = "Template", description = "템플릿 관련 API")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
@Tag(name = "Template", description = "템플릿 관련 API")
public class TemplateController {

    private final TemplateService templateService;

    /**
     * GET 방식으로 spaceId를 받아서 해당 spaceId의 템플릿 title들을 조회하는 API
     * @param spaceId 스페이스 ID
     * @return 템플릿 제목 리스트
     */
    @Operation(
        summary = "템플릿 제목 조회",
        description = "특정 spaceId의 템플릿 제목들을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                schema = @Schema(
                    type = "array",
                    implementation = TemplateTitleResponseDto.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 spaceId의 템플릿을 찾을 수 없음"
        )
    })
    @GetMapping("/{spaceId}")
    public ResponseEntity<List<TemplateTitleResponseDto>> getTemplateTitlesBySpaceId(
        @Parameter(
            description = "스페이스 ID",
            required = true,
            example = "1"
        )
        @PathVariable(name = "spaceId") Long spaceId
    ) {
        List<TemplateTitleResponseDto> titles = templateService.getTitlesBySpaceId(spaceId);
        return ResponseEntity.ok(titles);
    }

    /**
     * AI를 통한 템플릿 생성 요청 API
     * 사용자의 메시지를 받아서 AI Flask 서버로 전달하고,
     * 생성된 템플릿 내용을 반환합니다.
     *
     * @param request 템플릿 생성 요청 DTO (사용자 메시지 포함)
     * @return AI가 생성한 템플릿 내용 (String 형태)
     */
    @Operation(
        summary = "AI 템플릿 생성 요청",
        description = "사용자 메시지를 기반으로 AI가 템플릿을 생성합니다. " +
                     "리액트에서 사용자 입력을 받아 AI Flask 서버로 전달하는 중간다리 역할을 합니다."
    )
    @PostMapping("/create-template")
    public ResponseEntity<Object> createTemplate(
            @org.springframework.web.bind.annotation.RequestBody TemplateCreateRequestDto request) {

        log.info("템플릿 생성 요청 수신 - 사용자 메시지: {}, state: {}", request.getMessage(), request.getState());

        // TemplateService를 통해 AI Flask 서버로 요청 전달 (message + state)
        Object aiResponse = templateService.createTemplate(request.getMessage(), request.getState());

        log.info("AI Flask 서버로부터 응답 수신 완료");

        return ResponseEntity.ok(aiResponse);
    }

    @PatchMapping("/{templateId}/space/{spaceId}")
    @Operation(
            summary = "템플릿 저장 상태 변경",
            description = "특정 스페이스의 템플릿 저장 여부(`isSaved`)를 업데이트합니다. " +
                    "예를 들어 `isSaved=true`로 호출하면 해당 템플릿은 저장된 상태로 변경됩니다."
    )
    @ApiResponse(responseCode = "200", description = "성공적으로 저장 상태가 변경됨")
    @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음")
    public ResponseEntity<Boolean> saveTemplate(
            @Parameter(description = "템플릿 ID", required = true, example = "1")
            @PathVariable Long templateId,

            @Parameter(description = "스페이스 ID", required = true, example = "10")
            @PathVariable Long spaceId,

            @Parameter(description = "저장 여부 (true=저장, false=삭제)", required = true, example = "true")
            @RequestParam Boolean isSaved) {
        Boolean result = templateService.saveTemplate(templateId, spaceId, isSaved);
        return ResponseEntity.ok(result);
    }
}
