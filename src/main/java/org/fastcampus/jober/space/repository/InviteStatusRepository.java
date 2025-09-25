package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.dto.InviteStatus;
import org.fastcampus.jober.space.entity.InviteStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteStatusRepository extends JpaRepository<InviteStatus, Long> {

    Optional<InviteStatus> findByEmailAndSpaceId(String email, Long spaceId);

    @Query("""
SELECT is
FROM InviteStatus is
WHERE is.email = :email
AND is.spaceId = :spaceId
AND is.status = :status
""")
    Optional<InviteStatus> findByEmailAndSpaceIdAndStatus(String email, Long spaceId, InviteStatusType status);
}
