package org.fastcampus.jober.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.user.dto.request.LoginRequestDto;
import org.fastcampus.jober.user.dto.request.RegisterRequestDto;
import org.fastcampus.jober.user.dto.response.LoginResponseDto;
import org.fastcampus.jober.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 회원가입, 로그인 API")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SessionRegistry sessionRegistry;

    @PostMapping("/register")
    @Operation(
            summary = "회원가입",
            description = "신규 사용자를 등록합니다. 아이디(username)와 비밀번호(password), 기타 정보를 전달하면 계정이 생성됩니다."
    )
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @ApiResponse(responseCode = "400", description = "요청 데이터가 잘못되었거나 이미 존재하는 사용자")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDto req) {
        userService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자 이름과 비밀번호로 인증을 수행합니다. 성공 시 세션이 생성되어 로그인 상태가 유지됩니다."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공 및 사용자 ID/이름 반환")
    @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 아이디/비밀번호)")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password())
            );

            // 인증 성공 시 SecurityContext를 세션에 저장하여 로그인 상태 유지
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // 세션을 먼저 생성 (세션이 없으면 생성)
            HttpSession session = request.getSession(true);

            // 세션에 SecurityContext 저장 (Spring Security가 자동으로 하지만 명시적으로)
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            // 세션 고정 공격 방지 - 세션이 생성된 후에 호출
            // 기존 세션이 있는 경우에만 세션 ID 변경
            if (session != null && !session.isNew()) {
                request.changeSessionId();
            }

            // 세션 레지스트리에 등록
            sessionRegistry.registerNewSession(session.getId(), auth.getPrincipal());

            UserDetails principal = (UserDetails) auth.getPrincipal();
            String username = principal.getUsername();
            Long id = userService.getUserId(username);

            return ResponseEntity.ok(new LoginResponseDto(id, username));
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Bad credentials");
        }
    }
}
