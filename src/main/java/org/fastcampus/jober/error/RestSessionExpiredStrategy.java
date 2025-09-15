package org.fastcampus.jober.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestSessionExpiredStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletRequest req = event.getRequest();
        HttpServletResponse res = event.getResponse();
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("""
        {"code":"SESSION_EXPIRED_CONCURRENT","message":"다른 위치에서 로그인되어 현재 세션이 종료되었습니다.","path":"%s"}
        """.formatted(req.getRequestURI()));
    }
}
