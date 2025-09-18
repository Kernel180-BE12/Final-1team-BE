package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.user.entity.Users;

import java.time.LocalDateTime;

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

 // 초대 완료가 되어야 스페이스멤버 테이블에 등록되니 이건 굳이 필요 없을 듯
//  @Enumerated(EnumType.STRING)
//  private InviteStatus inviteStatus = InviteStatus.REQUESTING; // 기본값 설정

  private String tag;

  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  @ManyToOne
  @JoinColumn(name = "spaceId")
  private Space space;

  // 비회원일 경우도 있는데 ?
  @ManyToOne
  @JoinColumn(name = "userId")
  private Users user;

  private Boolean isDeleted = false; // 멤버 논리삭제 유무
}
