package org.fastcampus.jober.space.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.fastcampus.jober.space.entity.SpaceMember;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {}
