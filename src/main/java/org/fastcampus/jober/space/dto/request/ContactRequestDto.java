package org.fastcampus.jober.space.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.space.entity.SpaceContacts;

@Schema(description = "연락처 등록 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

  @Schema(description = "연락처 목록")
  private List<ContactInfo> contacts;

  @Schema(description = "연락처 정보")
  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  public static class ContactInfo {

    @Schema(description = "이름", example = "김철수")
    private String name;

    @Schema(description = "휴대전화", example = "010-1234-5678")
    private String phoneNum;

    @Schema(description = "이메일", example = "kim@example.com")
    private String email;

    /** ContactInfo를 SpaceContacts 엔티티로 변환 */
    public SpaceContacts toEntity(Long spaceId) {
      return SpaceContacts.builder()
          .name(this.name)
          .phoneNum(this.phoneNum)
          .email(this.email)
          .spaceId(spaceId)
          .build();
    }
  }

  /** DTO를 SpaceContacts 엔티티 리스트로 변환 */
  public List<SpaceContacts> toValidateEntities() {
    return contacts.stream()
        .map(contactInfo -> contactInfo.toEntity(this.spaceId))
        .peek(SpaceContacts::validateContactInfo)
        .collect(Collectors.toList());
  }
}
