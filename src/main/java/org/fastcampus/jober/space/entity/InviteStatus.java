package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.fastcampus.jober.common.entity.BaseEntity;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long statusId;

    private String userEmail;
    private InviteStatusType status;
}
