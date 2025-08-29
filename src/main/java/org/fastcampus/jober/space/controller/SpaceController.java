package org.fastcampus.jober.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceMemberRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.repository.SpaceMemberRepository;
import org.fastcampus.jober.space.service.SpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Spaces", description = "스페이스 관련 API")
@Controller
@RequestMapping("/spaces")
@AllArgsConstructor
public class SpaceController {
    private SpaceService spaceService;

    @Operation(
            summary = "스페이스 생성",
            description = "새로운 스페이스를 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "스페이스 생성 성공")
    @PostMapping
    public ResponseEntity<Void> createSpace(@RequestBody SpaceCreateRequestDto dto) {
        spaceService.createSpace(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "스페이스 수정",
            description = "스페이스 정보를 수정합니다. (권한이 있는 사용자만 가능)"
    )
    @ApiResponse(responseCode = "200", description = "스페이스 수정 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "404", description = "스페이스 없음")
    @PatchMapping("/{id}")
    public ResponseEntity<SpaceResponseDto> updateSpace(
            @PathVariable Long id,
            @RequestBody SpaceUpdateRequestDto dto,
            Authentication authentication) {

        SpaceResponseDto result = spaceService.updateSpace(id, dto, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

//    @PostMapping("/{spaceId}/members")
//    public ResponseEntity<Void> addSpaceMember(
//            @PathVariable Long spaceId, @RequestBody SpaceMemberRequestDto dto) {
//       spaceService.addSpaceMember(spaceId, dto.getUserId());
//       return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @DeleteMapping("/{spaceId}/members/{userId}")
//    public ResponseEntity<Void> deleteSpaceMember(
//            @PathVariable Long spaceId,
//            @PathVariable Long userId) {
//        spaceService.deleteSpaceMember(spaceId, userId);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<SpaceResponseDto>> getUserSpace(@PathVariable Long userId) {
//        List<SpaceResponseDto> result = SpaceService.getUserSpace(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(result);
//    }
//
//    // 스페이스의 멤버 조회
//    @GetMapping("/{spaceId}/members")
//    public ResponseEntity<List<SpaceMemberResponseDto>> getSpaceMembers(@PathVariable Long spaceId) {
//        List<SpaceMemberResponseDto> result = spaceService.getSpaceMembers(spaceId);
//        return ResponseEntity.ok(result);
//    }

}
