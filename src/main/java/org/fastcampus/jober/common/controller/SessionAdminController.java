package org.fastcampus.jober.common.controller;

import org.fastcampus.jober.common.dto.SessionRow;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Controller
public class SessionAdminController {

    private final SessionRegistry sessionRegistry;

    public SessionAdminController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/admin/sessions")
    public String sessionsPage(Model model) {
        ZoneId zone = ZoneId.systemDefault();

        @SuppressWarnings("unchecked")
        var principals = sessionRegistry.getAllPrincipals();

        var rows = principals.stream()
                .flatMap(p -> sessionRegistry.getAllSessions(p, false).stream().map(si -> toRow(p, si, zone)))
                .sorted((a, b) -> b.lastRequestLocal().compareTo(a.lastRequestLocal())) // 최근 순
                .toList();

        model.addAttribute("rows", rows);
        model.addAttribute("count", rows.size());
        return "sessions";
    }

    private SessionRow toRow(Object principal, SessionInformation si, ZoneId zone) {
        String username;
        String authorities;

        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
            authorities = AuthorityUtils.authorityListToSet(ud.getAuthorities()).toString();
        } else if (principal instanceof Authentication auth && auth.getPrincipal() instanceof UserDetails ud) {
            username = ud.getUsername();
            authorities = AuthorityUtils.authorityListToSet(ud.getAuthorities()).toString();
        } else {
            username = String.valueOf(principal);
            authorities = "";
        }

        ZonedDateTime lastLocal = si.getLastRequest().toInstant().atZone(zone);

        long seconds = Duration.between(lastLocal, ZonedDateTime.now(zone)).getSeconds();
        String ago = humanize(seconds);

        return new SessionRow(username, authorities, si.getSessionId(), lastLocal, ago);
    }

    private static String humanize(long seconds) {
        if (seconds < 60) return "방금 전";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "분 전";
        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";
        long days = hours / 24;
        return days + "일 전";
    }
}
