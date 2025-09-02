package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.fastcampus.jober.user.entity.Users;

@Entity
@Getter
public class SpaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Authority authority;
    private InviteStatus inviteStatus;
    private String tag;

    // 관리자, 참여문서 관리자, 권한해제 3개인데 이걸 구현해야 할까........
//    private String documentPermission;

    @ManyToOne
    @JoinColumn(name = "spaceId")
    private Space space;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

}
