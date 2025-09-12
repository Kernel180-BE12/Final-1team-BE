package org.fastcampus.jober.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.util.TokenGenerator;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Entity
@Getter
public class PasswordResetToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64) // sha256 hex
    private String secretHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    @Column(length = 45)        // IPv6까지 고려(최대 45자)
    private String issuedIp;

    private String issuedUserAgent;

    protected PasswordResetToken() {}

    private PasswordResetToken(String secretHash, Instant expiresAt, Instant usedAt, String issuedIp, String issuedUserAgent) {
        this.secretHash = secretHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.issuedIp = issuedIp;
        this.issuedUserAgent = issuedUserAgent;
    }

    public static PasswordResetToken forGenerateToken(String ip, String ua) throws NoSuchAlgorithmException {
        return new PasswordResetToken(
                sha256Hex(TokenGenerator.generateToken()),
                Instant.now().plus(Duration.ofMinutes(30)),
                null,
                ip,
                ua
        );
    }
}
