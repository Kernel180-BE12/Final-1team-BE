package org.fastcampus.jober.template.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.template.entity.enums.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@AllArgsConstructor (access = AccessLevel.PROTECTED)
@Entity
public class Template extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //템플릿 아이디

    private Long spaceId; // 스페이스 아이디(fk)

    @Column(length = 120)
    private String title; //템플릿 제목

    @Enumerated(EnumType.STRING)
    private Status status; //상태

    @Column(columnDefinition ="JSON" )
    private String extractedVariables; //태그json

    private LocalDateTime completedAt; // 생성일시

    private String sessionId;

    private String finalTemplate;

    private String htmlPreview;

    private String parameterizedTemplate;

    // @Builder.Default
    private Integer totalAttempts;

    // @Builder.Default
    private Boolean isSaved; // 0:저장안됨 / 1:저장됨

    private String type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccepted = false;


    // public Boolean updateIsSaved(Boolean isSaved) {
    //     this.isSaved = isSaved;
    //     return this.isSaved;
    // }


    /**
     * 템플릿 삭제 권한 검증
     * @param spaceId 검증할 스페이스 ID
     * @throws IllegalArgumentException 권한이 없는 경우
     */
    public void validateDeletePermission(Long spaceId) {
        if (this.isDeleted) {
            throw new IllegalArgumentException("이미 삭제된 템플릿입니다.");
        }
        if (!this.spaceId.equals(spaceId)) {
            throw new IllegalArgumentException("해당 스페이스에서 템플릿을 삭제할 권한이 없습니다.");
        }
    }

    /**
     * 템플릿을 논리적으로 삭제합니다.
     */
    public void softDelete() {
        this.isDeleted = true;
    }

}
