package org.fastcampus.jober.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.user.entity.Users;

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

    public boolean isAdminUser(final Users user) {
        return this.admin.getUserId().equals(user.getUserId());
    }

    public void deleteBy(final Users user) {
        if (isAdminUser(user)) {
            this.deleaAt = now();
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN, "스페이스 관리자만 가능합니다.");
        }
    }

    public void updateBy(final SpaceUpdateRequestDto dto, final Users user) {
        if (isAdminUser(user)) {
            this.spaceName = dto.getSpaceName();
            this.adminName = dto.getAdminName();
            this.adminNum = dto.getAdminNum();
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN, "스페이스 관리자만 가능합니다.");
        }

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
