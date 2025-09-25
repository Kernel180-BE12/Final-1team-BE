package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.space.entity.SpaceContacts;

@Schema(description = "연락처 삭제 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDeleteRequestDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "삭제할 연락처 ID", example = "1")
  private Long id;

  @Schema(description = "삭제할 연락처 ", example = "홍길동")

  /**
   * 연락처 삭제 권한 검증 및 처리
   *
   * @param contact 삭제할 연락처 엔티티
   */
  public void validateAndPrepareForDeletion(SpaceContacts contact) {
    contact.validateDeletePermission(this.spaceId);
  }
}
