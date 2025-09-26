package org.fastcampus.jober.space.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.service.SpaceContactService;

@Tag(name = "Space Contact", description = "스페이스 연락처 관리 API")
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
@Slf4j
public class SpaceContactController {

  private final SpaceContactService spaceContactService;

  /**
   * 연락처 조회하는 API
   *
   * @param spaceId 연락처 조회 요청 데이터 (스페이스 ID)
   * @return 조회된 연락처 정보
   */
  @Operation(summary = "연락처 조회", description = "스페이스 ID를 받아 해당 스페이스의 연락처를 조회합니다.")
  @GetMapping("/contact/{spaceId}")
  public ResponseEntity<ContactResponseDto> getContacts(
      @Parameter(description = "스페이스 ID", required = true, example = "1")
          @PathVariable(name = "spaceId")
          Long spaceId) {
    ContactResponseDto response = spaceContactService.getContacts(spaceId);
    return ResponseEntity.ok(response);
  }

  /**
   * 스페이스에 여러 연락처를 추가하는 API
   *
   * @param requestDto 연락처 추가 요청 데이터 (스페이스 ID, 연락처 목록)
   * @return 추가된 연락처 정보와 등록 시간을 포함한 응답
   */
  @Operation(summary = "연락처 추가", description = "스페이스에 여러 연락처를 추가합니다.")
  @PostMapping("/contact")
  public ResponseEntity<ContactResponseDto> addContacts(@RequestBody ContactRequestDto requestDto) {
    ContactResponseDto response = spaceContactService.addContacts(requestDto);
    return ResponseEntity.ok(response);
  }

  /**
   * 연락처 정보 수정 API
   *
   * @param requestDto 수정할 연락처 정보 (스페이스 ID, 연락처 ID, 수정할 정보)
   * @return 수정된 연락처 정보
   */
  @Operation(
      summary = "연락처 정보 수정",
      description = "스페이스 ID와 연락처 ID를 받아 해당 연락처의 이름, 전화번호, 이메일을 수정합니다.")
  @PutMapping("/contact")
  public ResponseEntity<SpaceContactsUpdateResponseDto> updateContactInfo(
      @RequestBody SpaceContactsUpdateRequestDto requestDto) {
    SpaceContactsUpdateResponseDto response = spaceContactService.updateContactInfo(requestDto);
    return ResponseEntity.ok(response);
  }

  /**
   * 연락처 삭제 API (단일 또는 여러 연락처)
   *
   * @param requestDto 삭제할 연락처 정보 (스페이스 ID, 연락처 ID 목록)
   * @return 삭제 성공 응답
   */
  @Operation(summary = "연락처 삭제", description = "스페이스 ID와 연락처 ID 목록을 받아 연락처를 삭제합니다. 단일 또는 여러 연락처 삭제가 가능합니다.")
  @DeleteMapping("/contact")
  @ApiResponse(responseCode = "204", description = "성공적으로 연락처가 삭제됨")
  @ApiResponse(responseCode = "404", description = "일부 연락처를 찾을 수 없음")
  @ApiResponse(responseCode = "403", description = "삭제 권한이 없음")
  public ResponseEntity<Void> deleteContacts(@RequestBody ContactDeleteRequestDto requestDto) {
    spaceContactService.deleteContacts(requestDto);
    return ResponseEntity.noContent().build();
  }

  /**
   * 스페이스 ID와 tag를 받아 연락처를 조회하는 API
   *
   * @param spaceId 스페이스 ID
   * @param tag 태그
   * @return 연락처 정보
   */
  @Operation(summary = "스페이스 ID와 tag를 받아 연락처를 조회합니다.", description = "스페이스 ID와 tag를 받아 연락처를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "404", description = "해당 spaceId의 연락처를 찾을 수 없음")
  @GetMapping("/contact/{spaceId}/tag")
  public ResponseEntity<ContactResponseDto> getContactsByTag(
      @Parameter(description = "스페이스 ID", required = true, example = "1")
          @PathVariable(name = "spaceId")
          Long spaceId,
      @Parameter(description = "태그", required = true, example = "프리랜서") @RequestParam(name = "tag")
          String tag) {
            log.info("spaceId = " + spaceId + ", tag = " + tag);
    ContactResponseDto response = spaceContactService.getContactsByTag(spaceId, tag);
    return ResponseEntity.ok(response);
  }
}
