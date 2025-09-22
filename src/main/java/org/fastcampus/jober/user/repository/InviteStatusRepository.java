package org.fastcampus.jober.user.repository;

import org.fastcampus.jober.space.entity.InviteStatus;
import org.fastcampus.jober.space.entity.InviteStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteStatusRepository extends JpaRepository<InviteStatus, Long> {
    public Optional<InviteStatus> findByUsersEmailAndSpaceIdAndStatus(String email, Long spaceId, InviteStatusType status);

}
