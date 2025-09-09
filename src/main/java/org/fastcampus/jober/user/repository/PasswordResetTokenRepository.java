package org.fastcampus.jober.user.repository;

import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    @Modifying
    @Query("""
      update PasswordResetToken t
         set t.usedAt = :now
       where t.id = :id
         and t.secretHash = :secretHash
         and t.usedAt is null
         and t.expiresAt > :now
    """)
    int markUsedIfValid(@Param("id") UUID id,
                        @Param("secretHash") String secretHash,
                        @Param("now") Instant now);
}

