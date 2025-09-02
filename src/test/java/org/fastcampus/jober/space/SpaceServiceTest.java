//package org.fastcampus.jober.space;
//
//import org.fastcampus.jober.error.BusinessException;
//import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
//import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
//import org.fastcampus.jober.space.dto.response.SpaceResponseDto;
//import org.fastcampus.jober.space.entity.Space;
//import org.fastcampus.jober.space.mapper.SpaceMapper;
//import org.fastcampus.jober.space.repository.SpaceMemberRepository;
//import org.fastcampus.jober.space.repository.SpaceRepository;
//import org.fastcampus.jober.space.service.SpaceService;
//import org.fastcampus.jober.user.entity.Users;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.Authentication;
//
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
 // User 엔티티 업뎃 후 테스트 예정

//class SpaceServiceTest {
//
//    @Mock
//    private SpaceRepository spaceRepository;
//
//    @Mock
//    private SpaceMemberRepository spaceMemberRepository;
//
//    @Mock
//    private SpaceMapper spaceMapper;
//
//    @InjectMocks
//    private SpaceService spaceService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createSpace_success() {
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("테스트 스페이스");
//        dto.setAdminName("admin");
//        dto.setAdminNum("1L");
//
//        Space space = new Space();
//        when(spaceMapper.toEntity(dto)).thenReturn(space);
//
//        spaceService.createSpace(dto);
//
//        verify(spaceRepository, times(1)).save(space);
//    }
//
//    @Test
//    void updateSpace_success() {
//        // given
//        Long spaceId = 1L;
//
//        // DTO 생성
//        SpaceUpdateRequestDto dto = SpaceUpdateRequestDto.builder()
//                .spaceName("변경된 이름")
//                .adminName("admin")
//                .adminNum("010-1234-5678")
//                .build();
//
//        // Users 객체 생성 (builder 사용)
//        Users user = Users.builder()
//                .userId(1L)
//                .username("admin")
//                .build();
//
//        // 기존 Space 객체 생성
//        Space existingSpace = Space.builder()
//                .spaceId(spaceId)
//                .spaceName("기존 스페이스")
//                .admin(user)
//                .spaceMembers(Collections.emptyList())
//                .build();
//
//        // Mockito stubbing
//        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(existingSpace));
//        when(spaceRepository.save(existingSpace)).thenReturn(existingSpace);
//        when(spaceMapper.toResponseDto(existingSpace)).thenReturn(
//                SpaceResponseDto.builder()
//                        .spaceId(spaceId)
//                        .spaceName("변경된 이름")
//                        .build()
//        );
//
//        // when
//        SpaceResponseDto result = spaceService.updateSpace(spaceId, dto, user);
//
//        // then
//        verify(spaceMapper, times(1)).updateSpaceFromDto(dto, existingSpace);
//        verify(spaceRepository, times(1)).save(existingSpace);
//
//        // 결과 검증
//        assertEquals("변경된 이름", result.getSpaceName());
//        assertEquals(spaceId, result.getSpaceId());
//    }
//
//
//    @Test
//    void updateSpace_forbidden() {
//        Long spaceId = 1L;
//        SpaceUpdateRequestDto dto = new SpaceUpdateRequestDto();
//        dto.setSpaceName("변경된 이름");
//
//        Space existingSpace = new Space();
//        existingSpace.setAdminName("someoneElse");
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn("admin");
//
//        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(existingSpace));
//
//        BusinessException ex = assertThrows(BusinessException.class, () ->
//                spaceService.updateSpace(spaceId, dto, user));
//
//        assertEquals("스페이스 수정 권한이 없습니다.", ex.getMessage());
//    }
//}
