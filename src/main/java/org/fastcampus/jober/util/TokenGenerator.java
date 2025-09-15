package org.fastcampus.jober.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
  public static String generateToken() throws NoSuchAlgorithmException {
    byte[] secretBytes = new byte[32];
    SecureRandom.getInstanceStrong().nextBytes(secretBytes);
    String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);

    return secret;
  }
}
