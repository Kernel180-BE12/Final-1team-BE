package org.fastcampus.jober.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.request.ContactTagRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.dto.response.ContactTagResponseDto;
import org.fastcampus.jober.space.service.SpaceContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.fastcampus.jober.space.dto.request.SpaceContactsUpdateRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceContactsUpdateResponseDto;
import org.fastcampus.jober.space.dto.request.ContactDeleteRequestDto;

@Tag(name = "Space Contact", description = "스페이스 연락처 관리 API")
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceContactController {

  private final SpaceContactService spaceContactService;

  /**
   * 연락처 조회하는 API
   * @param spaceId 연락처 조회 요청 데이터 (스페이스 ID)
   * @return 조회된 연락처 정보
   */
  @Operation(summary = "연락처 조회", description = "스페이스 ID를 받아 해당 스페이스의 연락처를 조회합니다.")
  @GetMapping("/contact/{spaceId}")
  public ResponseEntity<ContactResponseDto> getContacts(
      @Parameter(
        description = "스페이스 ID",
        required = true,
        example = "1")
      @PathVariable(name = "spaceId") Long spaceId
  ) {
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
  public ResponseEntity<ContactResponseDto> addContacts(
      @RequestBody ContactRequestDto requestDto) {
    ContactResponseDto response = spaceContactService.addContacts(requestDto);
    return ResponseEntity.ok(response);
  }

  /**
   * 연락처 정보 수정 API
   * 
   * @param requestDto 수정할 연락처 정보 (스페이스 ID, 연락처 ID, 수정할 정보)
   * @return 수정된 연락처 정보
   */
  @Operation(summary = "연락처 정보 수정", description = "스페이스 ID와 연락처 ID를 받아 해당 연락처의 이름, 전화번호, 이메일을 수정합니다.")
  @PutMapping("/contact")
  public ResponseEntity<SpaceContactsUpdateResponseDto> updateContactInfo(
      @RequestBody SpaceContactsUpdateRequestDto requestDto) {
    SpaceContactsUpdateResponseDto response = spaceContactService.updateContactInfo(requestDto);
    return ResponseEntity.ok(response);
  }

  /**
   * 연락처 삭제 API
   * 
   * @param requestDto 삭제할 연락처 정보 (스페이스 ID, 연락처 ID)
   * @return 삭제 성공 응답
   */
  @Operation(summary = "연락처 삭제", description = "스페이스 ID와 연락처 ID를 받아 해당 연락처를 삭제합니다.")
  @DeleteMapping("/contact")
  public ResponseEntity<Void> deleteContact(
      @RequestBody ContactDeleteRequestDto requestDto) {
    spaceContactService.deleteContact(requestDto);
    return ResponseEntity.ok().build();
  }

  /**
   * 연락처 태그 추가 API
   * 
   * @param requestDto 연락처 태그 추가 요청 데이터 (스페이스 ID, 태그)
   * @return 추가된 연락처 태그 정보
   */
  @Operation(summary = "연락처 태그 추가", description = "스페이스 ID와 태그를 받아 해당 스페이스의 연락처 태그를 추가합니다.")
  @PostMapping("/contact/tag")
  public ResponseEntity<ContactTagResponseDto> addContactTag(
      @RequestBody ContactTagRequestDto requestDto) {
    ContactTagResponseDto response = spaceContactService.addContactTag(requestDto);
    return ResponseEntity.ok(response);
  }

  /**
   * 연락처 태그 조회 API
   * 
   * @param spaceId 연락처 태그 조회 요청 데이터 (스페이스 ID)
   * @return 조회된 연락처 태그 정보
   */
  @Operation(summary = "연락처 태그 조회", description = "스페이스 ID를 받아 해당 스페이스의 연락처 태그를 조회합니다.")
  @GetMapping("/contact/tag/{spaceId}")
  public ResponseEntity<ContactTagResponseDto> getContactTag(@PathVariable Long spaceId) {
    ContactTagResponseDto response = spaceContactService.getContactTag(spaceId);
    return ResponseEntity.ok(response);
  }
}
