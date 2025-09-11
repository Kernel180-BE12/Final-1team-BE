package org.fastcampus.jober.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 세션 무효/타임아웃
@Component
public class RestInvalidSessionStrategy implements InvalidSessionStrategy {
    @Override
    public void onInvalidSessionDetected(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("""
        {"code":"SESSION_INVALID","message":"세션이 유효하지 않거나(타임아웃) 종료되었습니다.","path":"%s"}
        """.formatted(req.getRequestURI()));
    }
}
