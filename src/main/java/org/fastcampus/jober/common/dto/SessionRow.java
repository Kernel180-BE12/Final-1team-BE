package org.fastcampus.jober.common.dto;

import java.time.ZonedDateTime;

public record SessionRow(
        String username,
        String authorities,
        String sessionId,
        ZonedDateTime lastRequestLocal,
        String ago
) {}
