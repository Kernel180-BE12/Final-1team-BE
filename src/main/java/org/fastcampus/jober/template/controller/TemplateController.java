package org.fastcampus.jober.template.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 템플릿 관련 HTTP 요청을 처리하는 컨트롤러
 */
@Tag(name = "Template", description = "템플릿 관련 API")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {
    
    private final TemplateService templateService;
    
    /**
     * GET 방식으로 spaceId를 받아서 해당 spaceId의 템플릿 title들을 조회하는 API
     * @param spaceId 스페이스 ID (0이 아닌 값)
     * @return 템플릿 제목 리스트
     */
    @Operation(
        summary = "템플릿 제목 조회",
        description = "특정 spaceId의 템플릿 제목들을 조회합니다. spaceId는 0이 아닌 값이어야 합니다."
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
            responseCode = "400",
            description = "잘못된 요청 (spaceId가 0인 경우)"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 spaceId의 템플릿을 찾을 수 없음"
        )
    })
    @GetMapping("/{spaceId}")
    public ResponseEntity<List<TemplateTitleResponseDto>> getTemplateTitlesBySpaceId(
        @Parameter(
            description = "스페이스 ID (0이 아닌 값)",
            required = true,
            example = "1"
        )
        @PathVariable(name = "spaceId") Long spaceId
    ) {
        List<TemplateTitleResponseDto> titles = templateService.getTitlesBySpaceIdNotZero(spaceId);
        return ResponseEntity.ok(titles);
    }
}
