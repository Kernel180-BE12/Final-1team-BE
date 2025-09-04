package org.fastcampus.jober.space.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 스페이스 연락처 데이터 접근을 위한 Repository
 */
@Repository
public interface SpaceContactsRepository extends JpaRepository<SpaceContacts, Long> {
    
    /**
     * 특정 spaceId의 연락처들을 조회
     * @param spaceId 스페이스 ID
     * @return 연락처 엔티티 리스트
     */
    @Operation(
        summary = "스페이스 연락처 조회",
        description = "특정 spaceId의 연락처들을 조회합니다."
    )
    @Query("SELECT sc FROM SpaceContacts sc WHERE sc.spaceId = :spaceId")
    List<SpaceContacts> findBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );
    
    /**
     * 특정 spaceId의 모든 연락처를 삭제
     * @param spaceId 삭제할 스페이스 ID
     */
    @Operation(
        summary = "스페이스 연락처 삭제",
        description = "특정 spaceId의 모든 연락처를 삭제합니다."
    )
    @Query("DELETE FROM SpaceContacts sc WHERE sc.spaceId = :spaceId")
    void deleteBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );
}
