package org.fastcampus.jober.space.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"spaceId", "tag"})
})
public class ContactTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag; 

    @Column(nullable = false)
    private Long spaceId;

    /**
     * ContactTag 엔티티를 생성하는 빌더 메서드
     * 
     * @param tag 태그명
     * @param spaceId 스페이스 ID
     * @return ContactTag 엔티티
     */
    public static ContactTag create(String tag, Long spaceId) {
        return ContactTag.builder()
            .tag(tag)
            .spaceId(spaceId)
            .build();
    }
    
}
