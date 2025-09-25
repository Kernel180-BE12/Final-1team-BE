package org.fastcampus.jober.space.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.space.entity.Authority;
import org.fastcampus.jober.space.entity.InviteStatusType;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.entity.SpaceMember;
import org.fastcampus.jober.user.entity.Users;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InviteStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long statusId;

    private Long spaceId;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String email;
    private String tag;

    @Enumerated(EnumType.STRING)
    private InviteStatusType status;

    private LocalDateTime expireDate; // 초대 만료일

    /** 저장 이후 만료일 갱신/연장/단축을 위한 메서드 */
    public InviteStatus expire(int days) {
        this.expireDate = LocalDateTime.now().plusDays(days);
        return this;
    }

    public void updateStatus(InviteStatusType status) {
        this.status = status;
    }

    public SpaceMember toSpaceMember(Space space, Users user) {
        return SpaceMember.builder()
                .space(space)
                .user(user)
                .tag(this.tag)
                .authority(this.authority)
                .isDeleted(false)
                .build();
    }
}
