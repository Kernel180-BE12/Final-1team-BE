package org.fastcampus.jober.user.entity;

import jakarta.persistence.*;

@Entity
public class UserInfoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String updatedBy;
    private String updatedAt;
    private String updatedColumnName;
    private String beforeUpdate;
    private String afterUpdate;

    private Long userId;
}
