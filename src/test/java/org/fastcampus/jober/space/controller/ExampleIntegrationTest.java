package org.fastcampus.jober.space.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fastcampus.jober.config.IntegrationTestWithProperties;
import org.fastcampus.jober.space.dto.request.SpaceCreateRequestDto;
import org.fastcampus.jober.space.entity.Space;
import org.fastcampus.jober.space.repository.SpaceRepository;
import org.fastcampus.jober.user.entity.Users;
import org.fastcampus.jober.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@IntegrationTestWithProperties(
    properties = {
        "spring.datasource.url=jdbc:h2:mem:spacetest",
        "logging.level.org.fastcampus.jober=DEBUG", 
        "spring.jpa.show-sql=true",
        "custom.test.property=test-value"
    },
    profiles = {"test", "integration"}
)
class ExampleIntegrationTest {

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
    private Space testSpace;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 테스트용 사용자 생성
        testUser = createTestUser("test@example.com", "testUser", "테스트 사용자");
    }

    private Users createTestUser(String email, String username, String name) {
        // Users의 정적 팩토리 메서드 사용 (실제 구현에 맞게 수정 필요)
        Users user = Users.forSignup(username, "encodedPassword", name, email);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("커스텀 프로퍼티를 사용한 통합 테스트")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void customPropertyIntegrationTest() throws Exception {
        // given
        SpaceCreateRequestDto requestDto = new SpaceCreateRequestDto();
        requestDto.setSpaceName("Test Space with Custom Properties");
        requestDto.setAdminName("테스트 관리자");
        requestDto.setAdminNum("010-1234-5678");

        // when & then
        mockMvc.perform(post("/spaces")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpected(status().isCreated());
    }
}