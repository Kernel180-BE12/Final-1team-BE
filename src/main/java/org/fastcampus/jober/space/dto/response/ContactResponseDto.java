package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "연락처 등록 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "스페이스 이름", example = "테스트 회사")
  private String spaceName;

  @Schema(description = "등록된 연락처 목록")
  private List<ContactInfo> contacts;

  @Schema(description = "등록 시간", example = "2024-01-15T10:30:00")
  private LocalDateTime registeredAt;

  @Schema(description = "연락처 정보")
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactInfo {

    @Schema(description = "연락처 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "김철수")
    private String name;

    @Schema(description = "휴대전화", example = "010-1234-5678")
    private String phoneNum;

    @Schema(description = "이메일", example = "kim@example.com")
    private String email;
  }
}
