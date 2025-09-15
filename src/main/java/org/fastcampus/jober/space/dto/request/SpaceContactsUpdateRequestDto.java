package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.space.entity.SpaceContacts;

/** 연락처 정보 수정 요청 DTO */
@Schema(description = "연락처 정보 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceContactsUpdateRequestDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "연락처 ID", example = "1")
  private Long contactId;

  @Schema(description = "연락처 이름", example = "홍길동")
  private String name;

  @Schema(description = "연락처 전화번호", example = "010-1234-5678")
  private String phoneNumber;

  @Schema(description = "연락처 이메일", example = "hong@example.com")
  private String email;

  @Schema(description = "연락처 태그", example = "프리랜서")
  private String tag;

  /**
   * 기존 연락처 엔티티의 정보를 업데이트
   *
   * @param existingContact 기존 연락처 엔티티
   * @return 업데이트된 연락처 엔티티
   */
  public SpaceContacts updateExistingContact(SpaceContacts existingContact) {
    // 스페이스 ID 검증
    if (!existingContact.getSpaceId().equals(this.spaceId)) {
      throw new IllegalArgumentException("해당 스페이스의 연락처가 아닙니다.");
    }

    // 연락처 정보 업데이트
    existingContact.updateContactInfo(this.name, this.phoneNumber, this.email, this.tag);

    return existingContact;
  }
}
