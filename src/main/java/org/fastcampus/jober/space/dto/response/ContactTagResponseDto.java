package org.fastcampus.jober.space.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.fastcampus.jober.space.entity.ContactTag;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "연락처 태그 응답 DTO")
public class ContactTagResponseDto {

    @Schema(description = "태그 목록")
    private List<ContactTag> tags;

    /**
     * ContactTag 엔티티를 ContactTagResponseDto로 변환하는 메서드
     * 
     * @param contactTag ContactTag 엔티티
     * @return ContactTagResponseDto
     */
    public static ContactTagResponseDto fromEntity(ContactTag contactTag) {
        return ContactTagResponseDto.builder()
            .tags(List.of(contactTag))
            .build();
    }

    /**
     * ContactTag 엔티티 목록을 ContactTagResponseDto로 변환하는 메서드
     * 
     * @param contactTags ContactTag 엔티티 목록
     * @return ContactTagResponseDto
     */
    public static ContactTagResponseDto fromEntities(List<ContactTag> contactTags) {
        return ContactTagResponseDto.builder()
            .tags(contactTags)
            .build();
    }
}
