package org.fastcampus.jober.space.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 등록, 수정 등에 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceRequestDto {

    private String spaceName;
    private String adminName;
    private String adminNum;
    // 이 밑으로 과연 필요할지?
    private String faxNum;
    private String spaceEmail;
    private String spaceUrl;


}
