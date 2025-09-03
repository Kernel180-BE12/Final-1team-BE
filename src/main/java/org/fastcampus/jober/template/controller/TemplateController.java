package org.fastcampus.jober.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.template.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
@Tag(name = "Template", description = "템플릿 관련 API")
public class TemplateController {

    private final TemplateService templateService;

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
