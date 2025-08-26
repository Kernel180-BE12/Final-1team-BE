package org.fastcampus.jober.space.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SpaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority;
    private String status;
    private String tag;
    private String documentPermission;

    // 상민님이 넣으셨다는데 뭔지 잘 모르겠고 우리가 구현할 땐 필요 없을듯
    private String inviteMethod;

    private Long spaceId;
    private Long userId;
}
