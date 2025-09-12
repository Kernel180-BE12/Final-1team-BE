package org.fastcampus.jober.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import jakarta.persistence.*;
import org.fastcampus.jober.common.entity.BaseEntity;


@Entity
@Getter
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    private String username;

    private String password;

    private String name;

    private String email;

    // ✅ 기본 생성자 (JPA 필수)
    protected Users() {}

    protected Users(Long userId) {
        this.userId = userId;
    }

    // ✅ private 생성자
    private Users(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    // ✅ 상황별 팩토리 메서드
    public static Users forSignup(String username, String password, String name, String email) {
        return new Users(username,
                password,
                name,
                email);
    }

    public static Users forCreateSpace(Long userId) {
        return new Users(userId);
    }

    public boolean isSameUser(final Long userId) {
        return this.userId.equals(userId);
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
