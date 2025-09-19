package org.fastcampus.jober.user.controller;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.user.dto.CustomUserDetails;
import org.fastcampus.jober.user.dto.request.*;
import org.fastcampus.jober.user.dto.response.LoginResponseDto;
import org.fastcampus.jober.user.dto.response.UserInfoResponseDto;
import org.fastcampus.jober.user.service.UserService;
import org.fastcampus.jober.util.ClientIpResolver;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 회원가입, 로그인 API")
public class UserController {
  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final SessionRegistry sessionRegistry;
  private final ClientIpResolver ipResolver;

  @PostMapping("/register")
  @Operation(
      summary = "회원가입",
      description = "신규 사용자를 등록합니다. 아이디(username)와 비밀번호(password), 기타 정보를 전달하면 계정이 생성됩니다.")
  @ApiResponse(responseCode = "200", description = "회원가입 성공")
  @ApiResponse(responseCode = "400", description = "요청 데이터가 잘못되었거나 이미 존재하는 사용자")
  public ResponseEntity<Void> register(@RequestBody RegisterRequestDto req) {
    userService.register(req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  @Operation(summary = "로그인", description = "사용자 이름과 비밀번호로 인증을 수행합니다. 성공 시 세션이 생성되어 로그인 상태가 유지됩니다.")
  @ApiResponse(responseCode = "200", description = "로그인 성공 및 사용자 ID/이름 반환")
  @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 아이디/비밀번호)")
  public ResponseEntity<LoginResponseDto> login(
      @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
    // 입력값 형식 검증
    if (!loginRequestDto.username().matches("^[a-z0-9]{5,15}$")) {
      throw new BusinessException(ErrorCode.INVALID_USERNAME);
    }
    if (!loginRequestDto
        .password()
        .matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$")) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    try {
      Authentication auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequestDto.username(), loginRequestDto.password()));

      UserDetails principal = (UserDetails) auth.getPrincipal();
      List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
      for (SessionInformation si : sessions) {
        // security 관점에서 '만료' 표시 (다음 요청부터 인증 끊김)
        si.expireNow();
      }

      // 인증 성공 시 SecurityContext를 세션에 저장하여 로그인 상태 유지
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);

      // 세션을 먼저 생성 (세션이 없으면 생성)
      HttpSession session = request.getSession(true);
      // 세션 유효시간 설정 (24시간)
      session.setMaxInactiveInterval((int) Duration.ofHours(24).getSeconds());

      // 세션에 SecurityContext 저장 (Spring Security가 자동으로 하지만 명시적으로)
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

      // 세션 고정 공격 방지 - 세션이 생성된 후에 호출
      // 기존 세션이 있는 경우에만 세션 ID 변경
      if (!session.isNew()) {
        request.changeSessionId();
      }

      // 세션 레지스트리에 등록
      sessionRegistry.registerNewSession(session.getId(), auth.getPrincipal());

      String username = principal.getUsername();
      Long id = userService.getUserId(username);

      return ResponseEntity.ok(new LoginResponseDto(id, username));
    } catch (BadCredentialsException e) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "Bad credentials");
    }
  }

  /**
   * 회원 정보 조회
   *
   * @return 조회된 회원 정보
   */
  @GetMapping("/info")
  @Operation(summary = "회원 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공")
  public ResponseEntity<UserInfoResponseDto> getUser(
      @AuthenticationPrincipal CustomUserDetails principal) {
    UserInfoResponseDto response = userService.getUserInfo(principal);
    return ResponseEntity.ok(response);
  }

  /**
   * 회원 정보 수정
   *
   * @param req 수정할 회원 정보
   * @param principal 현재 로그인된 사용자 정보
   * @return 수정된 회원 정보
   */
  @PutMapping("/update")
  @Operation(summary = "회원 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다. 기존 값과 동일한 필드는 변경되지 않습니다.")
  @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공")
  @ApiResponse(responseCode = "400", description = "요청 데이터가 잘못되었거나 이미 존재하는 아이디 또는 이메일")
  @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  @ApiResponse(responseCode = "204", description = "변경사항이 없음")
  public ResponseEntity<Void> update(
      @RequestBody UpdateRequestDto req, @AuthenticationPrincipal CustomUserDetails principal) {
    // 사용자 정보 업데이트 (변경사항이 있을 때만)
    boolean hasChanges = userService.update(req, principal);

    if (!hasChanges) {
      // 변경사항이 없으면 204 No Content 응답
      return ResponseEntity.noContent().build();
    }
    
    return ResponseEntity.ok().build();
  }

  @PostMapping("/password")
  @Operation(summary = "비밀번호 변경 메일 전송", description = "비밀번호 변경 시에 메일")
  @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
  @ApiResponse(responseCode = "500", description = "이메일 전송 실패")
  public ResponseEntity<Boolean> sendPasswordResetEmail(
      @RequestBody PasswordResetEmailRequestDto passwordResetEmailRequestDto,
      HttpServletRequest request)
      throws MessagingException, NoSuchAlgorithmException {
    String ip = ipResolver.resolve(request);
    String ua = request.getHeader("User-Agent");
    if (ua != null && ua.length() > 255) ua = ua.substring(0, 255);

    userService.issueTokenAndSendMail(passwordResetEmailRequestDto, ip, ua);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/token/check")
  @Operation(summary = "비밀번호 변경 토큰 검증", description = "비밀번호 변경 시 토큰 검증")
  @ApiResponse(responseCode = "200", description = "토큰 존재")
  @ApiResponse(responseCode = "401", description = "토큰 만료")
  public ResponseEntity<Boolean> checkToken(
      @RequestBody PasswordResetTokenRequestDto passwordResetTokenRequestDto) {
    userService.checkToken(passwordResetTokenRequestDto);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/password")
  @Operation(summary = "비밀번호 변경", description = "새 비밀번호로 변경")
  @ApiResponse(responseCode = "200", description = "비밀번호 변경 완료")
  @ApiResponse(responseCode = "401", description = "토큰 만료")
  @ApiResponse(responseCode = "404", description = "없는 회원")
  public ResponseEntity<Boolean> changePassword(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
    userService.changePassword(passwordResetRequestDto);
    return ResponseEntity.ok().build();
  }

    /**
   * 아이디 중복 체크
   *
   * @param checkIdRequestDto 아이디 중복 체크 요청 데이터
   * @return 아이디 중복 체크 결과
   */
  @PostMapping("/id/check")
  @Operation(summary = "아이디 중복 체크", description = "아이디 중복 체크를 합니다.")
  @ApiResponse(responseCode = "200", description = "중복되지 않는 아이디입니다.")
  @ApiResponse(responseCode = "400", description = "이미 존재하는 아이디입니다.")
  public ResponseEntity<Void> checkId(
      @Parameter(description = "아이디 중복 체크 요청 데이터", required = true) @RequestBody
          CheckIdRequestDto checkIdRequestDto) {
    userService.isUsernameExists(checkIdRequestDto.getUsername());
    return ResponseEntity.ok().build();
  }

  /**
   * 이메일 중복 체크
   *
   * @param checkEmailRequestDto 이메일 중복 체크 요청 데이터
   * @return 이메일 중복 체크 결과
   */
  @PostMapping("/email/check")
  @Operation(summary = "이메일 중복 체크", description = "이메일 중복 체크를 합니다.")
  @ApiResponse(responseCode = "200", description = "중복되지 않는 이메일입니다.")
  @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일입니다.")
  public ResponseEntity<Void> checkEmail(
      @Parameter(description = "이메일 중복 체크 요청 데이터", required = true) @RequestBody
          CheckEmailRequestDto checkEmailRequestDto) {
    userService.isEmailExists(checkEmailRequestDto.getEmail());
    return ResponseEntity.ok().build();
  }

  /**
   * 회원 탈퇴
   *
   * @param principal 현재 로그인된 사용자 정보
   * @param request 요청 정보
   * @param response 응답 정보
   * @param authentication 인증 정보
   * @return 회원 탈퇴 성공
   */
  @DeleteMapping("/delete")
  @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 합니다.")
  @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
  @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails principal, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    userService.delete(principal);

    SecurityContextLogoutHandler sclh = new SecurityContextLogoutHandler();
    sclh.setInvalidateHttpSession(true); // 세션 제거
    sclh.setClearAuthentication(true);   // 인증 제거
    sclh.logout(request, response, authentication);
    
    
    return ResponseEntity.ok().build();
  }

}
