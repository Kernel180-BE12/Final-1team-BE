package org.fastcampus.jober.space.controller;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
            summary = "스페이스 멤버 초대",
            description = "특정 스페이스에 멤버 초대 메일을 발송합니다."
    )
    @ApiResponse(responseCode = "200", description = "초대 메일 발송 완료")
      @PostMapping("/{spaceId}/invitations")
      public ResponseEntity<String> inviteSpaceMember(
            @Parameter(description = "초대할 스페이스 ID", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "초대할 멤버 이메일·권한 정보 목록", required = true)
            @RequestBody List<SpaceMemberAddRequestDto> dtos,
            @Parameter(hidden = true) // 로그인 사용자 정보는 Swagger에 노출 안 함
            @AuthenticationPrincipal CustomUserDetails principal) throws MessagingException {
         spaceMemberService.inviteSpaceMember(spaceId, dtos, principal);
         return ResponseEntity.ok("초대 메일 발송 완료");
      }

    @Operation(
            summary = "스페이스 초대 수락",
            description = "초대 메일 링크를 통해 스페이스 가입을 완료하고 지정된 URL로 리다이렉트합니다."
    )
    @ApiResponse(responseCode = "302", description = "수락 후 지정된 페이지로 리다이렉트")
    @GetMapping("/{spaceId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable Long spaceId,
            @RequestParam String email) {

        String redirectUrl = spaceMemberService.acceptInvitationByEmail(spaceId, email);

        // 리다이렉트 처리
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 리다이렉트
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
