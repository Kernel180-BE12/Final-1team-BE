package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.Getter;

import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.user.entity.Users;

@Entity
@Getter
public class SpaceMember extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  private InviteStatus inviteStatus;

  private String tag;

  @ManyToOne
  @JoinColumn(name = "spaceId")
  private Space space;

  @ManyToOne
  @JoinColumn(name = "userId")
  private Users user;
}
