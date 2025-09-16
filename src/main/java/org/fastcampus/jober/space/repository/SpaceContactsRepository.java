package org.fastcampus.jober.space.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fastcampus.jober.space.entity.SpaceContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
     * 특정 spaceId의 연락처들을 조회 (논리삭제되지 않은 연락처만)
     * @param spaceId 스페이스 ID
     * @return 연락처 엔티티 리스트
     */
    @Operation(
        summary = "스페이스 연락처 조회",
        description = "특정 spaceId의 연락처들을 조회합니다. (삭제되지 않은 연락처만)"
    )
    @Query("SELECT sc FROM SpaceContacts sc LEFT JOIN FETCH sc.contactTag WHERE sc.spaceId = :spaceId AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
    List<SpaceContacts> findBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );
    
    /**
     * 특정 spaceId의 모든 연락처를 논리삭제
     * @param spaceId 삭제할 스페이스 ID
     */
    @Operation(
        summary = "스페이스 연락처 논리삭제",
        description = "특정 spaceId의 모든 연락처를 논리삭제합니다."
    )
    @Modifying
    @Query("UPDATE SpaceContacts sc SET sc.isDeleted = true WHERE sc.spaceId = :spaceId AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
    void softDeleteBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );

    @Operation(
        summary = "스페이스 ID와 tag를 받아 연락처를 조회합니다.",
        description = "스페이스 ID와 tag를 받아 연락처를 조회합니다."
    )
    @Query("SELECT sc FROM SpaceContacts sc LEFT JOIN FETCH sc.contactTag WHERE sc.spaceId = :spaceId AND sc.contactTag.tag = :tag AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
    List<SpaceContacts> findBySpaceIdAndTag(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "태그", required = true) @Param("tag") String tag
    );

    /**
     * 특정 ContactTag를 참조하는 연락처들을 조회
     * @param contactTagId 조회할 ContactTag ID
     * @return 해당 태그를 참조하는 연락처 목록
     */
    @Operation(
        summary = "태그를 참조하는 연락처 조회",
        description = "특정 ContactTag를 참조하는 연락처들을 조회합니다."
    )
    @Query("SELECT sc FROM SpaceContacts sc WHERE sc.contactTag.id = :contactTagId")
    List<SpaceContacts> findByContactTagId(
        @Parameter(description = "ContactTag ID", required = true) @Param("contactTagId") Long contactTagId
    );

    /**
     * 특정 ContactTag를 참조하는 연락처들의 contactTag를 null로 설정 (대량 처리용)
     * @param contactTagId null로 설정할 ContactTag ID
     */
    @Operation(
        summary = "연락처 태그 참조 제거 (대량 처리)",
        description = "특정 ContactTag를 참조하는 연락처들의 contactTag를 null로 설정합니다. (대량 데이터 처리용)"
    )
    @Modifying
    @Query("UPDATE SpaceContacts sc SET sc.contactTag = null WHERE sc.contactTag.id = :contactTagId")
    void removeContactTagReferenceBulk(
        @Parameter(description = "ContactTag ID", required = true) @Param("contactTagId") Long contactTagId
    );
}
