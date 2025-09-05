package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.fastcampus.jober.space.entity.SpaceContacts;

/**
 * 연락처 정보 수정 응답 DTO
 */
@Schema(description = "연락처 정보 수정 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceContactsUpdateResponseDto {
    
    @Schema(description = "연락처 ID", example = "1")
    private Long id;
    
    @Schema(description = "연락처 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "연락처 전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @Schema(description = "연락처 이메일", example = "hong@example.com")
    private String email;
    
    @Schema(description = "연락처 태그", example = "프리랜서")
    private String tag;

    @Schema(description = "수정된 시간")
    private LocalDateTime updatedAt;

    
    /**
     * SpaceContacts 엔티티로부터 SpaceContactsUpdateResponseDto 생성
     */
    public static SpaceContactsUpdateResponseDto fromEntities(SpaceContacts spaceContacts) {
        return SpaceContactsUpdateResponseDto.builder()
            .id(spaceContacts.getId())
            .name(spaceContacts.getName())
            .phoneNumber(spaceContacts.getPhoneNumber())
            .email(spaceContacts.getEmail())
            .tag(spaceContacts.getTag())
            .updatedAt(spaceContacts.getUpdatedAt())
            .build();
    }
}
