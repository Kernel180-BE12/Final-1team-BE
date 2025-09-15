package org.fastcampus.jober.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.fastcampus.jober.user.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PasswordManager {

    @Value("${app.reset.url}")
    private String frontUrl;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public PasswordResetToken saveToken(final PasswordResetToken token) {
        return passwordResetTokenRepository.save(token);
    }

    public String getPasswordRestUrl(final PasswordResetToken token) {
        return frontUrl + "?token=" + token.getSecretHash();
    }

    public String getRestEmailContent(final String resetUrl) {
        return "아래 링크로 비밀번호를 재설정하세요 (30분 유효):\n" + resetUrl;
    }
}
