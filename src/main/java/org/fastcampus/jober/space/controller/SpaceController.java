package org.fastcampus.jober.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.response.SpaceListResponseDto;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.service.SpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Spaces", description = "스페이스 관련 API")
@Controller
@RequestMapping("/spaces")
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

    @Operation(
            summary = "스페이스 생성",
            description = "새로운 스페이스를 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "스페이스 생성 성공")
    @PostMapping
    public ResponseEntity<Void> createSpace(@Valid @RequestBody SpaceCreateRequestDto dto, @AuthenticationPrincipal CustomUserDetails principal) {
        spaceService.createSpace(dto, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "단일 스페이스 조회",
            description = "ID를 통해 특정 스페이스의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 스페이스 정보를 조회함",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpaceResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 스페이스를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpaceResponseDto> getSpace (@PathVariable Long id) {
        SpaceResponseDto result = spaceService.getSpace(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @Operation(
            summary = "스페이스 수정",
            description = "스페이스 정보를 수정합니다. (권한이 있는 사용자만 가능)"
    )
    @ApiResponse(responseCode = "200", description = "스페이스 수정 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "스페이스 없음")
    @PatchMapping("/{spaceId}")
    public ResponseEntity<SpaceResponseDto> updateSpace(
            @PathVariable Long spaceId,
            @Valid @RequestBody SpaceUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails principal) {

        SpaceResponseDto result = spaceService.updateSpace(spaceId, dto, principal);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "스페이스 삭제",
            description = "지정된 스페이스 ID를 통해 스페이스를 삭제합니다. 스페이스 관리자만 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공적으로 스페이스를 삭제함"),
            @ApiResponse(responseCode = "403", description = "스페이스 삭제 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 스페이스를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        spaceService.deleteSpace(id, principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "스페이스 목록 조회",
            description = "로그인한 사용자가 속한 모든 스페이스 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스페이스 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SpaceListResponseDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/list")
    public ResponseEntity<List<SpaceListResponseDto>> getSpaceList(
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<SpaceListResponseDto> result = spaceService.getSpaceList(principal);
        return ResponseEntity.ok(result);
    }
}
