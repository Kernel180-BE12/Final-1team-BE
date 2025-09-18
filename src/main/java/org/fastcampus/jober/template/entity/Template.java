package org.fastcampus.jober.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter를 추가하여 유연성을 확보하거나, 별도의 수정 메소드를 만듭니다.
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.template.entity.enums.Status;

@Getter
@Setter // 빌더 외에 객체 수정을 위해 Setter 추가 (혹은 필요한 필드에만 별도 수정 메소드 추가)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Template extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long spaceId;

    @Column(length = 120)
    private String title;

    private String description; // 설명 필드를 추가하는 것이 좋아보입니다.

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime completedAt;

    private String sessionId;

    private Integer totalAttempts;

    private String type;

    // --- ▼▼▼ 여기가 수정/추가된 필드입니다 ▼▼▼ ---

    @Column(columnDefinition = "TEXT")
    private String template; // 옛 이름: parameterizedTemplate

    @Column(columnDefinition = "TEXT")
    private String structuredTemplate; // 옛 이름: finalTemplate

    @Column(columnDefinition = "TEXT")
    private String editableVariables; // 옛 이름: extractedVariables

    private Boolean hasImage; // 새로 추가된 필드

    // --- ▲▲▲ 여기까지 수정/추가된 필드입니다 ▲▲▲ ---

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccepted = false;

    /** 템플릿 삭제 권한 검증 */
    public void validateDeletePermission(Long spaceId) {
        if (this.isDeleted) {
            throw new IllegalArgumentException("이미 삭제된 템플릿입니다.");
        }
        if (!this.spaceId.equals(spaceId)) {
            throw new IllegalArgumentException("해당 스페이스에서 템플릿을 삭제할 권한이 없습니다.");
        }
    }

    /** 템플릿을 논리적으로 삭제합니다. */
    public void softDelete() {
        this.isDeleted = true;
    }
}