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
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
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

    /**
     * 얘를 SpaceMemberMapper로 옮기면 this.tag, this.authority를 그대로 가져오지 못하는데
     * SpaceMember toSpaceMember(InviteStatus inviteStatus, Space space, Users user); 처럼 파라미터를 늘리는게 나은지
     * 아님 지금처럼 이 파일에 두는게 나은지?
     */
    public SpaceMember toSpaceMember(Space space, Users user) {
        return SpaceMember.builder()
                .space(space)
                .user(user)
                .tag(this.tag)
                .authority(this.authority)
                .build();
    }
}
