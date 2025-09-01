//package org.fastcampus.jober.space;
//
//import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
//import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
//import org.fastcampus.jober.space.entity.Space;
//import org.fastcampus.jober.space.repository.SpaceRepository;
//import org.fastcampus.jober.space.service.SpaceService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class SpaceUpdateTest {
//
//    @Autowired
//    private SpaceService spaceService;
//
//    @Autowired
//    private SpaceRepository spaceRepository;
//
//    private Long savedSpaceId;
//
//    @BeforeEach
//    void setup() {
//        Space space = Space.builder()
//                .spaceName("원래 이름")
//                .build();
//        savedSpaceId = spaceRepository.save(space).getSpaceId();
//    }
//
//    @Test
//    void updateSpace_success() {
//        // given
//        SpaceUpdateRequestDto dto = new SpaceUpdateRequestDto();
//        dto.setSpaceName("업데이트 이름");
//
//        // when
//        SpaceResponseDto updatedSpace = spaceService.updateSpace(savedSpaceId, dto, null); // Authentication은 null 처리
//
//        // then
//        assertEquals("업데이트 이름", updatedSpace.getSpaceName());
//    }
//
//    @Test
//    void updateSpace_fail_notFound() {
//        SpaceUpdateRequestDto dto = new SpaceUpdateRequestDto();
//        dto.setSpaceName("이름");
//
//        Long notExistId = 99999L;
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                spaceService.updateSpace(notExistId, dto, null)
//        );
//
//        assertTrue(exception.getMessage().contains("존재하지 않는 스페이스입니다."));
//    }
//}
