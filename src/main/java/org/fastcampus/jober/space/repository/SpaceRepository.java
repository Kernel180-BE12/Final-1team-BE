package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space,Long> {

    Optional<Space> findBySpaceName(String spaceName);

    default Space findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "존재하지 않는 스페이스입니다."));
    }
}
