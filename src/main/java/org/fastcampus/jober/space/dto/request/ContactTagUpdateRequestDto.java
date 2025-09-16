package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactTagUpdateRequestDto {

    @Schema(description = "태그 ID", example = "1")
    private Long id;

    @Schema(description = "태그", example = "퇴사자")
    private String tag;

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;

}
