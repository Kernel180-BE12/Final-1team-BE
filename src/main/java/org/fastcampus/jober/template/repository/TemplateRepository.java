package org.fastcampus.jober.template.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fastcampus.jober.template.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 템플릿 데이터 접근을 위한 Repository
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    /**
     * spaceID가 0이 아닌 템플릿들의 title만 조회
     * @param spaceId 스페이스 ID (0이 아닌 값)
     * @return 템플릿 제목 리스트
     */
    @Operation(
        summary = "템플릿 제목 조회",
        description = "spaceID가 0이 아닌 특정 spaceId의 템플릿 제목들을 조회합니다."
    )
    @Query("SELECT t.title FROM Template t WHERE t.spaceId = :spaceId AND t.spaceId != 0")
    List<String> findTitlesBySpaceIdNotZero(
        @Parameter(description = "스페이스 ID (0이 아닌 값)", required = true) @Param("spaceId") Long spaceId
    );
    
    /**
     * spaceID가 0이 아닌 템플릿들을 조회
     * @param spaceId 스페이스 ID (0이 아닌 값)
     * @return 템플릿 엔티티 리스트
     */
    @Operation(
        summary = "템플릿 엔티티 조회",
        description = "spaceID가 0이 아닌 특정 spaceId의 템플릿들을 조회합니다."
    )
    @Query("SELECT t FROM Template t WHERE t.spaceId = :spaceId AND t.spaceId != 0")
    List<Template> findBySpaceIdNotZero(
        @Parameter(description = "스페이스 ID (0이 아닌 값)", required = true) @Param("spaceId") Long spaceId
    );
}
