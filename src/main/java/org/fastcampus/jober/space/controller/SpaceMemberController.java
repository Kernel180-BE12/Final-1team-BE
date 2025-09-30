package org.fastcampus.jober.space.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import org.fastcampus.jober.space.dto.InviteResult;
import org.fastcampus.jober.space.dto.request.MemberUpdateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceMemberAddRequestDto;
import org.fastcampus.jober.space.dto.response.MemberUpdateResponseDto;
import org.fastcampus.jober.user.dto.CustomUserDetails;
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
@RequestMapping("/space-members")
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

  @Operation(summary = "스페이스 멤버 조회", description = "특정 spaceId에 속한 모든 멤버 정보를 가져옵니다.")
  @Parameter(name = "spaceId", description = "멤버를 조회할 스페이스의 ID", required = true)
  @GetMapping("/{spaceId}/members")
  public ResponseEntity<List<SpaceMemberResponseDto>> getSpaceMembers(@PathVariable Long spaceId) {
    List<SpaceMemberResponseDto> result = spaceMemberService.getSpaceMembers(spaceId);
    return ResponseEntity.ok(result);
  }

    @Operation(
            summary = "스페이스 멤버 논리 삭제",

            description = """
        특정 스페이스에서 여러 멤버를 논리 삭제합니다.  
        - `memberIds` : 삭제할 멤버 ID들의 리스트(예: `1,2,3`)  
        - `spaceId` : 해당 스페이스 ID
        """,
            parameters = {
                    @Parameter(
                            name = "memberIds",
                            description = "삭제할 스페이스 멤버 ID 목록 (콤마로 구분된 경로 변수)",
                            required = true
                    ),
                    @Parameter(
                            name = "spaceId",
                            description = "해당 스페이스 ID (쿼리 파라미터)",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공 (내용 없음)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 또는 관리자 아님"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "스페이스 또는 멤버를 찾을 수 없음"
            )
    })
    @DeleteMapping("/{memberIds}")
    public ResponseEntity<Void> deleteSpaceMember(
            @PathVariable List<Long> memberIds,
            @RequestParam Long spaceId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        spaceMemberService.deleteSpaceMember(memberIds, spaceId, principal);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "스페이스 멤버 정보 수정",
            description = "특정 스페이스의 멤버 권한(Authority 등)을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = SpaceMemberResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음")
    })
    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberUpdateResponseDto> updateSpaceMember(
            @PathVariable Long memberId,
            Long spaceId,
            @Parameter(description = "멤버 수정 요청 DTO", required = true)
            @RequestBody MemberUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails principal) {
        MemberUpdateResponseDto result = spaceMemberService.updateMember(memberId, spaceId, dto, principal);
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(
            summary = "태그별 스페이스 멤버 조회",
            description = "특정 스페이스에서 지정한 태그를 가진 멤버 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 스페이스 멤버가 아님")
    })
    @GetMapping("/{spaceId}/tag")
    public ResponseEntity<List<SpaceMemberResponseDto>> getMemberByTag(
            @Parameter(description = "조회할 스페이스 ID", required = true)
            @PathVariable Long spaceId,
            @Parameter(description = "조회할 멤버의 태그", required = true)
            @RequestParam String tag,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<SpaceMemberResponseDto> result = spaceMemberService.getMemberByTag(spaceId, tag, principal.getUserId());
        return ResponseEntity.ok(result);
    }
}
