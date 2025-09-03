package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SpaceContacts extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phoneNum;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "space_id", nullable = false)
    private Long spaceId;
    
    // 커스텀 빌더
    public static SpaceContactsBuilder builder() {
        return new SpaceContactsBuilder();
    }
    
    public static class SpaceContactsBuilder {
        private Long id;
        private String name;
        private String phoneNum;
        private String email;
        private Long spaceId;
        
        public SpaceContactsBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public SpaceContactsBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public SpaceContactsBuilder phoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
            return this;
        }
        
        public SpaceContactsBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public SpaceContactsBuilder spaceId(Long spaceId) {
            this.spaceId = spaceId;
            return this;
        }
        
        public SpaceContacts build() {
            return new SpaceContacts(id, name, phoneNum, email, spaceId);
        }
    }

    /**
     * 연락처 정보 유효성 검증
     */
    public void validateContactInfo() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (phoneNum == null || phoneNum.trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호는 필수입니다.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
    }
    

}
