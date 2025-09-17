package org.fastcampus.jober.space.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import org.fastcampus.jober.common.entity.BaseEntity;
import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.user.entity.Users;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long spaceId;

  @NotBlank(message = "스페이스 이름은 필수입니다.")
  private String spaceName;

  @ManyToOne
  @JoinColumn(name = "admin_user_id")
  private Users admin;

  private String ownerName;

  @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
  private String ownerNum;

  private String spaceUrl;

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  private List<SpaceMember> spaceMembers;

  public void validateAdminUser(long userId) {
    if (!(admin.isSameUser(userId))) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "스페이스 관리자만 가능합니다.");
    }
  }

  // 부분 업데이트 (null 값 무시)
  public void updateSpaceFromDto(SpaceUpdateRequestDto dto) {
    if (dto.getSpaceName() != null && !dto.getSpaceName().isBlank()) {
      this.spaceName = dto.getSpaceName();
    }
  }
}
