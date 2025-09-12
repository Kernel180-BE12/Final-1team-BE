package org.fastcampus.jober.user.repository;

import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findBySecretHashAndUsedAtIsNullAndExpiresAtAfter(String secretHash, Instant now);

    boolean existsBySecretHashAndUsedAtIsNullAndExpiresAtAfter(String secretHash, Instant now);
}

