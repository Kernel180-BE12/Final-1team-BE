package org.fastcampus.jober.space.repository;

import java.util.List;
import java.util.Optional;

import org.fastcampus.jober.space.entity.ContactTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface ContactTagRepository extends JpaRepository<ContactTag, Long> {
    
    /**
     * 스페이스 ID로 태그 목록을 조회하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @return 해당 스페이스의 태그 목록
     */
    @Operation(
        summary = "태그 목록 조회",
        description = "스페이스 ID로 태그 목록을 조회합니다."
    )
    @Query("SELECT ct FROM ContactTag ct WHERE ct.spaceId = :spaceId AND ct.isDeleted = false")
    List<ContactTag> findBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );
    
    /**
     * 스페이스 ID와 태그명으로 삭제된 태그가 존재하는지 체크하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 삭제된 태그가 존재하면 true, 없으면 false
     */
    @Operation(
        summary = "삭제된 태그 중복 체크",
        description = "스페이스 ID와 태그명으로 삭제된 태그가 존재하는지 체크합니다."
    )
    @Query("SELECT COUNT(ct) > 0 FROM ContactTag ct WHERE ct.spaceId = :spaceId AND ct.tag = :tag AND ct.isDeleted = true")
    boolean existsBySpaceIdAndTagIsDeletedTrue(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "태그명", required = true) @Param("tag") String tag
    );

    /**
     * 스페이스 ID와 태그명으로 중복 체크하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 중복되는 태그가 있으면 true, 없으면 false
     */
    @Operation(
        summary = "태그 중복 체크",
        description = "스페이스 ID와 태그명으로 중복 체크합니다."
    )
    @Query("SELECT COUNT(ct) > 0 FROM ContactTag ct WHERE ct.spaceId = :spaceId AND ct.tag = :tag AND ct.isDeleted = false")
    boolean existsBySpaceIdAndTag(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "태그명", required = true) @Param("tag") String tag
    );

    /**
     * 스페이스 ID와 태그명으로 삭제된 태그를 조회하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 해당 조건에 맞는 태그
     */
    @Operation(
        summary = "삭제된 태그 조회",
        description = "스페이스 ID와 태그명으로 삭제된 태그를 조회합니다."
    )
    @Query("SELECT ct FROM ContactTag ct WHERE ct.spaceId = :spaceId AND ct.tag = :tag AND ct.isDeleted = true")
    Optional<ContactTag> findBySpaceIdAndTagIsDeletedTrue(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "태그명", required = true) @Param("tag") String tag
    );
    
    /**
     * 스페이스 ID와 태그명으로 태그를 조회하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 해당 조건에 맞는 태그
     */
    @Operation(
        summary = "태그 조회",
        description = "스페이스 ID와 태그명으로 태그를 조회합니다."
    )
    @Query("SELECT ct FROM ContactTag ct WHERE ct.spaceId = :spaceId AND ct.tag = :tag AND ct.isDeleted = false")
    Optional<ContactTag> findBySpaceIdAndTag(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "태그명", required = true) @Param("tag") String tag
    );
}
