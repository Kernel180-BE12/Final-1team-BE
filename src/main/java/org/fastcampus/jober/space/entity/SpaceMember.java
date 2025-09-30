package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.space.dto.request.MemberUpdateRequestDto;
import org.fastcampus.jober.user.entity.Users;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceMember extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  private String tag;

  @ManyToOne
  @JoinColumn(name = "spaceId")
  private Space space;

  @ManyToOne
  @JoinColumn(name = "userId")
  private Users user;

  @Column(nullable = false)   // DB 스키마의 제약조건
  @Builder.Default            // 애플리케이션 레벨 기본값
  private Boolean isDeleted = false; // 멤버 논리삭제 유무

  public void softDelete() {isDeleted = true;}

  public void updateMember(MemberUpdateRequestDto dto) {
    if (dto.getAuthority() != null) {
      this.authority = dto.getAuthority();
    }
    if (dto.getTag() != null && !dto.getTag().isBlank()) {
      this.tag = dto.getTag();
    }
  }
}
