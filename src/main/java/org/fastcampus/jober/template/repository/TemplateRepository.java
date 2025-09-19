package org.fastcampus.jober.template.repository;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.fastcampus.jober.template.dto.response.TemplateListResponseDto;
import org.fastcampus.jober.template.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 템플릿 데이터 접근을 위한 Repository */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

  /**
   * 특정 spaceId의 템플릿들을 조회
   *
   * @param spaceId 스페이스 ID
   * @return 템플릿 엔티티 리스트
   */
  @Operation(summary = "템플릿 엔티티 조회", description = "특정 spaceId의 템플릿들을 조회합니다.")
  @Query("SELECT t FROM Template t WHERE t.spaceId = :spaceId")
  List<Template> findBySpaceId(
      @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId);

  Optional<Template> findByIdAndSpaceId(Long id, Long spaceId);

    /**
     * 특정 spaceId와 templateId의 템플릿을 조회 (completedAt(생성일시) 제외 모든 필드 조회)
     * @param spaceId 스페이스 ID
     * @param templateId 템플릿 ID
     * @return 템플릿 엔티티
     */
    @Query("SELECT t FROM Template t WHERE t.spaceId = :spaceId AND t.id = :templateId")
    Template findBySpaceIdAndTemplateIdWithAllFields(
        @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
        @Parameter(description = "템플릿 ID", required = true) @Param("templateId") Long templateId
    );


  @Query("""
SELECT t.id templateId, t.title, t.template
FROM Template t
JOIN SpaceMember sm ON t.spaceId = sm.space.spaceId
WHERE t.spaceId = :spaceId
AND sm.user.userId = :userId
AND t.isDeleted = false
""")
  List<TemplateListResponseDto> findAllBySpaceIdAndUserId(@Param("userId") Long userId, @Param("spaceId") Long spaceId);
}
