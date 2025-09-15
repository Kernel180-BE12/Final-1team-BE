package org.fastcampus.jober.space.controller;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.space.service.SpaceMemberService;

@Controller
@RequiredArgsConstructor
public class SpaceMemberController {
  private final SpaceMemberService spaceMemberService;

  //    @PostMapping("/{spaceId}/members") // 엥 근데 이거 post 인가
  //    public ResponseEntity<Void> addSpaceMember(
  //            @PathVariable Long spaceId, @RequestBody SpaceMemberRequestDto dto) {
  //       spaceService.addSpaceMember(spaceId, dto.getUserId());
  //       return ResponseEntity.status(HttpStatus.CREATED).build();
  //    }

  //    @DeleteMapping("/{spaceId}/members/{userId}")
  //    public ResponseEntity<Void> deleteSpaceMember(
  //            @PathVariable Long spaceId,
  //            @PathVariable Long userId) {
  //        spaceService.deleteSpaceMember(spaceId, userId);
  //        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  //    }
  //
  //
  //    // 스페이스의 멤버 조회
  //    @GetMapping("/{spaceId}/members")
  //    public ResponseEntity<List<SpaceMemberResponseDto>> getSpaceMembers(@PathVariable Long
  // spaceId) {
  //        List<SpaceMemberResponseDto> result = spaceService.getSpaceMembers(spaceId);
  //        return ResponseEntity.ok(result);
  //    }
}
