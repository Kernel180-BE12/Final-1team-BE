package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 연락처 정보 수정 요청 DTO
 */
@Schema(description = "연락처 정보 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceContactsUpdateRequestDto {

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;
    
    @Schema(description = "연락처 ID", example = "1")
    private Long contactId;

    @Schema(description = "연락처 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "연락처 전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @Schema(description = "연락처 이메일", example = "hong@example.com")
    private String email;
    
}
