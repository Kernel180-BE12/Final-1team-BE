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

    /**
     * 사용자 정보 업데이트 (기존 값과 다를 때만 업데이트)
     * @param newUsername 새로운 사용자명 (null이면 변경하지 않음)
     * @param newName 새로운 이름 (null이면 변경하지 않음)
     * @param newEmail 새로운 이메일 (null이면 변경하지 않음)
     * @return 변경된 필드가 있는지 여부
     */
    public boolean updateUserInfo(String newUsername, String newName, String newEmail) {
        boolean hasChanges = false;
        
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            hasChanges = true;
        }
        
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            hasChanges = true;
        }
        
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            hasChanges = true;
        }
        
        return hasChanges;
    }

}
