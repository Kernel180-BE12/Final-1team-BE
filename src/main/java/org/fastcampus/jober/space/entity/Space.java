package org.fastcampus.jober.space.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.fastcampus.jober.space.dto.response.SpaceResponseDto;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "스페이스 이름은 필수입니다.")
    private String spaceName;

    private String adminName;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
    private String adminNum;

    private String spaceUrl;
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

    public SpaceResponseDto toResponseDto() {
        return SpaceResponseDto.builder()
                .id(this.id)
                .spaceName(this.spaceName)
                .adminName(this.adminName)
                .adminNum(this.adminNum)
                .spaceUrl(this.spaceUrl)
                .build();
    }

    public void updateSpaceInfo(String spaceName, String adminName, String adminNum) {
        if (spaceName != null && !spaceName.isBlank()) {this.spaceName = spaceName;}
        if (adminName != null && !adminName.isBlank()) {this.adminName = adminName;}
        if (adminNum != null && !adminNum.isBlank()) {this.adminNum = adminNum;}
    }
}
