package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.space.validator.NotNullOrEmpty;
import org.hibernate.annotations.ColumnDefault;

import java.util.Optional;

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

    @Column
    private String tag;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    // 커스텀 빌더
    public static SpaceContactsBuilder builder() {
        return new SpaceContactsBuilder();
    }

    public static class SpaceContactsBuilder {

        private Long id;

        @NotNullOrEmpty
        private String name;

        @NotNullOrEmpty
        private String phoneNum;

        @NotNullOrEmpty
        private String email;

        private String tag;

        private Boolean isDeleted;

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

        public SpaceContactsBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public SpaceContactsBuilder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public SpaceContactsBuilder spaceId(Long spaceId) {
            this.spaceId = spaceId;
            return this;
        }

        public SpaceContacts build() {
            return new SpaceContacts(id, name, phoneNum, email, tag, isDeleted != null ? isDeleted : false, spaceId);
        }
    }

    /**
     * 연락처 정보 유효성 검증
     */
    /**
     * 연락처 정보 수정
     */
    public void updateContactInfo(String name, String phoneNumber, String email, String tag) {
        Optional.ofNullable(name).filter(n -> !n.trim().isEmpty()).ifPresent(n -> this.name = n);
        Optional.ofNullable(email).filter(n -> !n.trim().isEmpty()).ifPresent(n -> this.name = n);
        Optional.ofNullable(tag).filter(n -> !n.trim().isEmpty()).ifPresent(n -> this.name = n);
    }

    /**
     * 스페이스 삭제 권한 검증
     *
     * @param spaceId 검증할 스페이스 ID
     * @throws IllegalArgumentException 권한이 없는 경우
     */
    public void validateDeletePermission(Long spaceId) {
        if (!this.spaceId.equals(spaceId)) {
            throw new IllegalArgumentException("해당 스페이스에서 연락처를 삭제할 권한이 없습니다.");
        }
    }

    /**
     * 연락처를 논리삭제합니다 (isDeleted를 true로 설정)
     */
    public void softDelete() {
        this.isDeleted = true;
    }

}
