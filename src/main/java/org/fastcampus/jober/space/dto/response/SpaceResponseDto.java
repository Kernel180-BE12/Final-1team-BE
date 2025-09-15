package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceResponseDto {
    @Schema(description = "스페이스 식별자(ID)", example = "1001")
    private Long spaceId;

    @Schema(description = "스페이스 이름", example = "회의실 A")
    private String spaceName;

    @Schema(description = "관리자 이름", example = "홍길동")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "010-1234-5678")
    private String adminNum;

    @Schema(description = "스페이스 URL", example = "https://company.com/spaces/meetingA")
    private String spaceUrl;


}
