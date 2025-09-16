package org.fastcampus.jober.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactTagDeleteRequestDto {

    @Schema(description = "태그 ID", example = "1")
    private Long id;

    @Schema(description = "스페이스 ID", example = "1")
    private Long spaceId;

}
