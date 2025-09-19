package org.fastcampus.jober.space.controller;

import java.util.List;

import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceMemberRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.space.dto.response.SpaceMemberResponseDto;
import org.fastcampus.jober.space.service.SpaceMemberService;

@RestController
@RequestMapping("/spaceMembers")
@RequiredArgsConstructor
public class SpaceMemberController {
  private final SpaceMemberService spaceMemberService;
//
//      @PostMapping("/{spaceId}/add")
//      public ResponseEntity<Void> addSpaceMember(
//              @PathVariable Long spaceId, @RequestBody List<SpaceMemberAddRequestDto> dtos) {
//         spaceMemberService.addSpaceMember(spaceId, dtos);
//         return ResponseEntity.status(HttpStatus.CREATED).build();
//      }

  //    @DeleteMapping("/{spaceId}/members/{userId}")
  //    public ResponseEntity<Void> deleteSpaceMember(
  //            @PathVariable Long spaceId,
  //            @PathVariable Long userId) {
  //        spaceService.deleteSpaceMember(spaceId, userId);
  //        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  //    }
  //
  /**
   * 특정 스페이스의 모든 멤버를 조회합니다.
   *
   * @param spaceId 멤버를 조회할 스페이스 ID
   * @return 스페이스 멤버 목록과 HTTP 상태 코드
   */
  @Operation(summary = "스페이스 멤버 조회", description = "특정 spaceId에 속한 모든 멤버 정보를 가져옵니다.")
  @Parameter(name = "spaceId", description = "멤버를 조회할 스페이스의 ID", required = true)
  @GetMapping("/{spaceId}/members")
  public ResponseEntity<List<SpaceMemberResponseDto>> getSpaceMembers(@PathVariable Long spaceId) {
    List<SpaceMemberResponseDto> result = spaceMemberService.getSpaceMembers(spaceId);
    return ResponseEntity.ok(result);
  }
}
