package org.fastcampus.jober.space.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import org.fastcampus.jober.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;
    
    // 커스텀 빌더
    public static ContactTagBuilder builder() {
        return new ContactTagBuilder();
    }
    
    public static class ContactTagBuilder {
        private Long id;
        private String tag;
        private Long spaceId;
        private Boolean isDeleted;
        
        public ContactTagBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public ContactTagBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }
        
        public ContactTagBuilder spaceId(Long spaceId) {
            this.spaceId = spaceId;
            return this;
        }
        
        public ContactTagBuilder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }
        
        public ContactTag build() {
            return new ContactTag(id, tag, spaceId, isDeleted != null ? isDeleted : false);
        }
    }
    
    /**
     * 연락처 태그 수정
     * @param tag 수정할 태그
     */
    public void updateTag(String tag) {
        this.tag = tag;
    }

    /**
     * 연락처 태그 삭제
     */
    public void softDelete() {
        this.isDeleted = true;
    }

    /**
     * 연락처 태그 복구
     */
    public void restore() {
        this.isDeleted = false;
    }

}
