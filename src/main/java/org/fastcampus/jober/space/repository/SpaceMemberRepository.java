package org.fastcampus.jober.space.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.fastcampus.jober.space.entity.SpaceMember;

@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
  @Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
AND sm.isDeleted = false
""")
  List<SpaceMember> findBySpaceId(Long spaceId);

@Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
AND sm.user.userId = :userId
AND sm.isDeleted = false
""")
  Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);
}
