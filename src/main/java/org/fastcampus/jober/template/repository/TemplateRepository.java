package org.fastcampus.jober.template.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fastcampus.jober.template.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

/**
 * 템플릿 데이터 접근을 위한 Repository
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * 특정 spaceId의 템플릿들의 title만 조회
     * @param spaceId 스페이스 ID
     * @return 템플릿 제목 리스트
     */
    @Operation(
        summary = "템플릿 제목 조회",
        description = "특정 spaceId의 템플릿 제목들을 조회합니다."
    )
    @Query("SELECT t.title FROM Template t WHERE t.spaceId = :spaceId")
    List<String> findTitlesBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );

    /**
     * 특정 spaceId의 템플릿들을 조회
     * @param spaceId 스페이스 ID
     * @return 템플릿 엔티티 리스트
     */
    @Operation(
        summary = "템플릿 엔티티 조회",
        description = "특정 spaceId의 템플릿들을 조회합니다."
    )
    @Query("SELECT t FROM Template t WHERE t.spaceId = :spaceId")
    List<Template> findBySpaceId(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId
    );

    Optional<Template> findByIdAndSpaceId(Long id, Long spaceId);

}
