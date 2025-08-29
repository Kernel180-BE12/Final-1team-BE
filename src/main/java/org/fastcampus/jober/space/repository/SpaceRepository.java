package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space,Long> {

    Optional<Space> findBySpaceName(String spaceName);
}
