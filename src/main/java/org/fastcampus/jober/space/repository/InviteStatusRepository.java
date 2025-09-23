package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteStatusRepository extends JpaRepository<InviteStatus, Long> {

    Optional<InviteStatus> findByEmailAndSpaceId(String email, Long spaceId);
}
