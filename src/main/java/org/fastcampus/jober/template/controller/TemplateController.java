package org.fastcampus.jober.template.controller;


import lombok.extern.slf4j.Slf4j;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.fastcampus.jober.template.dto.response.TemplateListResponseDto;
import org.fastcampus.jober.user.dto.CustomUserDetails;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.template.dto.request.TemplateDeleteRequestDto;
import org.fastcampus.jober.template.dto.request.TemplateSaveRequestDto;
import org.fastcampus.jober.template.dto.response.TemplateCreateResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateSaveResponseDto;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;
import org.fastcampus.jober.template.service.TemplateService;
import reactor.core.publisher.Flux;

/** 템플릿 관련 REST API를 제공하는 컨트롤러 클래스 템플릿 생성, 조회, 수정, 삭제 등의 기능을 처리합니다. */
@Slf4j
@Tag(name = "Template", description = "템플릿 관련 API")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

  private final TemplateService templateService;

  /**
   * AI를 통한 템플릿 생성 요청 API 사용자의 메시지를 받아서 AI Flask 서버로 전달하고, 구조화된 템플릿 응답을 반환합니다.
   *
   * @param request 템플릿 생성 요청 DTO (사용자 메시지와 AI 세션 상태 포함)
   * @return AI가 생성한 구조화된 템플릿 응답 DTO
   */
  @Operation(
      summary = "AI 템플릿 생성 요청",
      description =
          "사용자 메시지를 기반으로 AI가 템플릿을 생성합니다. " + "리액트에서 사용자 입력을 받아 AI Flask 서버로 전달하고 구조화된 응답을 반환합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "AI 템플릿 생성 성공",
      content = @Content(schema = @Schema(implementation = TemplateCreateResponseDto.class)))
  @PostMapping("/create-template")
  public ResponseEntity<TemplateCreateResponseDto> createTemplate(
      @org.springframework.web.bind.annotation.RequestBody TemplateCreateRequestDto request) {

    log.info("템플릿 생성 요청 수신 - 사용자 메시지: {}, state: {}", request.getMessage(), request.getState());

    // TemplateService를 통해 AI Flask 서버로 요청 전달
    TemplateCreateResponseDto aiResponse = templateService.createTemplate(request);

    log.info("AI Flask 서버로부터 응답 수신 완료");

    return ResponseEntity.ok(aiResponse);
  }

  @PostMapping("/sse")
  public Flux<String> templateSSE(@RequestBody TemplateCreateRequestDto templateCreateRequestDto) {
      return templateService.templateSSE(templateCreateRequestDto);
  }

  /**
   * GET 방식으로 spaceId를 받아서 해당 spaceId의 템플릿 title들을 조회하는 API
   *
   * @param spaceId 스페이스 ID
   * @return 템플릿 제목 리스트
   */
  @Operation(summary = "템플릿 제목 조회", description = "특정 spaceId의 템플릿 제목들을 조회합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content =
            @Content(
                schema = @Schema(type = "array", implementation = TemplateTitleResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "해당 spaceId의 템플릿을 찾을 수 없음")
  })
  @GetMapping("/{spaceId}")
  public ResponseEntity<List<TemplateTitleResponseDto>> getTemplateTitlesBySpaceId(
      @Parameter(description = "스페이스 ID", required = true, example = "1")
          @PathVariable(name = "spaceId")
          Long spaceId) {
    List<TemplateTitleResponseDto> titles = templateService.getTitlesBySpaceId(spaceId);
    return ResponseEntity.ok(titles);
  }

  /**
   * GET 방식으로 spaceId와 templateId를 받아서 해당 템플릿의 상세 정보를 조회하는 API (completedAt 제외)
   *
   * @param spaceId 스페이스 ID
   * @param templateId 템플릿 ID
   * @return 템플릿 상세 정보
   */
  @Operation(
      summary = "템플릿 상세 조회",
      description = "특정 spaceId와 templateId의 템플릿 상세 정보를 조회합니다. (생성일시 제외)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = @Content(schema = @Schema(implementation = TemplateDetailResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "해당 템플릿을 찾을 수 없음")
  })
  @GetMapping("/{spaceId}/{templateId}")
  public ResponseEntity<TemplateDetailResponseDto> getTemplateDetailBySpaceIdAndTemplateId(
      @Parameter(description = "스페이스 ID", required = true, example = "1")
          @PathVariable(name = "spaceId")
          Long spaceId,
      @Parameter(description = "템플릿 ID", required = true, example = "1")
          @PathVariable(name = "templateId")
          Long templateId) {
    TemplateDetailResponseDto template =
        templateService.getTemplateDetailBySpaceIdAndTemplateId(spaceId, templateId);
    if (template == null || !template.isValid()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(template);
  }


  /**
   * 템플릿 저장 API
   *
   * @param request 템플릿 저장 요청 DTO
   * @return 템플릿 저장 응답 DTO
   */
  @Operation(summary = "템플릿 저장", description = "템플릿을 저장합니다.")
  @ApiResponse(responseCode = "200", description = "성공적으로 템플릿이 저장됨")
  @ApiResponse(responseCode = "404", description = "템플릿 또는 스페이스를 찾을 수 없음")
  @PostMapping("/save")
  public ResponseEntity<TemplateSaveResponseDto> saveTemplate(
      @RequestBody TemplateSaveRequestDto request) {
    TemplateSaveResponseDto response = templateService.saveTemplate(request);
    return ResponseEntity.ok(response);
  }

  /**
   * 템플릿 삭제 API
   *
   * @param request 템플릿 ID
   * @return 템플릿 삭제 응답 DTO
   */
  @Operation(summary = "템플릿 논리 삭제", description = "템플릿을 논리적으로 삭제합니다.")
  @ApiResponse(responseCode = "204", description = "성공적으로 템플릿이 삭제됨")
  @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음")
  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteTemplate(@RequestBody TemplateDeleteRequestDto request) {
    templateService.deleteTemplate(request);
    return ResponseEntity.noContent().build();
  }
    @Operation(
            summary = "템플릿 목록 조회",
            description = "현재 로그인한 사용자가 접근 가능한 템플릿의 "
                    + "제목과 변수화된 템플릿(parameterizedTemplate) 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "템플릿 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<List<TemplateListResponseDto>> getTemplateList(
            @Parameter(hidden = true) // Swagger UI에는 안 보이도록
            @AuthenticationPrincipal CustomUserDetails principal,
            Long spaceId
    ) {
        List<TemplateListResponseDto> templates = templateService.getTemplateList(principal,spaceId);
        return ResponseEntity.ok(templates);
    }
}
