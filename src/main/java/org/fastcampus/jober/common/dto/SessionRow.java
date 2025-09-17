package org.fastcampus.jober.common.dto;

import java.time.ZonedDateTime;

public record SessionRow(
    String username,
    String authorities,
    String sessionId,
    ZonedDateTime lastRequestLocal,
    String ago,
    boolean expired, // 동시로그인 만료 여부
    String remaining, // 남은 유효시간 (사람 친화적)
    ZonedDateTime expiresAtLocal // 만료 예정 시각
    ) {}
