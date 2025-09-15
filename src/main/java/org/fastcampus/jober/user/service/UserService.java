package org.fastcampus.jober.user.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.dto.request.*;
import org.fastcampus.jober.user.dto.response.UserInfoResponseDto;
import org.fastcampus.jober.user.entity.PasswordResetToken;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.PasswordResetTokenRepository;
import org.fastcampus.jober.user.repository.UserRepository;
import org.fastcampus.jober.util.CustomMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordManager passwordManager;

    private final UserRepository userRepository;

    private final CustomMailSender customMailSender;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

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
     *
     * @param principal 현재 로그인된 사용자 정보
     * @return 사용자 정보
     */
    public UserInfoResponseDto getUserInfo(CustomUserDetails principal) {
        Users user = userRepository.findByIdOrThrow(principal.getUserId());
        return UserInfoResponseDto.fromEntity(user);
    }

    /**
     * 사용자 정보 수정 (현재 로그인된 사용자)
     *
     * @param req       수정할 사용자 정보
     * @param principal 현재 로그인된 사용자 정보
     * @return 변경사항이 있었는지 여부
     */
    @Transactional
    public boolean update(UpdateRequestDto req, CustomUserDetails principal) {
        final Users user = userRepository.findByIdOrThrow(principal.getUserId());

        if (requiredDuplicatedEmailCheck(user, req.getEmail())) {
            if (isEmailExists(req.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXIST, "이미 존재하는 이메일입니다.");
            }
        }

        return user.updateUserInfo(req);
    }

    private boolean requiredDuplicatedEmailCheck(final Users users, final String email) {
        return !users.getEmail().equals(email);
    }

    /**
     * 사용자명 중복 확인
     *
     * @param username 확인할 사용자명
     * @return 중복 여부
     */
    public void checkDuplicatedEmail(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 존재하는 아이디입니다.");
        }
    }

    /**
     * 이메일 중복 확인
     *
     * @param email 확인할 이메일
     * @return 중복 여부
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void sendPasswordResetMail(final PasswordResetEmailRequestDto passwordResetEmailRequestDto, final PasswordResetToken token)
            throws MessagingException {
        final String resetUrl = passwordManager.getPasswordRestUrl(token);
        final String plain = passwordManager.getRestEmailContent(resetUrl);

        customMailSender.sendPasswordResetMail(passwordResetEmailRequestDto.email(), resetUrl, plain);
    }

    public void checkToken(PasswordResetTokenRequestDto dto) {
        getValidToken(dto.getRequestedAt())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "토큰이 만료되었습니다"));
    }

    private Optional<PasswordResetToken> getValidToken(final Instant now) {
        return passwordResetTokenRepository.findById(UUID.randomUUID()).filter(t -> t.isNotExpired(now));
    }

    @Transactional
    public void changePassword(PasswordResetRequestDto passwordResetRequestDto) {
        getValidToken(passwordResetRequestDto.getRequestedAt())
                .ifPresentOrElse(
                        token -> userRepository.findByEmailOrThrow(token.getEmail())
                                .updatePassword(token, passwordResetRequestDto.newPassword()),
                        () -> new BusinessException(ErrorCode.UNAUTHORIZED, "토큰이 만료되었습니다")
                );
    }

    @Transactional
    public PasswordResetToken saveToken(final PasswordResetToken token) {
        return passwordManager.saveToken(token);
    }
}
