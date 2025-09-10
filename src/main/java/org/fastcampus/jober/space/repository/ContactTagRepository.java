package org.fastcampus.jober.space.repository;

import java.util.List;
import java.util.Optional;

import org.fastcampus.jober.space.entity.ContactTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactTagRepository extends JpaRepository<ContactTag, Long> {
    
    /**
     * 스페이스 ID로 태그 목록을 조회하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @return 해당 스페이스의 태그 목록
     */
    List<ContactTag> findBySpaceId(Long spaceId);
    
    /**
     * 스페이스 ID와 태그명으로 중복 체크하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 중복되는 태그가 있으면 true, 없으면 false
     */
    boolean existsBySpaceIdAndTag(Long spaceId, String tag);
    
    /**
     * 스페이스 ID와 태그명으로 태그를 조회하는 메서드
     * 
     * @param spaceId 스페이스 ID
     * @param tag 태그명
     * @return 해당 조건에 맞는 태그
     */
    Optional<ContactTag> findBySpaceIdAndTag(Long spaceId, String tag);
}
