// package org.fastcampus.jober.space;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
// import org.fastcampus.jober.space.entity.Space;
// import org.fastcampus.jober.space.repository.SpaceRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static
// org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional // 매 테스트 끝나면 롤백
// class SpaceControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private SpaceRepository spaceRepository;
//
//    @BeforeEach
//    void setUp() {
//        spaceRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("스페이스 생성 성공 → 201 Created 반환")
//    @WithMockUser(username = "testuser", roles = {"USER"}) // 인증된 사용자로 테스트
//    void createSpace_Success() throws Exception {
//        // given
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("테스트 스페이스");
//
//        // when & then
//        mockMvc.perform(post("/spaces")
//                        .with(csrf()) // CSRF 토큰 추가
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated());
//
//        // DB 검증
//        assertThat(spaceRepository.findAll()).hasSize(1);
//        Space saved = spaceRepository.findAll().get(0);
//        assertThat(saved.getSpaceName()).isEqualTo("테스트 스페이스");
//    }
//
//    @Test
//    @DisplayName("스페이스 생성 실패 → 유효성 검증 에러 (400 Bad Request)")
//    @WithMockUser(username = "testuser", roles = {"USER"}) // 인증된 사용자로 테스트
//    void createSpace_Fail_ValidationError() throws Exception {
//        // given (spaceName 비워둠 → 유효성 검증 실패 유도)
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("");
//
//        // when & then
//        mockMvc.perform(post("/spaces")
//                        .with(csrf()) // CSRF 토큰 추가
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").exists()); // 예외 처리 핸들러 응답 확인
//    }
//
//    @Test
//    @DisplayName("CSRF 토큰 없이 요청 시 403 Forbidden")
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    void createSpace_WithoutCsrf_ShouldReturn403() throws Exception {
//        // given
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("테스트 스페이스");
//
//        // when & then
//        mockMvc.perform(post("/spaces")
//                        // .with(csrf()) 생략 - CSRF 토큰 없음
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @DisplayName("인증 없이 요청 시 401 Unauthorized")
//    void createSpace_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
//        // @WithMockUser 없음 - 인증되지 않은 사용자
//
//        // given
//        SpaceCreateRequestDto dto = new SpaceCreateRequestDto();
//        dto.setSpaceName("테스트 스페이스");
//
//        // when & then
//        mockMvc.perform(post("/spaces")
//                        .with(csrf()) // CSRF는 있지만 인증이 없음
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isUnauthorized());
//    }
// }
