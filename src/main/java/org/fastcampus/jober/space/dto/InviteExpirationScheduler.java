package org.fastcampus.jober.space.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fastcampus.jober.space.entity.InviteStatus;
import org.fastcampus.jober.space.entity.InviteStatusType;
import org.fastcampus.jober.space.repository.InviteStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InviteExpirationScheduler {

    private final InviteStatusRepository inviteStatusRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireInvitations() {
        log.info("초대 만료 스케줄러 시작");
        List<InviteStatus> expiredInvites = inviteStatusRepository.findByStatusAndExpireDateBefore(
                InviteStatusType.PENDING,
                LocalDateTime.now()
        );

        int count = 0;
        for (InviteStatus invite : expiredInvites) {
            invite.markAsExpired();
            count++;
        }
        log.info("만료된 초대 {}건 처리 완료", count);
    }

    public void cleanupExpiredInvites() {
    }
}
