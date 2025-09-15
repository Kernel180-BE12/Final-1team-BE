package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.response.SpaceListResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.dto.SpaceEntityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SpaceRepository extends JpaRepository<Space,Long> {

//    Optional<Space> findBySpaceName(String spaceName);

    default Space findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));
    }

    @Query("""
           SELECT new org.fastcampus.jober.space.repository.dto.SpaceEntityDto(s.spaceId, s.spaceName, sm.authority) 
           FROM Space s 
           LEFT JOIN s.spaceMembers sm 
           WHERE sm.user.userId = :userId
           """)
    List<SpaceEntityDto> findSpacesByUserId(@Param("userId") Long userId);
}
