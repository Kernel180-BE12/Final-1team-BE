package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
import org.fastcampus.jober.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space,Long> {

    List<Space> findByMemberId(Long memberId);
}
