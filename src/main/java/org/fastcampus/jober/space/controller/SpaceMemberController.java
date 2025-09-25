package org.fastcampus.jober.space.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import org.fastcampus.jober.space.dto.InviteResult;
import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(
            summary = "스페이스 멤버 초대 메일 발송",
            description = "특정 스페이스에 멤버 초대 메일을 발송합니다."
    )
    @ApiResponse(responseCode = "200", description = "초대 메일 발송 완료")
      @PostMapping("/{spaceId}/invitations")
      public ResponseEntity<InviteResult> inviteSpaceMember(
            @Parameter(description = "초대할 스페이스 ID", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "초대할 멤버 이메일·권한 정보 목록", required = true)
            @RequestBody List<SpaceMemberAddRequestDto> dtos,
            @Parameter(hidden = true) // 로그인 사용자 정보는 Swagger에 노출 안 함
            @AuthenticationPrincipal CustomUserDetails principal) throws MessagingException {
        InviteResult result = spaceMemberService.inviteSpaceMember(spaceId, dtos, principal);
        return ResponseEntity.ok(result);
      }

    @Operation(
            summary = "스페이스 초대 수락",
            description = """
        이메일로 전송된 초대 수락 링크를 클릭했을 때 호출되는 API입니다.
        초대 상태를 `ACCEPTED`로 변경하고, SpaceMember 테이블에 해당 사용자를 등록합니다.
        """
    )
    @GetMapping("/{spaceId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @Parameter(description = "초대받은 스페이스 ID", example = "123")
            @PathVariable Long spaceId,

            @Parameter(description = "초대 메일이 발송된 회원 이메일", example = "user@example.com")
            @RequestParam String email) {
       spaceMemberService.acceptInvitationByEmail(spaceId, email);
       return ResponseEntity.ok().build();
    }

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
