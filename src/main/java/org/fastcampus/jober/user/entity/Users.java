package org.fastcampus.jober.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // ✅ 기본 생성자 (JPA 필수)
    protected Users() {}

    // ✅ private 생성자
    private Users(String username, String password, String name, String email,
                  LocalDateTime registeredAt, LocalDateTime updatedAt, String updatedBy) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    // ✅ 상황별 팩토리 메서드
    public static Users forSignup(String username, String password, String name, String email) {
        return new Users(username,
                password,
                name,
                email,
                LocalDateTime.now(),
                null,
                null);
    }

//    public static Users forUpdate(Users existing, String updatedBy) {
//        return new Users(
//                existing.username,
//                existing.password,
//                existing.name,
//                existing.email,
//                existing.registeredAt,
//                LocalDateTime.now(),
//                updatedBy
//        );
//    }
}
