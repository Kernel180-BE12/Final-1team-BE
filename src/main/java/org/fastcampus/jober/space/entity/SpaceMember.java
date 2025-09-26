package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.common.entity.BaseEntity;
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

  @Column(nullable = false) // 이거 바꾸기
  @Builder.Default
  private Boolean isDeleted = false; // 멤버 논리삭제 유무
}
