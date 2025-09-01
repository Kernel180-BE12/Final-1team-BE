package org.fastcampus.jober.space.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceCreateRequestDto {

    @NotBlank(message = "스페이스 이름은 필수입니다.")
    private String spaceName;

    @NotBlank
    private String adminName;

    @NotBlank
    private String adminNum;
}
