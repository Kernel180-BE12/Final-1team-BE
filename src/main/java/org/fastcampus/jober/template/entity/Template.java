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

    private Integer totalAttempts;

    private Boolean isSaved; // 0:저장안됨 / 1:저장됨

    private String type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAccepted = false;


    public Boolean updateIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
        return this.isSaved;
    }

    /**
     * 특정 spaceId와 templateId의 템플릿을 조회합니다.
     * @param repository TemplateRepository
     * @param spaceId 스페이스 ID
     * @param templateId 템플릿 ID
     * @return Template 엔티티
     */
    public static Template findBySpaceIdAndTemplateId(
            org.fastcampus.jober.template.repository.TemplateRepository repository,
            Long spaceId,
            Long templateId) {
        return repository.findBySpaceIdAndTemplateId(spaceId, templateId);
    }

    /**
     * 특정 spaceId와 templateId의 템플릿을 조회하고 상세 응답 DTO로 변환합니다.
     * @param repository TemplateRepository
     * @param spaceId 스페이스 ID
     * @param templateId 템플릿 ID
     * @return TemplateDetailResponseDto
     */
    public static org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto findDetailBySpaceIdAndTemplateId(
            org.fastcampus.jober.template.repository.TemplateRepository repository,
            Long spaceId,
            Long templateId) {
        Template template = repository.findBySpaceIdAndTemplateIdWithAllFields(spaceId, templateId);
        if (template == null) {
            return null;
        }
        return org.fastcampus.jober.template.dto.response.TemplateDetailResponseDto.from(template);
    }
}
