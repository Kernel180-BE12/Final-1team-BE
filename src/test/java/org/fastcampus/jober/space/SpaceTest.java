// package org.fastcampus.jober.space;
//
// import jakarta.transaction.Transactional;
// import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
// import org.fastcampus.jober.space.dto.request.SpaceRequestDto;
// import org.fastcampus.jober.space.entity.Space;
// import org.fastcampus.jober.space.repository.SpaceRepository;
// import org.fastcampus.jober.space.service.SpaceService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// import java.util.Optional;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// @SpringBootTest
// @Transactional
// public class SpaceTest {
//
//    @Autowired
//    private SpaceRepository spaceRepository;
//    @Autowired
//    private SpaceService spaceService;
//
//    @Test
//    void createSpace_Success() {
//
//        // given
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("테스트");
//        dto.setAdminName("영경");
//        dto.setAdminNum("010-1111-1111");
//
//        // when
//        spaceService.createSpace(dto);
//
//        // then
//        Optional<Space> savedSpace = spaceRepository.findBySpaceName("테스트");
//        assertThat(savedSpace).isPresent();
//        assertThat(savedSpace.get().getSpaceName()).isEqualTo("테스트");
//    }
//
//    @Test
//    void createSpace_Fail_NameNull() {
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName(null);
//        dto.setAdminName("영경");
//        dto.setAdminNum("010-1111-1111");
//
//        assertThrows(IllegalArgumentException.class, () -> spaceService.createSpace(dto));
//    }
//
//    @Test
//    void createSpace_Fail_NameEmpty() {
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName(" ");
//        dto.setAdminName("dudrud");
//        dto.setAdminNum("010-1111-1111");
//
//        assertThrows(IllegalArgumentException.class, () -> spaceService.createSpace(dto));
//    }
// }
