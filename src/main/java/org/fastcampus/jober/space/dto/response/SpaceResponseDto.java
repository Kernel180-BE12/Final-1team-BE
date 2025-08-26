package org.fastcampus.jober.space.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** Space 조회 응답 DTO **/

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceResponseDto {

    private String spaceName;
    private List<Member> members;
    private List<BigSendDto> bigSends;
}
