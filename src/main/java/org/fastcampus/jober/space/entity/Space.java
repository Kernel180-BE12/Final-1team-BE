package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.fastcampus.jober.user.entity.Users;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long spaceId;

    @NotBlank(message = "스페이스 이름은 필수입니다.")
    private String spaceName;

    @OneToOne
    @JoinColumn(name = "id")
    private Users admin;

    private String adminName;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
    private String adminNum;

    private String spaceUrl;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    private List<SpaceMember> spaceMembers;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean isAdminUser(final Users user) {
        return this.admin.getUserId().equals(user.getUserId());
    }


    // 이 아래로 꼭 필요할지?
//    private String faxNum;
//    private String email;
//    private String businessType;
//    private String corporateRegistrationNo;
//    private String businessRegistrationNo;
//    private String signatureImgUrl;
//    private LocalDate businessOpenDate;
//    private String businessCategory;
//    private String businessItem;
//    private String taxEmail;

}
