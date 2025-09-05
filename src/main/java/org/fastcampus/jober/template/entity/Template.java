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


    public Boolean updateIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
        return this.isSaved;
    }

}
