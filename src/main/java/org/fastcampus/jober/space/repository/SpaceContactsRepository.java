package org.fastcampus.jober.space.repository;

import org.fastcampus.jober.space.entity.SpaceContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceContactsRepository extends JpaRepository<SpaceContacts, Long> {
    
    /**
     * 스페이스 ID로 연락처 목록을 조회합니다.
     * 
     * @param spaceId 조회할 스페이스 ID
     * @return 해당 스페이스의 연락처 목록
     */
    List<SpaceContacts> findBySpaceId(Long spaceId);
    
    /**
     * 스페이스 ID로 모든 연락처를 삭제합니다.
     * 
     * @param spaceId 삭제할 스페이스 ID
     */
    void deleteBySpaceId(Long spaceId);

    
}
