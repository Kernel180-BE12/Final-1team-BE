package org.fastcampus.jober.template.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String purposeType;
    private String channelType;
    private String status;
    private Long latestVersionId;
    private String tagsJson;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
}
