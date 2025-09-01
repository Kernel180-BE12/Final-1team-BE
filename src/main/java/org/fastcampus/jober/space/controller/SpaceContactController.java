package org.fastcampus.jober.space.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.space.dto.request.ContactRequestDto;
import org.fastcampus.jober.space.dto.response.ContactResponseDto;
import org.fastcampus.jober.space.service.SpaceContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Space Contact", description = "스페이스 연락처 관리 API")
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceContactController {

  private final SpaceContactService spaceContactService;

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
}
