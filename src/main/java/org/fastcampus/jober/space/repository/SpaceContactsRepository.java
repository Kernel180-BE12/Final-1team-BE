package org.fastcampus.jober.space.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.fastcampus.jober.space.entity.SpaceContacts;

/** 스페이스 연락처 데이터 접근을 위한 Repository */
@Repository
public interface SpaceContactsRepository extends JpaRepository<SpaceContacts, Long> {

  /**
   * 특정 spaceId의 연락처들을 조회 (논리삭제되지 않은 연락처만)
   *
   * @param spaceId 스페이스 ID
   * @return 연락처 엔티티 리스트
   */
  @Operation(summary = "스페이스 연락처 조회", description = "특정 spaceId의 연락처들을 조회합니다. (삭제되지 않은 연락처만)")
  @Query(
      "SELECT sc FROM SpaceContacts sc WHERE sc.spaceId = :spaceId AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
  List<SpaceContacts> findBySpaceId(
      @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId);

  /**
   * 특정 spaceId의 모든 연락처를 논리삭제
   *
   * @param spaceId 삭제할 스페이스 ID
   */
  @Operation(summary = "스페이스 연락처 논리삭제", description = "특정 spaceId의 모든 연락처를 논리삭제합니다.")
  @Query(
      "UPDATE SpaceContacts sc SET sc.isDeleted = true WHERE sc.spaceId = :spaceId AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
  @Modifying
  void softDeleteBySpaceId(
      @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId);

  @Operation(summary = "스페이스 ID와 tag를 받아 연락처를 조회합니다.", description = "스페이스 ID와 tag를 받아 연락처를 조회합니다.")
  @Query(
      "SELECT sc FROM SpaceContacts sc WHERE sc.spaceId = :spaceId AND sc.tag = :tag AND (sc.isDeleted = false OR sc.isDeleted IS NULL)")
  List<SpaceContacts> findBySpaceIdAndTag(
      @Parameter(description = "스페이스 ID", required = true) @Param("spaceId") Long spaceId,
      @Parameter(description = "태그", required = true) @Param("tag") String tag);
}
