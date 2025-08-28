package org.fastcampus.jober.template.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.template.entity.enums.ChannelType;
import org.fastcampus.jober.template.entity.enums.PurposeType;
import org.fastcampus.jober.template.entity.enums.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //템플릿 아이디

    private Long spaceId; // 스페이스 아이디(fk)

    @Column(length = 120)
    private String title; //템플릿 제목

    @Enumerated(EnumType.STRING)
    private PurposeType purposeType; //목적구분

    @Enumerated(EnumType.STRING)
    private ChannelType channelType;//채널타입

    @Enumerated(EnumType.STRING)
    private Status status; //상태

    private Long latestVersionId; //최신버전id(fk)

    @Column(length=80)
    private Long kakaoTemplateId; //카카오 실제 템플릿 id

    @Column(columnDefinition ="JSON" )
    private String tagsJson; //태그json

    private LocalDateTime createdAt; // 생성일시
    private String createdBy; //생성자회원id (fk)
    private LocalDateTime updatedAt; //수정일시
}
