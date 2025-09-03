package org.fastcampus.jober.template.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.template.entity.enums.Status;
import org.fastcampus.jober.template.dto.response.TemplateTitleResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private Boolean isAccepted;

    /**
     * 이 템플릿의 제목만 추출합니다.
     * @return 템플릿 제목
     */
    public String extractTitle() {
        return this.title;
    }

    /**
     * 템플릿 리스트에서 제목들만 추출합니다.
     * @param templates 템플릿 리스트
     * @return 제목 리스트
     */
    public static List<String> extractTitles(List<Template> templates) {
        if (templates == null) {
            return new ArrayList<>();
        }
        return templates.stream()
                .filter(Objects::nonNull)
                .map(Template::extractTitle)
                .collect(Collectors.toList());
    }

    /**
     * 특정 spaceId에 속하는 템플릿들을 필터링합니다.
     * @param templates 템플릿 리스트
     * @param spaceId 스페이스 ID
     * @return 필터링된 템플릿 리스트
     */
    public static List<Template> filterBySpaceId(List<Template> templates, Long spaceId) {
        if (templates == null || spaceId == null) {
            return new ArrayList<>();
        }
        return templates.stream()
                .filter(Objects::nonNull)
                .filter(template -> spaceId.equals(template.getSpaceId()))
                .collect(Collectors.toList());
    }

    /**
     * 이 템플릿이 특정 spaceId에 속하는지 확인합니다.
     * @param spaceId 스페이스 ID
     * @return 속하는 경우 true
     */
    public boolean belongsToSpace(Long spaceId) {
        if (this.spaceId == null || spaceId == null) {
            return false;
        }
        return this.spaceId.equals(spaceId);
    }

    /**
     * 특정 spaceId의 템플릿들을 조회하고 DTO로 변환합니다.
     * @param repository TemplateRepository
     * @param spaceId 스페이스 ID
     * @return TemplateTitleResponseDto 리스트
     */
    public static List<TemplateTitleResponseDto> findTitlesBySpaceId(
            org.fastcampus.jober.template.repository.TemplateRepository repository,
            Long spaceId) {
        return TemplateTitleResponseDto.fromTitleList(repository.findTitlesBySpaceId(spaceId));
    }

    /**
     * 특정 spaceId의 템플릿들을 조회합니다.
     * @param repository TemplateRepository
     * @param spaceId 스페이스 ID
     * @return Template 리스트
     */
    public static List<Template> findBySpaceId(
            org.fastcampus.jober.template.repository.TemplateRepository repository,
            Long spaceId) {
        return repository.findBySpaceId(spaceId);
    }

    public Boolean updateIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
        return this.isSaved;
    }
}
