package org.fastcampus.jober.space.dto.request;

import java.util.List;

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

  @Schema(description = "삭제할 연락처 ID 목록", example = "[1, 2, 3]")
  private List<Long> ids;

  /**
   * 연락처 삭제 권한 검증 및 처리
   *
   * @param contacts 삭제할 연락처 엔티티 목록
   */
  public void validateAndPrepareForDeletion(List<SpaceContacts> contacts) {
    contacts.forEach(contact -> contact.validateDeletePermission(this.spaceId));
  }
}
