package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.fastcampus.jober.user.entity.Users;

@Setter
@Getter
public class  SpaceUpdateRequestDto {
    @Schema(description = "수정할 스페이스 이름", example = "회의실 B")
    private String spaceName;

    @Schema(description = "수정할 관리자 이름", example = "이몽룡")
    private String adminName;

    @Schema(description = "수정할 관리자 연락처", example = "010-9876-5432")
    private String adminNum;

    @Schema(description = "수정 요청한 사용자", example = "Users 객체")
    private Users user;
}
