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
    private String inviteMethod;

    private Long spaceId;
    private Long userId;
}
