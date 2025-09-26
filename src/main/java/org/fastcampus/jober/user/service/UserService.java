package org.fastcampus.jober.user.service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import org.fastcampus.jober.space.service.SpaceMemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final CustomMailSender customMailSender;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final SpaceMemberService spaceMemberService;

  @Value("${app.reset.url}")
  private String frontUrl;

  public Long getUserId(String username) throws UsernameNotFoundException {
    return userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow().getUserId();
  }

  @Transactional
  public void register(RegisterRequestDto req, Long spaceId) {
    register(req);
    Users savedUser = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    spaceMemberService.processSpaceInvitation(spaceId, req.email(), savedUser);
  }

  /**
   * 이메일로 초대받아 진행되는 회원가입
   */
  @Transactional
  public void register(RegisterRequestDto req) {
    // 입력값 형식 검증
    if (!req.username().matches("^[a-z0-9]{5,15}$")) {
      throw new BusinessException(ErrorCode.INVALID_USERNAME);
    }
    if (!req.name().matches("^.{2,}$")) {
      throw new BusinessException(ErrorCode.INVALID_NAME);
    }
    if (!req.password().matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$")) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }
    if (!req.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL);
    }

    // 중복 검증
    isUsernameExists(req.username());
    isEmailExists(req.email());

    userRepository.save(req.toEntity());
  }

  /**
   * 사용자 정보 조회
   *
   * @param principal 현재 로그인된 사용자 정보
   * @return 사용자 정보
   */
  public UserInfoResponseDto getUserInfo(CustomUserDetails principal) {
    Users user =
        userRepository
            .findByUserIdAndIsDeletedFalse(principal.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    return UserInfoResponseDto.fromEntity(user);
  }

  /**
   * 사용자 정보 수정 (현재 로그인된 사용자)
   *
   * @param req 수정할 사용자 정보
   * @param principal 현재 로그인된 사용자 정보
   * @return 변경사항이 있었는지 여부
   */
  @Transactional
  public boolean update(UpdateRequestDto req, CustomUserDetails principal) {
    // 입력값 형식 검증 (null이 아닌 경우에만)
    if (req.getName() != null && !req.getName().matches("^.{2,}$")) {
      throw new BusinessException(ErrorCode.INVALID_NAME);
    }
    if (req.getEmail() != null && !req.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL);
    }

    if (!req.getEmail().equals(getUserInfo(principal).getEmail())) {
      isEmailExists(req.getEmail());
    }

    Users user =
        userRepository
            .findByUserIdAndIsDeletedFalse(principal.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

    // DTO를 통해 엔티티 업데이트 (@Transactional로 자동 저장)

    return user.updateUserInfo(req);
  }

  /**
   * 사용자명 중복 확인
   *
   * @param username 확인할 사용자명
   * @return 중복 여부
   */
  public void isUsernameExists(String username) {
    if (userRepository.existsByUsernameAndIsDeletedFalse(username)) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 존재하는 아이디입니다.");
    }
  }

  /**
   * 이메일 중복 확인
   *
   * @param email 확인할 이메일
   * @return 중복 여부
   */
  public void isEmailExists(String email) {
    if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 존재하는 이메일입니다.");
    }
  }

  @Transactional
  public void issueTokenAndSendMail(
      PasswordResetEmailRequestDto passwordResetEmailRequestDto, String ip, String ua)
      throws MessagingException, NoSuchAlgorithmException {
    PasswordResetToken token =
        PasswordResetToken.forGenerateToken(ip, ua, passwordResetEmailRequestDto.email());
    passwordResetTokenRepository.save(token);

    // 프론트엔드 비밀번호 변경 페이지 URL 생성
    String resetUrl = frontUrl + "?token=" + token.getSecretHash();
    String plain = "아래 링크로 비밀번호를 재설정하세요 (30분 유효):\n" + resetUrl;

    customMailSender.sendMail(
        passwordResetEmailRequestDto.email(),
        resetUrl,
        "[Jober] 비밀번호 재설정",
        "mail/password-reset", // server 에 있는 메일 템플릿
        plain);
  }

  public void checkToken(PasswordResetTokenRequestDto token) {
    if (!passwordResetTokenRepository.existsBySecretHashAndUsedAtIsNullAndExpiresAtAfter(
        token.token(), Instant.now())) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "토큰이 만료되었습니다");
    }
  }

  @Transactional
  public void changePassword(PasswordResetRequestDto passwordResetRequestDto) {
    PasswordResetToken token =
        passwordResetTokenRepository
            .findBySecretHashAndUsedAtIsNullAndExpiresAtAfter(
                passwordResetRequestDto.token(), Instant.now())
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "토큰이 만료되었습니다"));

    Users u =
        userRepository
            .findByEmailAndIsDeletedFalse(token.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다"));

    token.updateIsUsedAt(Instant.now());
    u.updatePassword(passwordResetRequestDto.newPassword());
  }

  /**
   * 사용자 탈퇴
   *
   * @param principal 현재 로그인된 사용자 정보
   */
  @Transactional
  public void delete(CustomUserDetails principal) {
    Users u =
        userRepository
            .findByUserIdAndIsDeletedFalse(principal.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다"));
    u.deleteUser();
  }
}
