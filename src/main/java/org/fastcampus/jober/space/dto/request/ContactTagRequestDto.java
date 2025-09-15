package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.fastcampus.jober.space.entity.ContactTag;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "연락처 태그 추가 요청 DTO")
public class ContactTagRequestDto {
    
    @Schema(description = "태그", example = "프리랜서")
    private String tag;
    
    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;
    
    /**
     * DTO를 ContactTag 엔티티로 변환하는 메서드
     * 
     * @return ContactTag 엔티티
     */
    public ContactTag toEntity() {
        return ContactTag.builder()
            .tag(tag)
            .spaceId(spaceId)
            .build();
    }
}
