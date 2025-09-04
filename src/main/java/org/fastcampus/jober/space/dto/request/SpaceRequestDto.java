package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceRequestDto {
    @Schema(description = "스페이스 이름", example = "우두머리경두")
    private String spaceName;

    @Schema(description = "관리자 이름", example = "홍길동")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "010-1234-5678")
    private String adminNum;

    // 이 밑으로 과연 필요할지?
//    private String faxNum;
//    private String spaceEmail;
//    private String spaceUrl;


}
