package org.fastcampus.jober.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.user.dto.request.LoginRequestDto;
import org.fastcampus.jober.user.dto.request.PasswordResetEmailRequestDto;
import org.fastcampus.jober.user.dto.request.PasswordResetRequestDto;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.fastcampus.jober.user.repository.PasswordResetTokenRepository;
import org.fastcampus.jober.user.repository.UserRepository;
import org.fastcampus.jober.util.CustomMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CustomMailSender customMailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.reset.url}")
    private String frontUrl;

    public Long getUserId(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow()
                .getUserId();
    }

    @Transactional
    public void register(RegisterRequestDto req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        userRepository.save(req.toEntity());
    }

    @Transactional
    public void issueTokenAndSendMail(PasswordResetEmailRequestDto passwordResetEmailRequestDto, String ip, String ua) throws MessagingException, NoSuchAlgorithmException {
        PasswordResetToken token = PasswordResetToken.forGenerateToken(ip, ua);
        passwordResetTokenRepository.save(token);

        // 프론트엔드 비밀번호 변경 페이지 URL 생성
        String resetUrl = frontUrl + token;
        String plain = "아래 링크로 비밀번호를 재설정하세요 (30분 유효):\n" + resetUrl;

        customMailSender.sendMail(passwordResetEmailRequestDto.email(),
                resetUrl,
                "[Jober] 비밀번호 재설정",
                "mail/password-reset",
                plain);
    }

    @Transactional
    public void changePassword(PasswordResetRequestDto passwordResetRequestDto) {
        
    }
}
