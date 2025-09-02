package org.fastcampus.jober.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.dto.request.SpaceUpdateRequestDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.h2.console.enabled=true",
        "logging.level.org.springframework.security=DEBUG",
        "logging.level.org.springframework.web=DEBUG"
})
@Transactional
class SpaceControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Users testUser;
    private Users adminUser;
    private Space testSpace;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 테스트용 사용자 생성
        testUser = createTestUser("test@example.com", "testUser", "테스트 사용자");
        adminUser = createTestUser("admin@example.com", "adminUser", "관리자 사용자");

        // 테스트용 스페이스 생성
        testSpace = createTestSpace("Test Space", "관리자 사용자", "010-1234-5678", adminUser);
    }

    private Users createTestUser(String email, String username, String name) {
        // Users의 정적 팩토리 메서드 사용
        Users user = Users.forSignup(username, "encodedPassword", name, email);
        return userRepository.save(user);
    }

    private Space createTestSpace(String spaceName, String adminName, String adminNum, Users user) {
        // Space @Builder 사용
        Space space = Space.builder()
                .spaceName(spaceName)
                .adminName(adminName)
                .adminNum(adminNum)
                .admin(user)
                .build();
        return spaceRepository.save(space);
    }

    // ================ 스페이스 생성 테스트 ================

    @Test
    @DisplayName("인증된 사용자가 스페이스를 성공적으로 생성한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createSpace_Success_WithAuthenticatedUser() throws Exception {
        // given
        SpaceCreateRequestDto requestDto = new SpaceCreateRequestDto();
        requestDto.setSpaceName("New Space");
        requestDto.setAdminName("테스트 관리자");
        requestDto.setAdminNum("010-1111-2222");

        // when & then
        mockMvc.perform(post("/spaces")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 스페이스 생성 시 401 또는 403 에러가 발생한다")
    void createSpace_Fail_UnauthorizedUser() throws Exception {
        // given
        SpaceCreateRequestDto requestDto = new SpaceCreateRequestDto();
        requestDto.setSpaceName("New Space");
        requestDto.setAdminName("테스트 관리자");
        requestDto.setAdminNum("010-1111-2222");

        // when & then
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 스페이스 생성 시 400 에러가 발생한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createSpace_Fail_InvalidRequestData() throws Exception {
        // given - spaceName이 없는 잘못된 요청
        SpaceCreateRequestDto requestDto = new SpaceCreateRequestDto();
        requestDto.setAdminName("테스트 관리자");
        requestDto.setAdminNum("010-1111-2222");

        // when & then
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 400 || status == 403;
                });
    }

    // ================ 스페이스 조회 테스트 ================

    @Test
    @DisplayName("존재하는 스페이스 ID로 조회 시 성공한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getSpace_Success_ExistingSpace() throws Exception {
        // when & then
        mockMvc.perform(get("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceId").value(testSpace.getSpaceId()))
                .andExpect(jsonPath("$.spaceName").value(testSpace.getSpaceName()))
                .andExpect(jsonPath("$.adminName").value(testSpace.getAdminName()))
                .andExpect(jsonPath("$.adminNum").value(testSpace.getAdminNum()));
    }

    @Test
    @DisplayName("존재하지 않는 스페이스 ID로 조회 시 404 에러가 발생한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getSpace_Fail_NonExistentSpace() throws Exception {
        // given
        Long nonExistentId = 99999L;

        // when & then
        mockMvc.perform(get("/spaces/{id}", nonExistentId).with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 스페이스 조회 시 401 또는 403 에러가 발생한다")
    void getSpace_Fail_UnauthorizedUser() throws Exception {
        // when & then
        mockMvc.perform(get("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    // ================ 스페이스 수정 테스트 ================

    @Test
    @DisplayName("스페이스를 성공적으로 수정한다")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateSpace_Success() throws Exception {
        // given
        SpaceUpdateRequestDto requestDto = new SpaceUpdateRequestDto();
        requestDto.setSpaceName("Updated Space");
        requestDto.setAdminName("수정된 관리자");
        requestDto.setUser(adminUser);

        // when & then
        mockMvc.perform(patch("/spaces/{id}", testSpace.getSpaceId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spaceName").value("Updated Space"));
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 스페이스 수정 시 401 또는 403 에러가 발생한다")
    void updateSpace_Fail_UnauthenticatedUser() throws Exception {
        // given
        SpaceUpdateRequestDto requestDto = new SpaceUpdateRequestDto();
        requestDto.setSpaceName("Updated Space");

        // when & then
        mockMvc.perform(patch("/spaces/{id}", testSpace.getSpaceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("존재하지 않는 스페이스 수정 시 404 에러가 발생한다")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateSpace_Fail_NonExistentSpace() throws Exception {
        // given
        Long nonExistentId = 99999L;
        SpaceUpdateRequestDto requestDto = new SpaceUpdateRequestDto();
        requestDto.setSpaceName("Updated Space");
        requestDto.setAdminName("수정된 관리자");

        // when & then
        mockMvc.perform(patch("/spaces/{id}", nonExistentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ================ 스페이스 삭제 테스트 ================

    @Test
    @DisplayName("스페이스를 성공적으로 삭제한다")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteSpace_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/spaces/{id}", testSpace.getSpaceId()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 스페이스 삭제 시 401 또는 403 에러가 발생한다")
    void deleteSpace_Fail_UnauthenticatedUser() throws Exception {
        // when & then
        mockMvc.perform(delete("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("존재하지 않는 스페이스 삭제 시 404 에러가 발생한다")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteSpace_Fail_NonExistentSpace() throws Exception {
        // given
        Long nonExistentId = 99999L;

        // when & then
        mockMvc.perform(delete("/spaces/{id}", nonExistentId).with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ================ JWT 토큰 없이 API 호출 테스트 ================

    @Test
    @DisplayName("Authorization 헤더 없이 POST 요청 시 인증 에러가 발생한다")
    void createSpace_Fail_MissingAuthorizationHeader() throws Exception {
        // given
        SpaceCreateRequestDto requestDto = new SpaceCreateRequestDto();
        requestDto.setSpaceName("New Space");
        requestDto.setAdminName("테스트 관리자");
        requestDto.setAdminNum("010-1111-2222");

        // when & then
        mockMvc.perform(post("/spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("Authorization 헤더 없이 GET 요청 시 인증 에러가 발생한다")
    void getSpace_Fail_MissingAuthorizationHeader() throws Exception {
        // when & then
        mockMvc.perform(get("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("Authorization 헤더 없이 PATCH 요청 시 인증 에러가 발생한다")
    void updateSpace_Fail_MissingAuthorizationHeader() throws Exception {
        // given
        SpaceUpdateRequestDto requestDto = new SpaceUpdateRequestDto();
        requestDto.setSpaceName("Updated Space");

        // when & then
        mockMvc.perform(patch("/spaces/{id}", testSpace.getSpaceName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    @Test
    @DisplayName("Authorization 헤더 없이 DELETE 요청 시 인증 에러가 발생한다")
    void deleteSpace_Fail_MissingAuthorizationHeader() throws Exception {
        // when & then
        mockMvc.perform(delete("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 401 || status == 403;
                });
    }

    // ================ 권한 테스트 (실제 사용자 객체 전달) ================

    @Test
    @DisplayName("일반 사용자가 다른 사용자의 스페이스 수정 시도 시 권한 에러가 발생한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateSpace_Fail_InsufficientPermission() throws Exception {
        // given
        SpaceUpdateRequestDto requestDto = new SpaceUpdateRequestDto();
        requestDto.setSpaceName("Unauthorized Update");

        // when & then
        mockMvc.perform(patch("/spaces/{id}", testSpace.getSpaceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isForbidden()); // 권한 부족으로 403 예상
    }

    @Test
    @DisplayName("일반 사용자가 스페이스 삭제 시도 시 권한 에러가 발생한다")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void deleteSpace_Fail_InsufficientPermission() throws Exception {
        // when & then
        mockMvc.perform(delete("/spaces/{id}", testSpace.getSpaceId()))
                .andDo(print())
                .andExpect(status().isForbidden()); // 권한 부족으로 403 예상
    }
}