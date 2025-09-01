package org.fastcampus.jober.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
