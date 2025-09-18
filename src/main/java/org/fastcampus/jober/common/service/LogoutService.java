package org.fastcampus.jober.common.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 로그아웃 처리를 중앙화하는 서비스
 * Spring Security 로그아웃과 동일한 로직을 제공
 */
@Service
@RequiredArgsConstructor
public class LogoutService {
    
    @Lazy
    private final SessionRegistry sessionRegistry;
    
    /**
     * Spring Security 로그아웃과 동일한 로직으로 로그아웃 처리
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     */
    public void performLogout(HttpServletRequest request, HttpServletResponse response) {
        // 1) 세션레지스트리에서 제거
        var session = request.getSession(false);
        if (session != null) {
            sessionRegistry.removeSessionInformation(session.getId());
        }
        
        // 2) 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        
        // 3) 인증 정보 정리
        SecurityContextHolder.clearContext();
        
        // 4) 세션 무효화
        if (session != null) {
            session.invalidate();
        }
    }
}
