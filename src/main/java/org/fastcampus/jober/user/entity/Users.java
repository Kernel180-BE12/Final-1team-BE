package org.fastcampus.jober.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.user.dto.request.UpdateRequestDto;
import org.fastcampus.jober.util.PasswordHashing;

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

  private Boolean isDeleted;

  // ✅ 기본 생성자 (JPA 필수)
  protected Users() {}

  // ✅ private 생성자
  private Users(String username, String password, String name, String email, Boolean isDeleted) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.email = email;
    this.isDeleted = isDeleted;
  }

  private Users(Long userId) {
    this.userId = userId;
  }

  // ✅ 상황별 팩토리 메서드
  public static Users forSignup(String username, String password, String name, String email) {
    return new Users(username, password, name, email, false);
  }

  /**
   * 사용자 정보 업데이트 (null이 아닌 필드만 업데이트)
   *
   * @param req 업데이트 요청 DTO
   * @return 변경된 필드가 있는지 여부
   */
  public boolean updateUserInfo(UpdateRequestDto req) {
    boolean hasChanges = false;

    // if (req.getUsername() != null && !req.getUsername().equals(this.username)) {
    //     this.username = req.getUsername();
    //     hasChanges = true;
    // }

    if (req.getName() != null && !req.getName().equals(this.name)) {
      this.name = req.getName();
      hasChanges = true;
    }

    if (req.getEmail() != null && !req.getEmail().equals(this.email)) {
      this.email = req.getEmail();
      hasChanges = true;
    }

    return hasChanges;
  }

  public void updatePassword(String password) {
    this.password = PasswordHashing.hash(password);
  }

  public static Users forCreateSpace(Long userId) {
    return new Users(userId);
  }

  public boolean isSameUser(final Long userId) {
    return this.userId.equals(userId);
  }

  public void deleteUser() {
    this.isDeleted = true;
  }
}
