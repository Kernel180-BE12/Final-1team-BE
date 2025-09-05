package org.fastcampus.jober.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fastcampus.jober.template.dto.request.TemplateCreateRequestDto;
import org.fastcampus.jober.template.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 템플릿 관련 REST API를 제공하는 컨트롤러 클래스
 * 템플릿 생성, 조회, 수정, 삭제 등의 기능을 처리합니다.
 */
@Slf4j
@Tag(name = "Template", description = "템플릿 관리 API")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    /**
     * AI를 통한 템플릿 생성 요청 API
     * 사용자의 메시지를 받아서 AI Flask 서버로 전달하고, 
     * 생성된 템플릿 내용을 반환합니다.
     * 
     * @param request 템플릿 생성 요청 DTO (사용자 메시지와 AI 세션 상태 포함)
     * @return AI가 생성한 템플릿 내용 (Object 형태)
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
        
        // TemplateService를 통해 AI Flask 서버로 요청 전달
        Object aiResponse = templateService.createTemplate(request);
        
        log.info("AI Flask 서버로부터 응답 수신 완료");
        
        return ResponseEntity.ok(aiResponse);
    }
}
