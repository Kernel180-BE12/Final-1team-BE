package org.fastcampus.jober.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.dto.request.LoginRequestDto;
import org.fastcampus.jober.user.dto.request.PasswordResetEmailRequestDto;
import org.fastcampus.jober.user.dto.request.PasswordResetRequestDto;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.fastcampus.jober.user.dto.request.UpdateRequestDto;
import org.fastcampus.jober.user.dto.response.UserInfoResponseDto;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.fastcampus.jober.user.repository.PasswordResetTokenRepository;
import org.fastcampus.jober.user.repository.UserRepository;
import org.fastcampus.jober.util.CustomMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 사용자 정보 조회
     * @param principal 현재 로그인된 사용자 정보
     * @return 사용자 정보
     */
    public UserInfoResponseDto getUserInfo(CustomUserDetails principal) {
        Users user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserInfoResponseDto.fromEntity(user);
    }

    /**
     * 사용자 정보 수정 (현재 로그인된 사용자)
     * @param req 수정할 사용자 정보
     * @param principal 현재 로그인된 사용자 정보
     * @return 변경사항이 있었는지 여부
     */
    @Transactional
    public boolean update(UpdateRequestDto req, CustomUserDetails principal) {
        Users user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // DTO를 통해 엔티티 업데이트 (@Transactional로 자동 저장)
        boolean hasChanges = user.updateUserInfo(req);

        return hasChanges;
    }

    /**
     * 사용자명 중복 확인
     * @param username 확인할 사용자명
     * @return 중복 여부
     */
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일
     * @return 중복 여부
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
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
