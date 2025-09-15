package org.fastcampus.jober.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {
    private static final String[] CANDIDATES = {
            "X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP",
            "X-Forwarded", "Forwarded-For", "Forwarded"
    };

    public String resolve(HttpServletRequest req) {
        for (String h : CANDIDATES) {
            String v = req.getHeader(h);
            if (v != null && !v.isBlank() && !"unknown".equalsIgnoreCase(v)) {
                // X-Forwarded-For: client, proxy1, proxy2...
                return v.split(",")[0].trim();
            }
        }
        return req.getRemoteAddr();
    }
}
