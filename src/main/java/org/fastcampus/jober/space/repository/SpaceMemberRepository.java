package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
}
