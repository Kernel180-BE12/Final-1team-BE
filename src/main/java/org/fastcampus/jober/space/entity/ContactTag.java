package org.fastcampus.jober.space.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import org.fastcampus.jober.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"spaceId", "tag"})
})
public class ContactTag extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag; 

    @Column(name = "space_id", nullable = false)
    private Long spaceId;
    
    /**
     * 연락처 태그 수정
     * @param tag 수정할 태그
     */
    public void updateTag(String tag) {
        this.tag = tag;
    }

}
