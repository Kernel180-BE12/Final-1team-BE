package org.fastcampus.jober.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashing {
  private static PasswordEncoder encoder;

  @Autowired
  public PasswordHashing(PasswordEncoder passwordEncoder) {
    PasswordHashing.encoder = passwordEncoder;
  }

  public static String hash(String rawPassword) {
    return encoder.encode(rawPassword);
  }

  //    public static boolean matches(String rawPassword, String encoded) {
  //        return encoder.matches(rawPassword, encoded);
  //    }
}
