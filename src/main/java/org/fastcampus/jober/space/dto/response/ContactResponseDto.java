package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.space.entity.ContactTag;
import org.fastcampus.jober.space.entity.SpaceContacts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "연락처 등록 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDto {

  @Schema(description = "스페이스 ID", example = "1")
  private Long spaceId;

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

    @Schema(description = "연락처 ID", example = "1", required = true)
    private Long id;

    @Schema(description = "이름", example = "김철수", required = true)
    private String name;

    @Schema(description = "휴대전화", example = "010-1234-5678", required = true)
    private String phoneNum;

    @Schema(description = "이메일", example = "kim@example.com", required = true)
    private String email;
    
    @Schema(description = "태그 정보", example = "1", required = false)
    private ContactTag tag;
  }
  
  /**
   * SpaceContacts 엔티티 리스트로부터 ContactResponseDto 생성
   */
  public static ContactResponseDto fromEntities(List<SpaceContacts> contacts, Long spaceId) {
    List<ContactInfo> contactInfos = contacts.stream()
        .map(contact -> {
          ContactInfo.ContactInfoBuilder builder = ContactInfo.builder()
              .id(contact.getId())
              .name(contact.getName())
              .phoneNum(contact.getPhoneNum())
              .email(contact.getEmail());
              
          // 태그 정보 추가
          builder.tag(contact.getContactTag());
          
          return builder.build();
        })
        .collect(Collectors.toList());
    
    return ContactResponseDto.builder()
        .spaceId(spaceId)
        .contacts(contactInfos)
        .registeredAt(LocalDateTime.now())
        .build();
  }
}
