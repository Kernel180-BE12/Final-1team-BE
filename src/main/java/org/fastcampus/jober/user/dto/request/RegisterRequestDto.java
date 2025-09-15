package org.fastcampus.jober.user.dto.request;

import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.util.PasswordHashing;

public record RegisterRequestDto(String username, String password, String name, String email) {
  public Users toEntity() {
    return Users.forSignup(username, PasswordHashing.hash(password), name, email);
  }
}
