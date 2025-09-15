package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
    @Query("""
SELECT sm
FROM SpaceMember sm
WHERE sm.space.spaceId = :spaceId
""")
    List<SpaceMember> findBySpaceId(Long spaceId);
}
