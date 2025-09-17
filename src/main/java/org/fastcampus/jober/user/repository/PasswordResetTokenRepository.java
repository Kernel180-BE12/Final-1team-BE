package org.fastcampus.jober.user.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.fastcampus.jober.user.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
  Optional<PasswordResetToken> findBySecretHashAndUsedAtIsNullAndExpiresAtAfter(
      String secretHash, Instant now);

  boolean existsBySecretHashAndUsedAtIsNullAndExpiresAtAfter(String secretHash, Instant now);
}
