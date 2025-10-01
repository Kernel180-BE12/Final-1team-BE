package org.fastcampus.jober.space.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.fastcampus.jober.space.entity.SpaceMember;

@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
  @Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
AND sm.user.isDeleted = false
AND sm.isDeleted = false
""")
  List<SpaceMember> findBySpaceId(Long spaceId);

@Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
AND sm.user.userId = :userId
AND sm.user.isDeleted = false
AND sm.isDeleted = false
""")
  Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);

@Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.id IN :memberIds
AND sm.space.spaceId = :spaceId
AND sm.user.isDeleted = false
""")
  List<SpaceMember> findAllByIdInAndSpaceId(@Param("memberIds") List<Long> memberIds, @Param("spaceId") Long spaceId);


@Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
AND sm.tag = :tag
AND sm.isDeleted = false
AND sm.user.isDeleted = false
""")
  List<SpaceMember> findBySpaceIdAndTag(Long spaceId, String tag);


  // 대기 중인 초대만 조회
//  List<SpaceMember> findBySpaceIdAndInviteStatusAndIsDeleted(Long spaceId, InviteStatus status, Boolean isDeleted);
}
