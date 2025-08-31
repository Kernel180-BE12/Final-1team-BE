package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.management.LockInfo;
import java.util.Optional;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {

    Optional<SpaceMember> findBySpaceIdAndUserIdAndAuthority(Long spaceId, Long userId, String authority);

}
